package org.aion4j.avm.idea.action.remote.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GetReceiptTxHashDialog extends DialogWrapper {
    private JTextField accountTf;
    private JPanel mainPanel;

    public GetReceiptTxHashDialog(Project project) {
        super(project, true);
        init();
        setTitle("Enter Tx Hash");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    public String getTxHash() {
        return accountTf.getText().trim();
    }
}
