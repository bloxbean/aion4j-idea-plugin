package org.aion4j.avm.idea.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

public interface AvmService {

    public void init(Project project);

    public boolean isInitialize();

    public void setIsAvmProject(boolean flag);

    public boolean isAvmProject();

    public boolean isClassAllowed(Project project, String clazz);

    public List<MethodDescriptor> getAllowedMethodsForClass(Project project, String clazz, String methodName);

    public boolean isUnderTestSource(VirtualFile file);

    public void resetJCLClassInitialization();
}
