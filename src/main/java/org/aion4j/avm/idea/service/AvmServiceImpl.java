package org.aion4j.avm.idea.service;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.apache.commons.lang3.SystemUtils;
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

    public void resetJCLClassInitialization() {
        this.isJCLClassInitializationDone = false;
    }

    private void getJCLListFromProjectLibrary(Project project) {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();

        if(sdk == null) {
            return;
        }

        String homePath = sdk.getHomePath();
        File pluginPath = PluginManager.getPlugin(PluginId.getId("org.aion4j.avm")).getPath();


        if(log.isDebugEnabled()) {
            log.info("SDK Home path: >>>>>>>>>  " + homePath);
            log.debug("Plugin Path >>>> " + pluginPath);
        }

        copyFile("/AvmDetailsGetter.class", pluginPath.getAbsolutePath());

        ArrayList<String> cmds = new ArrayList<>();
        cmds.add(homePath + File.separator + "bin/java");
        cmds.add("-cp");
       // cmds.add("lib" + File.separatorChar + "*");
        cmds.add(buildClasspathForAvmDetails(project));
        cmds.add("-Dfile.encoding=UTF8");
        cmds.add("AvmDetailsGetter");
        cmds.add(JCLWhitelistHolder.getSourceFilePath(project));

        GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmds);
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.setWorkDirectory(pluginPath);

        if(log.isDebugEnabled()) {
            log.debug("Trying to execute " + generalCommandLine.getCommandLineString());
        }

        ProcessHandler processHandler = null;
        try {
            processHandler = new OSProcessHandler(generalCommandLine);

            processHandler.startNotify();
            processHandler.addProcessListener(new ProcessAdapter() {

                @Override
                public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                }

                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    try {
                        whitelistHolder.init(project);
                    } catch (Exception e) {
                        log.error("Error parsing JCLWhitelist json string >>> ", e);
                        throw e;
                    }
                }
            });
        } catch (ExecutionException e) {
            log.error(e);
            IdeaUtil.showNotification(project, "JCL Whitelist cache", "Error getting JCL whitelist data for AVM", NotificationType.ERROR, null);
        }

        whitelistHolder.loadFromCache(project);

        isJCLClassInitializationDone = true;
    }

    private String buildClasspathForAvmDetails(Project project) {
        //check if there is a lib folder in project. Ignore, if avmLib in maven project has anyother value for now.
        String basePath = project.getBasePath();
        File libAvmJar = new File(basePath + File.separatorChar + "lib" + File.separatorChar + "avm.jar");

        String avmJarPath = null;
        if(libAvmJar.exists()) {
            if(log.isDebugEnabled())
                log.debug("Lib avm.jar exists in project.. Use project's avm.jar");

            avmJarPath = libAvmJar.getAbsolutePath();
        } else {
            if(log.isDebugEnabled())
                log.debug("Use default avm.jar from plugin.......");

            avmJarPath = "lib" + File.separatorChar + "avm.jar";
        }

        char cpSeparator = ':';

        if(SystemUtils.IS_OS_WINDOWS)
            cpSeparator = ';';

        StringBuilder sb = new StringBuilder();
        sb.append(".");
        sb.append(cpSeparator);
        sb.append(avmJarPath);
        sb.append(cpSeparator);
        sb.append("lib" + File.separatorChar + "minimal-json-0.9.5.jar");

        if(log.isDebugEnabled())
            log.debug("AVM classpath for AVMDetails " + sb.toString());

        return sb.toString();
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
