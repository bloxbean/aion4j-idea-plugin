package org.aion4j.avm.idea.service;

import com.intellij.openapi.project.Project;

public interface AvmService {

    public void init(Project project);

    public boolean isAvmProject();

    public boolean isClassAllowed(String clazz);
}
