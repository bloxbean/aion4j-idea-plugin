package org.aion4j.avm.idea.action.local.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.aion4j.avm.idea.action.account.AccountChooser;
import org.aion4j.avm.idea.action.account.model.Account;
import org.aion4j.avm.idea.misc.PsiCustomUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LocalGetAccountDialog extends DialogWrapper {
    private JTextField accountTf;
    private JPanel mainPanel;
    private JButton selectAccountButton;

    public LocalGetAccountDialog(Project project) {
        super(project, true);
        init();
        setTitle("Enter Account");

        selectAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //moduleDir can be set to null when showBalance is false.
                Account selectedAccount = AccountChooser.getLocalAvmSelectedAccount(project);

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
