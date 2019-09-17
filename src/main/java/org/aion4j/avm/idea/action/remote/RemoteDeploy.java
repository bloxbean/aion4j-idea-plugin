package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.aion4j.avm.idea.action.DeployArgsHelper;
import org.aion4j.avm.idea.common.Tuple;
import org.aion4j.avm.idea.exception.DeploymentCommandCancelledException;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoteDeploy extends AvmRemoteBaseAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

//        PsiFile file =  e.getDataContext().getData(CommonDataKeys.PSI_FILE);
//        if(file != null && !"pom.xml".equals(file.getName())) {
//            e.getPresentation().setEnabled(false);
//        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        //Maven settins map
        //Map<String, String> settingMap = new HashMap<>();
        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);
        AvmConfigStateService.State state = getConfigState(project);

        if(state == null)//Null means, don't proceed
            return;

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        if(state.cleanAndBuildBeforeDeploy) {
            goals.add("clean");
            goals.add("package");
        }

        //set deploy args
        Tuple<Map<String, String>, BigInteger> deployArgs = null;
        try {
            deployArgs = DeployArgsHelper.getAndSaveDeploymentArgs(e, project, true, false);
        } catch (DeploymentCommandCancelledException ex) {
            //deployment cancelled
            return;
        }

        if(deployArgs != null) {
            if(deployArgs._1() != null)
                mavenRunnerSettings.getMavenProperties().putAll(deployArgs._1());

            if(deployArgs._2() != null)
                mavenRunnerSettings.getMavenProperties().put("value", String.valueOf(deployArgs._2()));
        }

        goals.add("aion4j:deploy");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(e, project, goals);

        mavenRunnerSettings.setSkipTests(true);

        //Start get-receipt after deploy
        if(state.getReceiptWait) {
            mavenRunnerSettings.getMavenProperties().put("wait", "true");
        }

        //Needed for build / packaging. disable optimization. only required during error/exception scenarios
        if(state.disableJarOptimization) {
            mavenRunnerSettings.getMavenProperties().put("disableJarOptimization", "true");
        }

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            //System.out.println("Deployment is successfull");
        });
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.DEPLOY_ICON;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> properties) {
        populateCredentialInfo(project, properties);
    }

}
