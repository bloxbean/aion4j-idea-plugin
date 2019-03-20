package org.aion4j.avm.idea.action.local;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.action.AvmBaseAction;
import org.aion4j.avm.idea.action.remote.GetBalanceAction;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalGetBalanceAction extends AvmLocalBaseAction {

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        goals.add("aion4j:get-balance");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, goals);
        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            IdeaUtil.showNotification(project, "Get Balance call", "Balance fetched successfully",
                    NotificationType.INFORMATION, null);
        });
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.BALANCE_ICON;
    }
}
