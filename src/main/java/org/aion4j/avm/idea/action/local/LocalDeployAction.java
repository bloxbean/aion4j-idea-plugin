package org.aion4j.avm.idea.action.local;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.aion4j.avm.idea.action.DeployArgsHelper;
import org.aion4j.avm.idea.action.local.ui.LocalGetAccountDialog;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.misc.PsiCustomUtil;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenProject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalDeployAction extends AvmLocalBaseAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        PsiFile file =  e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if(file != null && !"pom.xml".equals(file.getName())) {
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);
        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);
        mavenRunnerSettings.setSkipTests(true);

        List<String> goals = new ArrayList<>();
        goals.add("clean");
        goals.add("package");

        //Only try to auto create  account when storage path is default (target folder) and custom account is select. Just to avoid error
        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);
        if(configService != null) {
            AvmConfigStateService.State state = configService.getState();
            if(state != null && StringUtil.isEmptyOrSpaces(state.avmStoragePath)) {
                if(state.shouldAskCallerAccountEverytime || !StringUtil.isEmptyOrSpaces(state.localDefaultAccount)) { //Custom account .. so lets create and give some balance.
                    goals.add("aion4j:create-account");
                    mavenRunnerSettings.getMavenProperties().put("balance", "100000000000000");
                }
            }
        }

        //set deploy args
        Map<String, String> deployArgs = DeployArgsHelper.getAndSaveDeploymentArgs(e, project, true);
        if(deployArgs != null)
            mavenRunnerSettings.getMavenProperties().putAll(deployArgs);

        goals.add("aion4j:deploy");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(e, project, goals);


        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            IdeaUtil.showNotification(project, "Deployment", "Contract deployed successfully",
                    NotificationType.INFORMATION, null);
        });
    }
    @Override
    protected void configureAVMProperties(@NotNull Project project, @NotNull Map<String, String> settingMap) {
        super.configureAVMProperties(project, settingMap);

        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        if(state.shouldAskCallerAccountEverytime) {
            String inputAccount = getInputDeployerAccount(project);

            if(!StringUtil.isEmptyOrSpaces(inputAccount)) {
                settingMap.put("address", inputAccount.trim());
            }
        } else {
            if (!StringUtil.isEmptyOrSpaces(state.localDefaultAccount)) {
                settingMap.put("address", state.localDefaultAccount);
            }
        }
    }

    private String getInputDeployerAccount(Project project) {
        LocalGetAccountDialog dialog = new LocalGetAccountDialog(project);
        boolean result = dialog.showAndGet();

        if(!result) {
            return null;
        }

        return dialog.getAccount();
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.DEPLOY_ICON;
    }
}
