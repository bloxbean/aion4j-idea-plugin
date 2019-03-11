package org.aion4j.avm.idea.service;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AvmServiceImpl implements AvmService {

    private final static String AION4j_MAVEN_PLUGIN = "aion4j-maven-plugin";
    private static final Logger log = Logger.getInstance(AvmServiceImpl.class);

    private boolean isInitialized;
    private boolean isMavenProject;
    private boolean isAvmProject;

    private List<String> testFolders;
    private List<String> sourceFolders;

    private boolean isJCLClassInitializationDone = false;
//    private Map<String, Map<String, List<MethodDescriptor>>> jclWhitelist;

    private JCLWhitelistHolder whitelistHolder;

    public AvmServiceImpl() {
        this.whitelistHolder = new JCLWhitelistHolder();
    }

    @Override
    public synchronized void init(Project project) {

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
                //attachProjectListener(mvnProjectManager);
                return;
            } else {
                debug(() -> log.debug(">>>>>> It's a avm project"));
                isAvmProject = true;
            }
        } else {
            if(log.isDebugEnabled()) {
                log.debug("<<<<<<<<< No root project found");
            }
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

//    private void attachProjectListener(MavenProjectsManager projectsManager) {
//
//        projectsManager.addManagerListener(new MavenProjectsManager.Listener() {
//            @Override
//            public void activated() {
//
//            }
//
//            @Override
//            public void projectsScheduled() {
//
//            }
//
//            @Override
//            public void importAndResolveScheduled() {
//                System.out.println("Scheduled for resolver..........");
//
//            }
//        });
//    }

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

        if(!isJCLClassInitializationDone)
            getJCLListFromProjectLibrary(project);

        if(whitelistHolder.size() == 0)
            return true;

        if(whitelistHolder.isClassPresent(clazzName))
            return true;
        else {
            if(clazzName.startsWith("java.") || clazzName.startsWith("javax.") || clazzName.startsWith("org.xml"))
                return false;
            else
                return true;
        }
    }

    @Override
    @NotNull
    public List<MethodDescriptor> getAllowedMethodsForClass(Project project, String clazz, String methodName) {
        if(!isAvmProject)
            return Collections.EMPTY_LIST;

        if(!isJCLClassInitializationDone)
            getJCLListFromProjectLibrary(project);

        return whitelistHolder.getMethods(clazz, methodName);

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

    private void getJCLListFromProjectLibrary(Project project) {
        String homePath = ProjectRootManager.getInstance(project).getProjectSdk().getHomePath();
        File pluginPath = PluginManager.getPlugin(PluginId.getId("org.aion4j.avm")).getPath();


        if(log.isDebugEnabled()) {
            log.info("SDK Home path: >>>>>>>>>  " + homePath);
            log.debug("Plugin Path >>>> " + pluginPath);
        }

        copyFile("/AvmDetailsGetter.class", pluginPath.getAbsolutePath());

        ArrayList<String> cmds = new ArrayList<>();
        cmds.add(homePath + File.separator + "bin/java");
        cmds.add("-cp");
        cmds.add("lib" + File.separatorChar + "*");
        cmds.add("AvmDetailsGetter");

        GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmds);
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.setWorkDirectory(pluginPath);

        ProcessHandler processHandler = null;
        try {
            processHandler = new OSProcessHandler(generalCommandLine);

            processHandler.startNotify();
            processHandler.addProcessListener(new ProcessAdapter() {

                StringBuffer sb = new StringBuffer();
                @Override
                public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                    sb.append(event.getText());
                }

                @Override
                public void processTerminated(@NotNull ProcessEvent event) {

                    String jsonStr = sb.toString();

                    whitelistHolder.init(project, jsonStr);
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        whitelistHolder.loadFromCache(project);

        isJCLClassInitializationDone = true;
    }

    private String copyFile(String fileName, String destFolder) {

        InputStream fileStream = null;
        OutputStream out = null;
        try {
            // Read the file we're looking for
            fileStream = this.getClass().getResourceAsStream(fileName);

            if (fileStream == null) {
                throw new RuntimeException(String.format("%s is not found in the plugin jar. ", fileName));
            }

            File targetFile
                    = new File(destFolder, fileName);

            out = new FileOutputStream(targetFile);

            // Write the file to the temp file
            byte[] buffer = new byte[1024];
            int len = fileStream.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = fileStream.read(buffer);
            }

            // Return the path of this sweet new file
            return targetFile.getAbsolutePath();

        } catch (IOException e) {
            throw new RuntimeException("Error copying " + fileName + "to " + destFolder, e);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void debug(Runnable doWhenDebug) {
        if (log.isDebugEnabled()) {
            doWhenDebug.run();
        }
    }
}
