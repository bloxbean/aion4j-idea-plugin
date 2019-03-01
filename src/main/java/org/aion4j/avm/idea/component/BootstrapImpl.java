package org.aion4j.avm.idea.component;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import org.aion4j.avm.idea.service.AvmService;
import org.jetbrains.annotations.NotNull;

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
    AvmService service = ServiceManager.getService(project, AvmService.class);

    debug(() -> log.debug("Project " + project.getName() + " is opened, checking if it's avm project"));
    service.init(project);

  }

  @Override
  public void projectClosed() {
    connection.disconnect();
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

}
