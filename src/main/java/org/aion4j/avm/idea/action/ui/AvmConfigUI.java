/*
 * Copyright (c) 2019 BloxBean Project
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

package org.aion4j.avm.idea.action.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import org.aion4j.avm.idea.action.account.AccountChooser;
import org.aion4j.avm.idea.action.account.model.Account;
import org.aion4j.avm.idea.action.remote.NrgConstants;
import org.aion4j.avm.idea.maven.AVMArcheTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Satya
 */
public class AvmConfigUI extends DialogWrapper {
    private JPanel contentPane;
    private JTextField web3RpcTf;
    private JTextField pkTf;
    private JTextField accountTf;
    private JCheckBox notStoreCredentialsCheckBox;
    private JCheckBox cleanAndBuildCheckBox;
    private JLabel customMessageLabel;
    private JTabbedPane tabbedPane;
    private JTextField deployNrgTf;
    private JTextField deployNrgPriceTf;
    private JTextField contractTxnNrgTf;
    private JTextField contractTxnNrgPriceTf;
    private JTextField mvnProfileTf;
    private JCheckBox getReceiptWaitCB;
    private JCheckBox preserveDebugModeCheckBox;
    private JCheckBox verboseContractErrorCheckBox;
    private JCheckBox verboseConcurrentExecutorCheckBox;
    private JTextField storagePathTf;
    private JButton fileChooserButton;
    private JButton storagePathResetButton;
    private JTextField localDefaultAccountTf;
    private JCheckBox askCallerAccountCB;
    private JCheckBox disableJarOptimizationCB;
    private JButton fetchButton;
    private JLabel fetchStatusLabel;
    private JButton defaultAccountChooser;
    private JButton localDefaultAccountChooser;
    private JCheckBox useCredentialStoreCB;

