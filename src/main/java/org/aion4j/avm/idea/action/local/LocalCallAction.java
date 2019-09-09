package org.aion4j.avm.idea.action.local;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.InvokeMethodAction;
import org.aion4j.avm.idea.action.local.ui.LocalGetAccountDialog;
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
    protected boolean isRemote() {
        return false;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> settingMap) {
        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        if(state != null) { //set avm properties
            settingMap.put("preserveDebuggability", String.valueOf(state.preserveDebugMode));
            settingMap.put("enableVerboseConcurrentExecutor", String.valueOf(state.verboseConcurrentExecutor));
            settingMap.put("enableVerboseContractErrors", String.valueOf(state.verboseContractError));

            if(!StringUtil.isEmptyOrSpaces(state.avmStoragePath))
                settingMap.put("storage-path", state.avmStoragePath);
        }

        if(state.shouldAskCallerAccountEverytime) {
            String inputAccount = getInputDeployerAccount(project);

            if(!StringUtil.isEmptyOrSpaces(inputAccount)) {
                settingMap.put("address", inputAccount.trim());
            }
        } else {
            if (!StringUtil.isEmptyOrSpaces(state.localDefaultAccount)) {
                settingMap.put("address", state.localDefaultAccount);
            }
        }
    }

    private String getInputDeployerAccount(Project project) {
        LocalGetAccountDialog dialog = new LocalGetAccountDialog(project);
        boolean result = dialog.showAndGet();

        if(!result) {
            return null;
        }

        return dialog.getAccount();
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.LOCAL_CALL;
    }
}
