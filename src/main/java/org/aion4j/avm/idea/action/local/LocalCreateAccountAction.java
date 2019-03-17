package org.aion4j.avm.idea.action.local;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.AvmBaseAction;
import org.aion4j.avm.idea.action.local.ui.LocalCreateAccountDialog;
import org.aion4j.avm.idea.action.local.ui.LocalGetAccountDialog;
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

public class LocalCreateAccountAction extends AvmBaseAction {

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();

        LocalCreateAccountDialog dialog = new LocalCreateAccountDialog(project);
        boolean result = dialog.showAndGet();

        if(!result) {
            return;
        }

        String account = dialog.getAccount();
        long balance = dialog.getBalance();

        if(StringUtil.isEmptyOrSpaces(account)) {
            IdeaUtil.showNotification(project, "Create account failed", "Account can't be empty or null.",
                    NotificationType.ERROR, null);
            return;
        }

        Map<String, String> settingMap = new HashMap<>();
        settingMap.put("address", account);
        settingMap.put("balance", String.valueOf(balance));

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        goals.add("aion4j:create-account");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, goals);
        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings();

        mavenRunnerSettings.setMavenProperties(settingMap);

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            IdeaUtil.showNotification(project, "Account creation", "Account created successfully",
                    NotificationType.INFORMATION, null);
        });
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.CONFIG_ICON;
    }
}
