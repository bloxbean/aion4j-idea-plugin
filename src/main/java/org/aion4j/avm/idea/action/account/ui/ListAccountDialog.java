/*
 * Copyright (c) 2019 Aion4j Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.aion4j.avm.idea.action.account.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.aion4j.avm.idea.action.account.AccountListFetcher;
import org.aion4j.avm.idea.action.account.model.Account;
import org.aion4j.avm.idea.kernel.adapter.LocalAvmAdapter;
import org.aion4j.avm.idea.kernel.adapter.RemoteAVMNodeAdapter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ListAccountDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTable accListTable;
    private JButton fetchBalanceButton;
    private Project project;
    private List<Account> accounts;
    private AccountListTableModel tableModel;
    private boolean isRemote;
    private boolean showBalance;

    //Kernel Adapters
    LocalAvmAdapter localAvmAdapter;

    public ListAccountDialog(Project project, List<Account> accounts, boolean isRemote) {
        this(project, accounts, isRemote, true);
    }

    public ListAccountDialog(Project project, List<Account> accounts, boolean isRemote, boolean showBalance) {
        super(project, false);
        init();
        setTitle("Accounts (" + (isRemote ? "Remote Mode": "Embedded Mode") + ")");
        this.project = project;
        this.accounts = accounts;
        this.isRemote = isRemote;
        this.showBalance = showBalance;

        populateAccount(accounts);

        if(showBalance) {
            //Right align balance column
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            accListTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        } else {
            fetchBalanceButton.setVisible(false);
        }

        if(!isRemote) {
            localAvmAdapter = new LocalAvmAdapter(project);
        }

        fetchBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchBalance(isRemote);
            }
        });
    }

    public void setModuleWorkingDir(String moduleWorkingDir) {
        localAvmAdapter.setWorkingDir(moduleWorkingDir);
    }

    public void fetchBalance(boolean isRemote) {
        if (accounts == null) return;

        if(isRemote) {
            AccountListFetcher accountListFetcher = new AccountListFetcher(project);

            RemoteAVMNodeAdapter remoteAVMNodeAdapter = accountListFetcher.getRemoteAvmAdapter(project);
            if (remoteAVMNodeAdapter == null) //Return null. May be web3rpc_url is not set yet.
                return;

            try {
                ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                    @Override
                    public void run() {
                        ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                        float counter = 0;
                        for (Account account : accounts) {
                            BigInteger balance = accountListFetcher.getBalance(account, isRemote);
                            progressIndicator.setFraction(counter++ / accounts.size());
                            if (balance != null) {
                                account.setBalance(balance);
                            }
                        }
                        progressIndicator.setFraction(1.0);
                        tableModel.fireTableDataChanged();
                    }
                }, "Fetching balance from remote kernel ...", true, project);

            } finally {

            }
        } else { //For local Avm
            localAvmAdapter.getLocalAvmAccounts(this);
        }
    }

    public Account getSelectAccount() {
        if (accounts == null)
            return null;

        int selectedRow = accListTable.getSelectedRow();
        if (selectedRow == -1)
            return null;
        else if (selectedRow <= accounts.size() - 1) {
            return accounts.get(selectedRow);
        } else {
            return null;
        }
    }

    public void updateAccount(List<Account> accs) {
        if(accs == null) return;

        if(this.accounts == null)
            this.accounts = new ArrayList<>();

        this.accounts.clear();
        for(Account account: accs) {
            this.accounts.add(account);
        }

        tableModel.fireTableDataChanged();
    }

    private void populateAccount(List<Account> accounts) {
        tableModel = new AccountListTableModel(accounts, showBalance);
        accListTable.setModel(tableModel);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }
}
