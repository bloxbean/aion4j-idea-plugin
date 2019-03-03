package org.aion4j.avm.idea.initializr;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.service.AvmService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.importing.MavenImporter;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;

import java.util.List;
import java.util.Map;

public class AvmMavenImporter extends MavenImporter {

    public AvmMavenImporter(String pluginGroupID, String pluginArtifactID) {
        super(pluginGroupID, pluginArtifactID);
    }

    @Override
    public void preProcess(Module module, MavenProject mavenProject, MavenProjectChanges changes, IdeModifiableModelsProvider modifiableModelsProvider) {

    }

    @Override
    public void process(IdeModifiableModelsProvider modifiableModelsProvider, Module module, MavenRootModelAdapter rootModel, MavenProjectsTree mavenModel, MavenProject mavenProject, MavenProjectChanges changes, Map<MavenProject, String> mavenProjectToModuleName, List<MavenProjectsProcessorTask> postTasks) {

    }
//
//    public AvmMavenImporter(String pluginGroupID, String pluginArtifactID) {
//        super(pluginGroupID, pluginArtifactID);
//    }
//
//    @Override
//    public void preProcess(Module module, MavenProject mavenProject, MavenProjectChanges changes, IdeModifiableModelsProvider modifiableModelsProvider) {
//
//        AvmService service = ServiceManager.getService(module.getProject(), AvmService.class);
//        if(isApplicable(mavenProject)) {
//            System.out.println(">>>>>> This is a avm project >>>");
//            service.init(mavenProject);
//            service.setIsAvmProject(true);
//        } else {
//            System.out.println(">>>>>>>> This is not a avm project >>>>>>>>>>");
//        }
//
//    }
//
//    @Override
//    public void process(IdeModifiableModelsProvider modifiableModelsProvider,
//                        Module module, MavenRootModelAdapter rootModel, MavenProjectsTree mavenModel, MavenProject mavenProject,
//                        MavenProjectChanges changes, Map<MavenProject, String> mavenProjectToModuleName, List<MavenProjectsProcessorTask> postTasks) {
//
//        AvmService service = ServiceManager.getService(module.getProject(), AvmService.class);
//        if(isApplicable(mavenProject)) {
//            System.out.println(">>>>>> This is a avm project >>>");
//            service.init(mavenProject);
//            service.setIsAvmProject(true);
//        } else {
//            System.out.println(">>>>>>>> This is not a avm project >>>>>>>>>>");
//        }
//    }
//
//    @Override
//    public boolean isApplicable(MavenProject mavenProject) {
//        return mavenProject.findPlugin("org.aion4j", "aion4j-maven-plugin") != null;
//    }


}
