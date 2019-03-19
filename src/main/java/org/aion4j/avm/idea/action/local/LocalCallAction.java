package org.aion4j.avm.idea.action.local;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.action.InvokeMethodAction;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.service.AvmConfigStateService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalCallAction extends InvokeMethodAction {

    @Override
    protected List<String> getGoals() {
        List<String> goals = new ArrayList<>();
        goals.add("aion4j:call");

        return goals;
    }

    @Override
    protected void initConfigInformation(Project project, Map<String, String> settingMap) {
        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        //Don't do anything
        if(state != null) { //set avm properties
            settingMap.put("preserveDebuggability", String.valueOf(state.preserveDebugMode));
            settingMap.put("enableVerboseConcurrentExecutor", String.valueOf(state.verboseConcurrentExecutor));
            settingMap.put("enableVerboseContractErrors", String.valueOf(state.verboseContractError));
        }
    }

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.EXECUTE_ICON;
    }
}
