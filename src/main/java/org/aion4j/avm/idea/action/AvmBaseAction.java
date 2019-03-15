package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import org.aion4j.avm.idea.service.AvmService;
import org.jetbrains.annotations.NotNull;

public abstract class AvmBaseAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {

        AvmService avmService = ServiceManager.getService(AvmService.class);

        if(avmService != null && avmService.isAvmProject()) {
            e.getPresentation().setEnabledAndVisible(true);
        }
    }
}
