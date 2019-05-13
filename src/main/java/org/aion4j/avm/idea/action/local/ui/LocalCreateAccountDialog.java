package org.aion4j.avm.idea.action.local.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LocalCreateAccountDialog extends DialogWrapper {
    private JTextField accountTf;
    private JPanel mainPanel;
    private JTextField balanceTf;
    private JCheckBox generateAccCB;

    public LocalCreateAccountDialog(Project project) {
        super(project, false);
        init();
        setTitle("Enter Account");

        this.generateAccCB.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(generateAccCB.isSelected()) {
                    accountTf.setText("");
                    accountTf.setEditable(false);
                    accountTf.setEnabled(false);
                } else {
                    accountTf.setEditable(true);
                    accountTf.setEnabled(true);
                }
            }
        });
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
