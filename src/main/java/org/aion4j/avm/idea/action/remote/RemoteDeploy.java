package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.AvmBaseAction;
import org.aion4j.avm.idea.action.remote.ui.RemoteConfigUI;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteDeploy extends AvmRemoteBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        //Maven settins map
        Map<String, String> settingMap = new HashMap<>();
        AvmConfigStateService.State state = populateCredentialInfo(project, settingMap);

        if(state == null)//Null means, don't proceed
            return;

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        if(state.cleanAndBuildBeforeDeploy) {
            goals.add("clean");
            goals.add("package");
        }

        goals.add("aion4j:deploy");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, goals);

        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings();
        mavenRunnerSettings.setSkipTests(true);
        mavenRunnerSettings.setMavenProperties(settingMap);

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            System.out.println("Deployment is successfull");
        });
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.DEPLOY_ICON;
    }

}
