package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.action.InvokeMethodAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContractTxnAction extends InvokeMethodAction {
    @Override
    protected List<String> getGoals() {
        List<String> goals = new ArrayList<>();
        goals.add("aion4j:contract-txn");

        return goals;
    }

    @Override
    protected void initConfigInformation(Project project, Map<String, String> settingMap) {
        populateCredentialInfo(project, settingMap);
    }
}
