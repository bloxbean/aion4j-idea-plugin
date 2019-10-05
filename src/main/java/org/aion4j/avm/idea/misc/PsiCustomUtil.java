package org.aion4j.avm.idea.misc;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
                    } else if(!(element instanceof PsiFile)) { //For other scenarios like xml file etc.
                        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

                        if(psiFile != null) element = psiFile;
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

    public static String getContractMainClass(MavenProject mavenProject) {
        if(mavenProject == null) return null;

        try {
            Element element = mavenProject.getPluginConfiguration("org.apache.maven.plugins", "maven-jar-plugin");

            if (element == null) return null;
            Element archiveElm = element.getChild("archive");
            if (archiveElm == null) return null;

            Element manifestElm = archiveElm.getChild("manifest");
            if (manifestElm == null) return null;

            Element mainClassElm = manifestElm.getChild("mainClass");
            if (mainClassElm == null) return null;

            String mainClass = mainClassElm.getText();

            if (StringUtil.isEmpty(mainClass)) return null;
            //check if it's a property
            if (mainClass.length() <= 4) return null;

            if (mainClass.trim().startsWith("${")) {
                mainClass = mainClass.trim().substring(2, -1);

                System.out.println("Main Class property >>>>> " + mainClass);
                //Now get the mainClass property value
                return mavenProject.getProperties().getProperty(mainClass);
            } else {
                return mainClass;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getWorkingDirFromActionEvent(AnActionEvent e, Project project) {
        //Get module if possible incase of multi module maven projects
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
                    module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(element.getContainingFile().getVirtualFile());
                }
            }
        } catch (Error ex) {

        }

        MavenProjectsManager mvnProjectManager = MavenProjectsManager.getInstance(project);
        if(module != null && mvnProjectManager.getProjects().size() > 1) { //more than root project
            MavenProject moduleProject = mvnProjectManager.findProject(module);
            return moduleProject.getDirectory();
        } else {
            return project.getBasePath();
        }
    }

}
