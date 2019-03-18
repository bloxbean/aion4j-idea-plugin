package org.aion4j.avm.idea.action.local;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.AvmBaseAction;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
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

public class LocalDeployAction extends AvmBaseAction {

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        List<String> goals = new ArrayList<>();
        goals.add("clean");
        goals.add("package");
        goals.add("aion4j:deploy");

        Map<String, String> settingMap = new HashMap<>();
        if(state != null && !StringUtil.isEmptyOrSpaces(state.deployArgs)) {
            settingMap.put("args", state.deployArgs);
        }

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, goals);

        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings();
        mavenRunnerSettings.setSkipTests(true);
        mavenRunnerSettings.setMavenProperties(settingMap);

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            IdeaUtil.showNotification(project, "Deployment", "Contract deployed successfully",
                    NotificationType.INFORMATION, null);
        });
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.DEPLOY_ICON;
    }
}
