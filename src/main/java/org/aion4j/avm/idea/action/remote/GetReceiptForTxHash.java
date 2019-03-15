package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.action.remote.ui.GetReceiptTxHashDialog;

public class GetReceiptForTxHash extends GetReceiptAction {

    @Override
    public String getTxHash(Project project) {
        GetReceiptTxHashDialog dialog = new GetReceiptTxHashDialog(project);

        boolean result = dialog.showAndGet();
        if(result) {
            return dialog.getTxHash();
        } else {
            return null;
        }
    }
}
