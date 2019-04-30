package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.service.AvmService;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

public class AvmActionGroup extends DefaultActionGroup {

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();

        boolean mavenContext = MavenActionUtil.hasProject(event.getDataContext())
                && !MavenActionUtil.getMavenProjects(event.getDataContext()).isEmpty();

        if(project == null)
            return;

        AvmService avmService = ServiceManager.getService(project, AvmService.class);

        if(avmService != null && mavenContext && !avmService.isInitialize()) { //check if avmService is initialized or not
            avmService.init(project);
        }

        if(avmService == null || !avmService.isAvmProject()) {
            event.getPresentation().setEnabledAndVisible(false);

            return;
        }

        Editor editor = event.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        if(!mavenContext && editor != null && psiFile != null && psiFile instanceof PsiJavaFile) {
            if(!avmService.isUnderTestSource(psiFile.getVirtualFile())) {
                mavenContext = true;
            }
        }

        if(avmService.isAvmProject() && mavenContext) {
            event.getPresentation().setVisible(true);
            event.getPresentation().setIcon(AvmIcons.AION_ICON);
        } else {
            event.getPresentation().setVisible(false);
            event.getPresentation().setIcon(AvmIcons.AION_ICON);
        }
    }
}
