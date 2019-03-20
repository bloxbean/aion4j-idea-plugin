package org.aion4j.avm.idea.action.remote.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GetAccountDialog extends DialogWrapper {
    private JTextField accountTf;
    private JPanel mainPanel;

    public GetAccountDialog(Project project) {
        super(project, false);
        init();
        setTitle("Enter Account");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    public String getAccount() {
        return accountTf.getText().trim();
    }
}
