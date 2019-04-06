package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.service.AvmService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InitializationAction extends AnAction { //This is called initially during project creation

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        initializeProject(e);
    }

    public static void initializeProject(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        if(project == null)
            return;

        File mvnwFile = new File(project.getBasePath() + File.separator + "mvnw");

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();

        if(!mvnwFile.exists())
            goals.add("io.takari:maven:0.7.4:wrapper");

        goals.add("aion4j:init");

        MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
        mavenRunnerParameters.setPomFileName("pom.xml");

        mavenRunnerParameters.setGoals(goals);
        mavenRunnerParameters.setWorkingDirPath(project.getBasePath());

        MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
        mavenRunnerSettings.setDelegateBuildToMaven(true);

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            //reset JCLWhitelist..
            AvmService avmService = ServiceManager.getService(e.getProject(), AvmService.class);

            if(avmService != null) {
                avmService.resetJCLClassInitialization();
            }

        });
    }
}
