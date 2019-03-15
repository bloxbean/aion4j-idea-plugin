package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.AvmBaseAction;
import org.aion4j.avm.idea.action.remote.ui.RemoteConfigUI;
import org.aion4j.avm.idea.action.remote.ui.TransferDialog;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferAction extends AvmBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        AvmConfigStateService configService = ServiceManager.getService(e.getProject(), AvmConfigStateService.class);

        AvmConfigStateService.State state = configService.getState();
        if(StringUtil.isEmptyOrSpaces(state.web3RpcUrl)) {
            RemoteConfigUI.RemoteConfigModel configDialogResponse = null;
            configDialogResponse = RemoteConfiguration.showAvmRemoteConfig(e.getProject(), "Please provide Web3 Rpc Url");

            if(configDialogResponse != null) {
                state = configService.getState();
            } else {
                return;
            }
        }

        TransferDialog dialog = new TransferDialog(project);

        boolean result = dialog.showAndGet();
        if(result) {

            MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

            MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
            mavenRunnerParameters.setPomFileName("pom.xml");

            List<String> goals = new ArrayList<>();

            goals.add("aion4j:transfer");
            mavenRunnerParameters.setGoals(goals);
            mavenRunnerParameters.setWorkingDirPath(project.getBasePath());

            Map<String, Boolean> profileMap = new HashMap();
            profileMap.put("remote", true);

            mavenRunnerParameters.setProfilesMap(profileMap);

            MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
            mavenRunnerSettings.setDelegateBuildToMaven(true);

            //Props
            Map<String, String> settingMap = new HashMap<>();
            settingMap.put("web3rpc.url", state.web3RpcUrl);

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
}
