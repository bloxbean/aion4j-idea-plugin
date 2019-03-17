package org.aion4j.avm.idea.action.remote;

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

public class GetReceiptAction extends AvmRemoteBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        Map<String, String> settingMap = new HashMap<>();
        AvmConfigStateService.State state = populateKernelInfo(project, settingMap);

        if(state == null || StringUtil.isEmptyOrSpaces(state.web3RpcUrl)) {
            IdeaUtil.showNotification(project, "Get Receipt failed", "Please configure remote kernel first.",
                    NotificationType.ERROR, IdeaUtil.AVM_REMOTE_CONFIG_ACTION);
            return;
        }

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        goals.add("aion4j:get-receipt");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, goals);
        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings();

        //command args
        String txHash = getTxHash(project);
        if(txHash != null) {
            settingMap.put("txHash", txHash);
        }

        mavenRunnerSettings.setMavenProperties(settingMap);
        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {

        });
    }

    public String getTxHash(Project project) {
        return null;
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.GETRECEIPT_ICON;
    }
}
