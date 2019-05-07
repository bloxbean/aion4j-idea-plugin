package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.action.InvokeMethodAction;
import org.aion4j.avm.idea.service.AvmConfigStateService;

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
    protected void configureAVMProperties(Project project, Map<String, String> properties) {
        populateCredentialInfo(project, properties);

        AvmConfigStateService.State state = getConfigState(project);
        if(state != null) {
            //Start get-receipt after contract txn
            if (state.getReceiptWait) {
                properties.put("wait", "true");
            }
        }
    }

}
