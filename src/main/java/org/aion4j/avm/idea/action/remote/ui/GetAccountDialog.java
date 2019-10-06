package org.aion4j.avm.idea.action.remote.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.aion4j.avm.idea.action.account.AccountChooser;
import org.aion4j.avm.idea.action.account.model.Account;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GetAccountDialog extends DialogWrapper {
    private JTextField accountTf;
    private JPanel mainPanel;
    private JButton selectAccountButton;

    public GetAccountDialog(Project project) {
        super(project, true);
        init();
        setTitle("Enter Account");

        selectAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account selectedAccount = AccountChooser.getSelectedAccount(project, false);

                if(selectedAccount != null)
                    accountTf.setText(selectedAccount.getAddress());
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
}
