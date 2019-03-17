package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.aion4j.avm.idea.service.AvmService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AvmBaseAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {

        AvmService avmService = ServiceManager.getService(AvmService.class);

        if(avmService != null && avmService.isAvmProject()) {
            e.getPresentation().setEnabledAndVisible(true);
        }


        if(getIcon() != null) {
            e.getPresentation().setIcon(getIcon());
        }
    }

    protected MavenRunnerParameters getMavenRunnerParameters(Project project, List<String> goals) {

        AvmConfigStateService avmConfigStateService = ServiceManager.getService(project, AvmConfigStateService.class);

        MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
        mavenRunnerParameters.setPomFileName("pom.xml");

        mavenRunnerParameters.setGoals(goals);
        mavenRunnerParameters.setWorkingDirPath(project.getBasePath());

        Map<String, Boolean> profileMap = new HashMap();

        if(isRemote()) {
            if(avmConfigStateService.getState() == null
                    || StringUtil.isEmptyOrSpaces(avmConfigStateService.getState().mvnProfile)) {
                profileMap.put("remote", true);
            } else {
                profileMap.put(avmConfigStateService.getState().mvnProfile, true);
            }
        } else {
            profileMap.put("default", true);
        }

        mavenRunnerParameters.setProfilesMap(profileMap);

        return mavenRunnerParameters;

    }

    protected MavenRunnerSettings getMavenRunnerSettings() {
        MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
        mavenRunnerSettings.setDelegateBuildToMaven(true);

        return mavenRunnerSettings;
    }

    public Icon getIcon() {
        return null;
    }

    protected abstract boolean isRemote();
}
