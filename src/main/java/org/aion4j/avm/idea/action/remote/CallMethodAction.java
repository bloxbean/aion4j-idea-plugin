package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.action.InvokeMethodAction;
import org.aion4j.avm.idea.misc.AvmIcons;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallMethodAction extends InvokeMethodAction {

    @Override
    protected List<String> getGoals() {
        List<String> goals = new ArrayList<>();
        goals.add("aion4j:call");

        return goals;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> settingMap) {
        populateKernelInfo(project, settingMap);
    }

    @Override
    protected boolean isCall() {
        return true;
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.REMOTE_CALL;
    }
}