    public AvmConfigUI(Project project, String customMessage) {

        super(project, true);
        init();
        setTitle("Aion Virtual Machine - Configuration");

        DocumentAdapter l = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                doValidateInput();
            }
        };

        web3RpcTf.getDocument().addDocumentListener(l);
        pkTf.getDocument().addDocumentListener(l);
        accountTf.getDocument().addDocumentListener(l);

        if(customMessage != null) {
//            setErrorText(customErrorMessage);
//            getRootPane().revalidate();
//            return;
            customMessageLabel.setText(customMessage);
        }

        //For embedded AVM storage path
        initStoragePathFileChooser();

        doValidateInput();

        commonTab();

        //Account chooser
        defaultAccountChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account selectedAccount = AccountChooser.getSelectedAccount(project, false);

                if(selectedAccount != null) {
                    accountTf.setText(selectedAccount.getAddress());
                    pkTf.setText(selectedAccount.getPrivateKey());
                }
            }
        });

        localDefaultAccountChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account selectedAccount = AccountChooser.getLocalAvmSelectedAccount(project, null, false);

                if(selectedAccount != null) {
                    localDefaultAccountTf.setText(selectedAccount.getAddress());
                }
            }
        });

    }

    public void initStoragePathFileChooser() {
        storagePathTf.setEditable(false);

        disableNewFolderButton(fileChooserButton);
        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("Choose a directory for AVM's disk storage: ");
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    if (jfc.getSelectedFile().isDirectory()) {
                        String path = jfc.getSelectedFile().getAbsolutePath();

                        if(!StringUtil.isEmptyOrSpaces(path)) {
                            path = path + File.separator + "avm_storage";
                        }
                        storagePathTf.setText(path);
                    }
                }
            }
        });

        storagePathResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storagePathTf.setText("");
            }
        });
    }

    //Needed to disable New Folder button
    public static void disableNewFolderButton(Container c) {
        int len = c.getComponentCount();
        for (int i = 0; i < len; i++) {
            Component comp = c.getComponent(i);
            if (comp instanceof JButton) {
                JButton b = (JButton) comp;
                Icon icon = b.getIcon();
                if (icon != null
                        && icon == UIManager.getIcon("FileChooser.newFolderIcon"))
                    b.setEnabled(false);
            } else if (comp instanceof Container) {
                disableNewFolderButton((Container) comp);
            }
        }
    }

    private void commonTab() {
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fetchStatusLabel.setText("Fetching...");
                    AVMArcheTypeUtil.updateArcheTypeCache();
                    fetchStatusLabel.setText("Avm archetypes updated.");
                } catch (Exception ex) {
                    fetchStatusLabel.setText("Fetch failed.");
                }
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return web3RpcTf;
    }

    public void setState(RemoteConfigModel model) {
        setWeb3RpcUrl(model.web3RpcUrl);
        setPk(model.pk);
        setAccount(model.account);
        setNotStoreCredentialsTf(model.disableCredentialStore);
        setCleanAndBuildCheckBox(model.cleanAndBuildBeforeDeploy);

        if(!StringUtil.isEmptyOrSpaces(model.getDeployNrg()))
            setDeployNrg(model.getDeployNrg());
        else
            setDeployNrg(String.valueOf(NrgConstants.defaulDeploytNrg));

        if(!StringUtil.isEmptyOrSpaces(model.getDeployNrgPrice())) {
            setDeployNrgPrice(model.getDeployNrgPrice());
        } else
            setDeployNrgPrice(String.valueOf(NrgConstants.defaultDeployNrgPrice));

        if(!StringUtil.isEmptyOrSpaces(model.getContractTxnNrg()))
            setContractTxnNrg(model.getContractTxnNrg());
        else
            setContractTxnNrg(String.valueOf(NrgConstants.defaultContractTxnNrg));

        if(!StringUtil.isEmptyOrSpaces(model.getContractTxnNrgPrice()))
            setContractTxnNrgPrice(model.getContractTxnNrgPrice());
        else
            setContractTxnNrgPrice(String.valueOf(NrgConstants.defaultContractTxnNrgPrice));

        if(!StringUtil.isEmptyOrSpaces(model.getMvnProfile()))
            setMvnProfile(model.getMvnProfile());
        else
            setMvnProfile("remote");

        setGetReceiptWait(model.isGetReceiptWait());

        //set Avm params
        setPreserveDebugMode(model.isPreserveDebugMode());
        setVerboseContractError(model.isVerboseContractError());
        setVerboseConcurrentExecutor(model.isVerboseConcurrentExecutor());

        if(!StringUtil.isEmptyOrSpaces(model.getAvmStoragePath()))
            setAvmStoragePath(model.getAvmStoragePath());
//        else
//            setAvmStoragePath(model.getAvmStoragePath());

        if(!StringUtil.isEmptyOrSpaces(model.getLocalDefaultAccount()))
            setLocalDefaultAccountTf(model.getLocalDefaultAccount());

        setAskCallerAccountEverytime(model.shouldAskCallerAccountEverytime());

        setDisableJarOptimization(model.isDisableJarOptimization());

        setUseCredentialStore(model.isUseCredentialStore());

    }

    public void setWeb3RpcUrl(String web3RpcUrl) {
        web3RpcTf.setText(web3RpcUrl);
    }

    public void setPk(String pk) {
        pkTf.setText(pk);
    }

    public void setAccount(String account) {
        accountTf.setText(account);
    }

    public void setNotStoreCredentialsTf(boolean isNotStoreCredential) {
        notStoreCredentialsCheckBox.setSelected(isNotStoreCredential);
    }

    public void setCleanAndBuildCheckBox(boolean cleanAndBuildCheckBox) {
        this.cleanAndBuildCheckBox.setSelected(cleanAndBuildCheckBox);
    }

    public void setDeployNrg(String nrg) {
        deployNrgTf.setText(nrg);
    }

    public void setDeployNrgPrice(String nrgPrice) {
        deployNrgPriceTf.setText(nrgPrice);
    }

    public void setContractTxnNrg(String contractTxnNrg) {
        this.contractTxnNrgTf.setText(contractTxnNrg);
    }

    public void setContractTxnNrgPrice(String contractTxnNrgPrice) {
        this.contractTxnNrgPriceTf.setText(contractTxnNrgPrice);
    }

    public void setMvnProfile(String mavenProfie) {
        this.mvnProfileTf.setText(mavenProfie);
    }

    public void setGetReceiptWait(boolean wait) {
        this.getReceiptWaitCB.setSelected(wait);
    }

    public void setPreserveDebugMode(boolean flag) {
        this.preserveDebugModeCheckBox.setSelected(flag);
    }

    public void setVerboseContractError(boolean flag) {
        this.verboseContractErrorCheckBox.setSelected(flag);
    }

    public void setVerboseConcurrentExecutor(boolean flag) {
        this.verboseConcurrentExecutorCheckBox.setSelected(flag);
    }

    public void setAvmStoragePath(String storagePath) {
        this.storagePathTf.setText(storagePath);
    }

    public void setLocalDefaultAccountTf(String localDefaultAccountTf) {
        this.localDefaultAccountTf.setText(localDefaultAccountTf);
    }

    public void setAskCallerAccountEverytime(boolean flag) {
        this.askCallerAccountCB.setSelected(flag);
    }

    public void setDisableJarOptimization(boolean flag) {
        this.disableJarOptimizationCB.setSelected(flag);
    }

    public void setUseCredentialStore(boolean flag) {
        this.useCredentialStoreCB.setSelected(flag);
    }

    private void doValidateInput() {

        List<String> errors = new ArrayList();

        if(!StringUtil.isEmptyOrSpaces(web3RpcTf.getText()) && !web3RpcTf.getText().startsWith("http")) errors.add("web3RpcUrl");
        if(!StringUtil.isEmptyOrSpaces(accountTf.getText()) && (!accountTf.getText().startsWith("0xa0")
                    && !accountTf.getText().startsWith("a0"))) {
            errors.add("account");
        }

        if(!StringUtil.isEmptyOrSpaces(deployNrgTf.getText())) {
            try {
                Long.parseLong(deployNrgTf.getText().trim());
            } catch (NumberFormatException e) {
                errors.add("Deploy Nrg");
            }
        }

        if(!StringUtil.isEmptyOrSpaces(deployNrgPriceTf.getText())) {
            try {
                Long.parseLong(deployNrgPriceTf.getText().trim());
            } catch (NumberFormatException e) {
                errors.add("Deploy Nrg Price");
            }
        }

        if(!StringUtil.isEmptyOrSpaces(contractTxnNrgTf.getText())) {
            try {
                Long.parseLong(contractTxnNrgTf.getText().trim());
            } catch (NumberFormatException e) {
                errors.add("Contract Txn Nrg");
            }
        }

        if(!StringUtil.isEmptyOrSpaces(contractTxnNrgPriceTf.getText())) {
            try {
                Long.parseLong(contractTxnNrgPriceTf.getText().trim());
            } catch (NumberFormatException e) {
                errors.add("Contract Txn Nrg Price");
            }
        }

        if (errors.isEmpty()) {
            setErrorText(null);
            getOKAction().setEnabled(true);
            return;
        }
        String message = "Please specify valid " + StringUtil.join(errors, ", ");
        setErrorText(message);
        getOKAction().setEnabled(false);
        getRootPane().revalidate();
    }

    public RemoteConfigModel getRemoteConfig() {
        return new RemoteConfigModel(web3RpcTf.getText(), pkTf.getText(), accountTf.getText(),
                notStoreCredentialsCheckBox.isSelected(), cleanAndBuildCheckBox.isSelected(), deployNrgTf.getText(), deployNrgPriceTf.getText(),
                contractTxnNrgTf.getText(), contractTxnNrgPriceTf.getText(), mvnProfileTf.getText(), getReceiptWaitCB.isSelected(),
                preserveDebugModeCheckBox.isSelected(), verboseContractErrorCheckBox.isSelected(), verboseConcurrentExecutorCheckBox.isSelected(),
                storagePathTf.getText(), localDefaultAccountTf.getText(), askCallerAccountCB.isSelected(), disableJarOptimizationCB.isSelected(),
                useCredentialStoreCB.isSelected());
    }


    public static class RemoteConfigModel {
        private String web3RpcUrl;
        private String pk;
        private String account;
        private boolean disableCredentialStore;
        private boolean cleanAndBuildBeforeDeploy;
        private String deployNrg;
        private String deployNrgPrice;
        private String contractTxnNrg;
        private String contractTxnNrgPrice;
        private String mvnProfile;
        private boolean getReceiptWait;

        //local avm properties
        private boolean preserveDebugMode;
        private boolean verboseContractError;
        private boolean verboseConcurrentExecutor;
        private String avmStoragePath;
        private String localDefaultAccount;
        private boolean shouldAskCallerAccountEverytime;

        private boolean disableJarOptimization;
        private boolean useCredentialStore;

        public RemoteConfigModel() {

        }

        public RemoteConfigModel(String web3RpcUrl, String pk, String account,
                                 boolean disableCredentialStore, boolean cleanAndBuildBeforeDeploy, String deployNrg, String deployNrgPrice,
                                 String contractTxnNrg, String contractTxnNrgPrice, String mvnProfile, boolean getReceiptWait,
                                 boolean preserveDebugMode, boolean verboseContractError, boolean verboseConcurrentExecutor, String avmStoragePath,
                                 String localDefaultAccount, boolean shouldAskCallerAccountEverytime, boolean disableJarOptimization, boolean useCredentialStore) {
            this.web3RpcUrl = web3RpcUrl;
            this.pk = pk;
            this.account = account;
            this.disableCredentialStore = disableCredentialStore;
            this.cleanAndBuildBeforeDeploy = cleanAndBuildBeforeDeploy;
            this.deployNrg = deployNrg;
            this.deployNrgPrice = deployNrgPrice;
            this.contractTxnNrg = contractTxnNrg;
            this.contractTxnNrgPrice = contractTxnNrgPrice;
            this.mvnProfile = mvnProfile;
            this.getReceiptWait = getReceiptWait;

            this.preserveDebugMode = preserveDebugMode;
            this.verboseContractError = verboseContractError;
            this.verboseConcurrentExecutor = verboseConcurrentExecutor;
            this.avmStoragePath = avmStoragePath;
            this.localDefaultAccount = localDefaultAccount;
            this.shouldAskCallerAccountEverytime = shouldAskCallerAccountEverytime;

            this.disableJarOptimization = disableJarOptimization;
            this.useCredentialStore = useCredentialStore;
        }

        public String getWeb3RpcUrl() {
            return web3RpcUrl;
        }

        public void setWeb3RpcUrl(String web3RpcUrl) {
            this.web3RpcUrl = web3RpcUrl;
        }

        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public boolean isDisableCredentialStore() {
            return disableCredentialStore;
        }

        public void setDisableCredentialStore(boolean disableCredentialStore) {
            this.disableCredentialStore = disableCredentialStore;
        }

        public boolean isCleanAndBuildBeforeDeploy() {
            return cleanAndBuildBeforeDeploy;
        }

        public void setCleanAndBuildBeforeDeploy(boolean cleanAndBuildBeforeDeploy) {
            this.cleanAndBuildBeforeDeploy = cleanAndBuildBeforeDeploy;
        }

        public String getDeployNrg() {
            return deployNrg;
        }

        public void setDeployNrg(String deployNrg) {
            this.deployNrg = deployNrg;
        }

        public String getDeployNrgPrice() {
            return deployNrgPrice;
        }

        public void setDeployNrgPrice(String deployNrgPrice) {
            this.deployNrgPrice = deployNrgPrice;
        }

        public String getContractTxnNrg() {
            return contractTxnNrg;
        }

        public void setContractTxnNrg(String contractTxnNrg) {
            this.contractTxnNrg = contractTxnNrg;
        }

        public String getContractTxnNrgPrice() {
            return contractTxnNrgPrice;
        }

        public void setContractTxnNrgPrice(String contractTxnNrgPrice) {
            this.contractTxnNrgPrice = contractTxnNrgPrice;
        }

        public String getMvnProfile() {
            return mvnProfile;
        }

        public void setMvnProfile(String mvnProfile) {
            this.mvnProfile = mvnProfile;
        }

        public boolean isGetReceiptWait() {
            return getReceiptWait;
        }

        public void setGetReceiptWait(boolean getReceiptWait) {
            this.getReceiptWait = getReceiptWait;
        }

        public void setPreserveDebugMode(boolean preserveDebugMode) {
            this.preserveDebugMode = preserveDebugMode;
        }

        public boolean isPreserveDebugMode() {
            return preserveDebugMode;
        }

        public void setVerboseContractError(boolean verboseContractError) {
            this.verboseContractError = verboseContractError;
        }

        public boolean isVerboseContractError() {
            return verboseContractError;
        }

        public void setVerboseConcurrentExecutor(boolean verboseConcurrentExecutor) {
            this.verboseConcurrentExecutor = verboseConcurrentExecutor;
        }

        public boolean isVerboseConcurrentExecutor() {
            return verboseConcurrentExecutor;
        }

        public String getAvmStoragePath() {
            return avmStoragePath;
        }

        public void setAvmStoragePath(String avmStoragePath) {
            this.avmStoragePath = avmStoragePath;
        }

        public String getLocalDefaultAccount() {
            return localDefaultAccount;
        }

        public void setLocalDefaultAccount(String localDefaultAccount) {
            this.localDefaultAccount = localDefaultAccount;
        }

        public boolean shouldAskCallerAccountEverytime() {
            return shouldAskCallerAccountEverytime;
        }

        public void setShouldAskCallerAccountEverytime(boolean shouldAskCallerAccountEverytime) {
            this.shouldAskCallerAccountEverytime = shouldAskCallerAccountEverytime;
        }

        public boolean isDisableJarOptimization() {
            return disableJarOptimization;
        }

        public void setDisableJarOptimization(boolean disableJarOptimization) {
            this.disableJarOptimization = disableJarOptimization;
        }

        public boolean isUseCredentialStore() {
            return useCredentialStore;
        }

        public void setUseCredentialStore(boolean useCredentialStore) {
            this.useCredentialStore = useCredentialStore;
        }
    }

}
