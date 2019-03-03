package org.aion4j.avm.idea.service;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import org.aion4j.avm.idea.AvmDetails;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.ArrayList;
import java.util.List;

public class AvmServiceImpl implements AvmService {

    private final static String AION4j_MAVEN_PLUGIN = "aion4j-maven-plugin";
    private static final Logger log = Logger.getInstance(AvmServiceImpl.class);

    private boolean isInitialized;
    private boolean isAvmProject;

    private List<String> testFolders;
    private List<String> sourceFolders;

    public AvmServiceImpl() {

    }

    @Override
    public void init(Project project) {

        this.isInitialized = true;

        MavenProjectsManager mvnProjectManager = MavenProjectsManager.getInstance(project);
        if(!mvnProjectManager.isMavenizedProject()) {
            return;
        }

        List<MavenProject> rootProjects = mvnProjectManager.getRootProjects();
        if(rootProjects != null && rootProjects.size() > 0) {
            MavenPlugin mavenPlugin = rootProjects.get(0).findPlugin("org.aion4j", AION4j_MAVEN_PLUGIN);
            if(mavenPlugin == null) {
                debug(() -> log.debug(">>>>>> Not a avm project"));;
                return;
            } else {
                debug(() -> log.debug(">>>>>> It's a avm project"));
                isAvmProject = true;
            }
        } else {
            System.out.println("<<<<<<<<< No root project found");
            return;
        }

        List<MavenProject> mavenProjects = MavenProjectsManager.getInstance(project).getProjects();

        if(mavenProjects != null && mavenProjects.size() > 0) {
            this.testFolders = new ArrayList<>();
            this.sourceFolders = new ArrayList<>();
            for (MavenProject mvnProject : mavenProjects) {
                List<String> testSources = mvnProject.getTestSources();
                List<String> sources = mvnProject.getSources();

                if(testSources != null && testSources.size() > 0) {
                    this.testFolders.addAll(testSources);
                }

                if(sources != null && sources.size() > 0) {
                    this.sourceFolders.addAll(sources);
                }
            }
        } else {
            return;
        }

        if(log.isDebugEnabled()) {
            debug(() -> log.debug(">>>>>> Test source folders: " + testFolders));
        }

       /* if(true)
        return;

        VirtualFile ideaFolder
                = project.getProjectFile().getParent();
        if(ideaFolder == null)
            return;

        VirtualFile rootFolder = ideaFolder.getParent();
        if(rootFolder == null)
            return;

        VirtualFile pomXml = rootFolder.findFileByRelativePath("pom.xml");

        try {
            if (pomXml.exists()) {
                String content = new String(pomXml.contentsToByteArray(), "UTF-8");

                if(content.contains(AION4j_MAVEN_PLUGIN)) {
                    debug(() -> log.debug("This is a AVM project >>>>>>>>>>>>>>>>>"));
                    this.isAvmProject = true;
                } else {
                    debug(() -> log.debug("This is not a AVM project >>>>>>>>>>>>>>>>"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean isInitialize() {
        return isInitialized;
    }

    @Override
    public void setIsAvmProject(boolean flag) {
        this.isAvmProject = flag;
    }

    @Override
    public boolean isAvmProject() {
        return isAvmProject;
    }

    @Override
    public boolean isClassAllowed(Project project, String clazzName) {
        if(!isAvmProject)
            return true;

        Class clazz = null;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            //debug(() -> log.debug(e.getMessage()));
            return true; //don't need to highlight it as it will be done by the editor.
        }

        AvmDetails.MethodDescriptor[] methodDescriptors = AvmDetails.getClassLibraryWhiteList().get(clazz);

        if(methodDescriptors !=  null)
            return true;
        else {
            if(clazzName.startsWith("java.") || clazzName.startsWith("javax.") || clazzName.startsWith("org.xml"))
                return false;
            else
                return true;
        }
    }

    @Override
    public boolean isUnderTestSource(VirtualFile file) {

        String filePath =file.getCanonicalPath();

        if(testFolders != null) {
            for (String testFolder : testFolders) {
                if (filePath.contains(testFolder)) {
                    return true;
                }
            }
        } else {
            return false;
        }

        return false;
    }

    private void debug(Runnable doWhenDebug) {
        if (log.isDebugEnabled()) {
            doWhenDebug.run();
        }
    }
}
