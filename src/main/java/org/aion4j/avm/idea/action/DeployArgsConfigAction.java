package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.PsiCustomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import javax.swing.*;
import java.util.Map;

public class DeployArgsConfigAction extends AvmBaseAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        PsiFile file =  e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if(file != null) {
            e.getPresentation().setEnabled(false);
        }

        MavenProject mavenProject = PsiCustomUtil.getMavenProject(e.getProject(), e);
        if(mavenProject != null && mavenProject.isAggregator()) {
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        DeployArgsHelper.getAndSaveDeploymentArgs(e, e.getProject(), false, true);
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.CONFIG_ICON;
    }

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> properties) {

    }
}
