package org.aion4j.avm.idea.service;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.aion4j.avm.idea.AvmDetails;

public class AvmServiceImpl implements AvmService {

    private final static String AION4j_MAVEN_PLUGIN = "aion4j-maven-plugin";
    private static final Logger log = Logger.getInstance(AvmServiceImpl.class);

    private boolean isAvmProject;

    public AvmServiceImpl() {

    }

    @Override
    public void init(Project project) {

        VirtualFile ideaFolder = project.getProjectFile().getParent();
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
        }
    }

    @Override
    public boolean isAvmProject() {
        return isAvmProject;
    }

    @Override
    public boolean isClassAllowed(String clazzName) {
        if(!isAvmProject)
            return true;

        Class clazz = null;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        AvmDetails.MethodDescriptor[] methodDescriptors = AvmDetails.getClassLibraryWhiteList().get(clazz);

        if(methodDescriptors !=  null)
            return true;
        else
            return false;
    }

    private void debug(Runnable doWhenDebug) {
        if (log.isDebugEnabled()) {
            doWhenDebug.run();
        }
    }
}
