package org.aion4j.avm.idea.action.local;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.local.ui.LocalGetAccountDialog;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalDeployAction extends AvmLocalBaseAction {

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        goals.add("clean");
        goals.add("package");
        goals.add("aion4j:deploy");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, goals);

        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);
        mavenRunnerSettings.setSkipTests(true);

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

        if(!StringUtil.isEmptyOrSpaces(state.deployArgs))
            settingMap.put("args", state.deployArgs);

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
