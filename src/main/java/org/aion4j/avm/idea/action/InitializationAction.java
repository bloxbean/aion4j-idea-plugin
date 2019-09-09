package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
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
            goals.add("io.takari:maven:0.7.6:wrapper");

        goals.add("initialize");

        MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
        mavenRunnerParameters.setPomFileName("pom.xml");

        mavenRunnerParameters.setGoals(goals);
        mavenRunnerParameters.setWorkingDirPath(project.getBasePath());

        MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
        mavenRunnerSettings.setDelegateBuildToMaven(true);

        //Check if lib folder is there and ask user if overwrite or not. It just checks lib folder under project root.
        File avmJar = new File(project.getBasePath() + File.separator + "lib" + File.separator + "avm.jar");
        if(avmJar.exists()) { //Prompt to overwrite
            int ret = Messages.showOkCancelDialog(project, "Do you want to overwrite existing avm jar files ?",
                    "Avm Initializer", Messages.getQuestionIcon());

            if(ret != Messages.OK) {
                return;
            }
            mavenRunnerSettings.getMavenProperties().put("forceCopy", "true");
        }

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            //reset JCLWhitelist..
            AvmService avmService = ServiceManager.getService(e.getProject(), AvmService.class);

            if(avmService != null) {
                avmService.resetJCLClassInitialization();
            }

        });
    }
}
