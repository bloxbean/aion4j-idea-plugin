package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.AvmBaseAction;
import org.aion4j.avm.idea.action.remote.ui.RemoteConfigUI;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteDeploy extends AvmBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        AvmConfigStateService configService = ServiceManager.getService(e.getProject(), AvmConfigStateService.class);

        AvmConfigStateService.State state = configService.getState();


        List<String> reqFields = new ArrayList<>();
        if(StringUtil.isEmptyOrSpaces(state.web3RpcUrl)) {
            reqFields.add("Web3 Rpc Url");
        }

        if(StringUtil.isEmptyOrSpaces(state.pk) && StringUtil.isEmptyOrSpaces(state.password)) {
            reqFields.add("Private Key / Account & Password");
        }

        RemoteConfigUI.RemoteConfigModel configDialogResponse = null;
        if(reqFields.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Please provide ");

            reqFields.stream().forEach(fl -> sb.append(fl + "  "));

            configDialogResponse = RemoteConfiguration.showAvmRemoteConfig(e.getProject(), sb.toString());

            if(configDialogResponse != null) {
                state = configService.getState();
            } else {
                return;
            }
        }

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
        mavenRunnerParameters.setPomFileName("pom.xml");

        List<String> goals = new ArrayList<>();

        if(configService.getState().cleanAndBuildBeforeDeploy) {
            goals.add("clean");
            goals.add("package");
        }
        goals.add("aion4j:deploy");
        mavenRunnerParameters.setGoals(goals);
        mavenRunnerParameters.setWorkingDirPath(project.getBasePath());

        Map<String, Boolean> profileMap = new HashMap();
        profileMap.put("remote", true);

        mavenRunnerParameters.setProfilesMap(profileMap);

        MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
        mavenRunnerSettings.setDelegateBuildToMaven(true);
        mavenRunnerSettings.setSkipTests(true);

        //Props
        Map<String, String> settingMap = new HashMap<>();
        settingMap.put("web3rpc.url", state.web3RpcUrl);


        if(configDialogResponse == null) { //store credential option
            if (!StringUtil.isEmptyOrSpaces(state.pk)) {
                settingMap.put("pk", state.pk);
            } else {
                settingMap.put("address", state.account);
                settingMap.put("password", state.password);
            }
        } else { //seems like it's nostorecredential option
            if (!StringUtil.isEmptyOrSpaces(configDialogResponse.getPk())) {
                settingMap.put("pk", configDialogResponse.getPk());
            } else {
                settingMap.put("address", configDialogResponse.getAccount());
                settingMap.put("password", configDialogResponse.getPassword());
            }
        }


        settingMap.put("address", state.account);

        mavenRunnerSettings.setMavenProperties(settingMap);
        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            System.out.println("Deployment is successfull");
        });
    }

}
