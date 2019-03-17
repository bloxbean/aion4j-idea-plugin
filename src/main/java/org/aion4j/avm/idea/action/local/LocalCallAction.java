package org.aion4j.avm.idea.action.local;

import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.action.InvokeMethodAction;
import org.aion4j.avm.idea.misc.AvmIcons;

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
        //Don't do anything
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
