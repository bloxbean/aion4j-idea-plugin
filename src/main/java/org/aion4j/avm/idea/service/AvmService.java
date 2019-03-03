package org.aion4j.avm.idea.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProject;

public interface AvmService {

    public void init(Project project);

    public boolean isInitialize();

    public void setIsAvmProject(boolean flag);

    public boolean isAvmProject();

    public boolean isClassAllowed(Project project, String clazz);

    public boolean isUnderTestSource(VirtualFile file);
}
