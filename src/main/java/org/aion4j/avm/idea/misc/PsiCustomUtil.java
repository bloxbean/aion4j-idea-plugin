package org.aion4j.avm.idea.misc;

import com.intellij.execution.PsiLocation;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public class PsiCustomUtil {

    @Nullable
    public static VirtualFile findFileUnderRootInModule(Module module, String targetFileName) {
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        for (VirtualFile contentRoot : contentRoots) {
            VirtualFile childFile = findFileUnderRootInModule(contentRoot, targetFileName);
            if (childFile != null) {
                return childFile;
            }
        }
        return null;
    }

    @Nullable
    public static VirtualFile findFileUnderRootInModule(@NotNull VirtualFile contentRoot,
                                                        String targetFileName) {
        VirtualFile childFile = contentRoot.findChild(targetFileName);
        if (childFile != null) {
            return childFile;
        }
        return null;
    }

    public static Module getModuleFromAction(Project project, AnActionEvent e) {
        Module module = null;

        try {
            PsiElement element = null;

            if(element == null)
                element = e.getData(CommonDataKeys.PSI_ELEMENT);

            if(element == null)
                element = e.getData(CommonDataKeys.PSI_FILE);

            if(element != null) {
                if(element instanceof PsiDirectory) {
                    module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(((PsiDirectory) element).getVirtualFile());
                } else {
                    if(element instanceof PsiClass) { //For editor context menu
                        //If it's class, it's better to get the PSI_FILE for safer side. Otherwise it may return stdlib class like String etc based on cursor location

                        element = e.getData(CommonDataKeys.PSI_FILE);
                        if(element == null) return null;
                    }

                    module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(element.getContainingFile().getVirtualFile());
                }
            }
        } catch (Error ex) {

        }

        return module;
    }

    public static String getMavenProjectName(Project project, AnActionEvent e) {
        Module module = getModuleFromAction(project, e);

        if(module == null)
            return null;

        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);

        if(manager == null) return module.getName();

        MavenProject mavenProject = manager.findProject(module);

        if(mavenProject == null) return module.getName();
        else
            return mavenProject.getMavenId().getArtifactId();
    }

    public static MavenProject getMavenProject(Project project, AnActionEvent e) {
        Module module = getModuleFromAction(project, e);

        if(module == null)
            return null;

        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);

        if(manager == null) return null;

        MavenProject mavenProject = manager.findProject(module);
        return mavenProject;
    }

    public static String getMavenProjectName(MavenProject mavenProject) {
        if(mavenProject == null) return null;
        else
            return mavenProject.getMavenId().getArtifactId();
    }
}
