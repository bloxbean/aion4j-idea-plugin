package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.aion4j.avm.idea.misc.AvmIcons;

public class AvmActionGroup extends DefaultActionGroup {

    @Override
    public void update(AnActionEvent event) {
        // Enable/disable depending on whether user is editing...
        event.getPresentation().setVisible(true);
        event.getPresentation().setIcon(AvmIcons.AION_ICON);
    }
}
