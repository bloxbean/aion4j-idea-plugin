package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.remote.ui.TransferDialog;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferAction extends AvmRemoteBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        Map<String, String> settingMap = new HashMap<>();
        populateKernelInfo(project, settingMap);

        TransferDialog dialog = new TransferDialog(project);

        boolean result = dialog.showAndGet();
        if(result) {

            MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

            List<String> goals = new ArrayList<>();
            goals.add("aion4j:transfer");

            MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, goals);
            MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings();

            if (!StringUtil.isEmptyOrSpaces(dialog.getPrivateKey())) {
                settingMap.put("pk", dialog.getPrivateKey());
            } else {
                settingMap.put("from", dialog.getFromAccount());
                settingMap.put("password", dialog.getPassword());
            }

            settingMap.put("to", dialog.getToAccount());
            settingMap.put("value", dialog.getValue());

            settingMap.put("gas", dialog.getNrg());
            settingMap.put("gasPrice", dialog.getNrgPrice());

            mavenRunnerSettings.setMavenProperties(settingMap);

            mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {

            });
        }
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.TRANSFER_ICON;
    }
}
