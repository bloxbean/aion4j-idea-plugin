package org.aion4j.avm.idea.action.local;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import java.util.ArrayList;
import java.util.List;

public class LocalDeployAction extends AnAction {

    public LocalDeployAction() {
        super("Deploy");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
//        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
//        boolean visible = file != null && file.getName().equals("pom.xml");
        e.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
        mavenRunnerParameters.setPomFileName("pom.xml");

        List<String> goals = new ArrayList<>();
        goals.add("clean");
        goals.add("package");
        goals.add("aion4j:deploy");
        mavenRunnerParameters.setGoals(goals);
        mavenRunnerParameters.setWorkingDirPath(project.getBasePath());

        MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
        mavenRunnerSettings.setDelegateBuildToMaven(true);

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            System.out.println("Deployment is successfull");
        });
    }
}
