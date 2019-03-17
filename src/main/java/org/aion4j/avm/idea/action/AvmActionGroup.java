package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.service.AvmService;

public class AvmActionGroup extends DefaultActionGroup {

    @Override
    public void update(AnActionEvent event) {
        // Enable/disable depending on whether user is editing...
        Project project = event.getProject();

        if(project == null)
            return;

        AvmService avmService = ServiceManager.getService(project, AvmService.class);

        if(avmService == null) {
            event.getPresentation().setVisible(false);
            return;
        }

        if(avmService.isAvmProject()) {
            event.getPresentation().setVisible(true);
            event.getPresentation().setIcon(AvmIcons.AION_ICON);
        } else {
            event.getPresentation().setVisible(false);
            event.getPresentation().setIcon(AvmIcons.AION_ICON);
        }
    }
}
