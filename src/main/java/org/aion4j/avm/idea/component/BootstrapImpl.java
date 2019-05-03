package org.aion4j.avm.idea.component;

import com.intellij.ProjectTopics;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.aion4j.avm.idea.action.InitializationAction;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.service.AvmService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.File;
import java.util.Set;

public class BootstrapImpl implements Bootstrap, ProjectComponent {

  private static final Logger log = Logger.getInstance(BootstrapImpl.class);

  private final Project project;
  private MessageBusConnection connection;

  public BootstrapImpl(Project project) {
    this.project = project;
  }

  @NotNull
  @Override
  public String getComponentName() {
    return "Avm Project";
  }

  @Override
  public void projectOpened() {

    debug(() -> log.debug("Project " + project.getName() + " is opened, checking if it's avm project"));
   // service.init(project);

    String basePath = this.project.getBasePath();

    if(connection == null)
      connection = project.getMessageBus().connect();

    connection.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
      @Override
      public void rootsChanged(ModuleRootEvent event) {

        if(log.isDebugEnabled())
          log.debug("Something changed in the project >>>>>>>>>");

        MavenProjectsManager mvnProjectManager = MavenProjectsManager.getInstance(project);
        if(!mvnProjectManager.isMavenizedProject()) {
          return;
        }

        AvmService service = ServiceManager.getService(project, AvmService.class);

        if(service.isInitialize() && !service.isAvmProject()) {

            if(log.isDebugEnabled()) {
              log.debug("Let's check if it has become a AVM project >>>>");
            }

            service.init(project);

            if(service.isAvmProject()) { //hey I am avm project. Let's check if everything initialized properly or not
              String aion4jVersion = mvnProjectManager.getRootProjects().get(0).getProperties().getProperty("aion4j.plugin.version");

              if(!"x.x.x".equals(aion4jVersion)) {
                String basePath = project.getBasePath();
                File libFolder = new File(basePath + File.separator + "lib");

                if(!libFolder.exists()) {
                  IdeaUtil.showNotificationWithAction(project, "Avm project Initialization", "Click below to setup the project",
                          NotificationType.INFORMATION, new NotificationAction("Run Initialize") {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                              notification.expire();
                              InitializationAction.initializeProject(e);

                              refreshNewMultiModuleProject(); //if required
                            }
                          });
                }
              }
            }
        }
      }
    });
  }

  @Override
  public void projectClosed() {
    try {
      connection.disconnect();
    } catch (Exception e) {

    }
  }

  @Override
  public void initComponent() {
  }

  @Override
  public void disposeComponent() {
  }

  /**
   * Debug logging can be enabled by adding fully classified class name/package name with # prefix
   * For eg., to enable debug logging, go `Help > Debug log settings` & type `#in.oneton.idea.spring.assistant.plugin.suggestion.service.SuggestionServiceImpl`
   *
   * @param doWhenDebug code to execute when debug is enabled
   */
  private void debug(Runnable doWhenDebug) {
    if (log.isDebugEnabled()) {
      doWhenDebug.run();
    }
  }

  private void refreshNewMultiModuleProject() {
    try {
      MavenProjectsManager mvnProjectManager = MavenProjectsManager.getInstance(project);

      if (mvnProjectManager.getProjects().size() == 1) {
        MavenProject mvnProject = mvnProjectManager.getProjects().get(0);

        if (mvnProject.isAggregator() && mvnProject.getModulePaths().size() > 0) {

          Set<String> modulePaths = mvnProject.getModulePaths();
          for (String path : modulePaths) {
            new File(path).setLastModified(System.currentTimeMillis());
            VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(new File(path));
            ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(vf).getModuleFile().refresh(false, true);
          }

//
//          IdeaUtil.showNotification(project, "Project loading",
//                  "Please close and re-open the project if the project doesn't load properly", NotificationType.INFORMATION, null);
        }
      }
    } catch (Exception ex) {

    }
  }

}
