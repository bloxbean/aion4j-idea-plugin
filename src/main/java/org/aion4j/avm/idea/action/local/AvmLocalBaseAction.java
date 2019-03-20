package org.aion4j.avm.idea.action.local;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.AvmBaseAction;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class AvmLocalBaseAction extends AvmBaseAction {

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    protected void configureAVMProperties(@NotNull Project project, @NotNull Map<String, String> settingMap) {
        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        //Don't do anything
        if(state != null) { //set avm properties
            settingMap.put("preserveDebuggability", String.valueOf(state.preserveDebugMode));
            settingMap.put("enableVerboseConcurrentExecutor", String.valueOf(state.verboseConcurrentExecutor));
            settingMap.put("enableVerboseContractErrors", String.valueOf(state.verboseContractError));

            if(!StringUtil.isEmptyOrSpaces(state.avmStoragePath))
                settingMap.put("storage-path", state.avmStoragePath);
        }

    }
}
