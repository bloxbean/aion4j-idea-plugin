package org.aion4j.avm.idea.action.local.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LocalCreateAccountDialog extends DialogWrapper {
    private JTextField accountTf;
    private JPanel mainPanel;
    private JTextField balanceTf;

    public LocalCreateAccountDialog(Project project) {
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

    public long getBalance() {
        try {
            return Long.parseLong(balanceTf.getText().trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
