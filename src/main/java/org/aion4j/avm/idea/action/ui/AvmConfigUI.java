package org.aion4j.avm.idea.action.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import org.aion4j.avm.idea.action.remote.NrgConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.ArrayList;
import java.util.List;

public class AvmConfigUI extends DialogWrapper {
    private JPanel contentPane;
    private JTextField web3RpcTf;
    private JTextField pkTf;
    private JTextField accountTf;
    private JTextField passwordTf;
    private JCheckBox notStoreCredentialsCheckBox;
    private JCheckBox cleanAndBuildCheckBox;
    private JLabel customMessageLabel;
    private JTabbedPane tabbedPane;
    private JTextField deployNrgTf;
    private JTextField deployNrgPriceTf;
    private JTextField contractTxnNrgTf;
    private JTextField contractTxnNrgPriceTf;
    private JTextField mvnProfileTf;
    private JTextField deployArgsTf;

    public AvmConfigUI(Project project, String customMessage) {

        super(project, false);
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
        passwordTf.getDocument().addDocumentListener(l);

        if(customMessage != null) {
//            setErrorText(customErrorMessage);
//            getRootPane().revalidate();
//            return;
            customMessageLabel.setText(customMessage);
        }

        doValidateInput();

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
        setPassword(model.password);
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

        setDeployArgs(model.getDeployArgs());
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

    public void setPassword(String password) {
        passwordTf.setText(password);
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

    public void setDeployArgs(String deployArgs) {
        this.deployArgsTf.setText(deployArgs);
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
        return new RemoteConfigModel(web3RpcTf.getText(), pkTf.getText(), accountTf.getText(), passwordTf.getText(),
                notStoreCredentialsCheckBox.isSelected(), cleanAndBuildCheckBox.isSelected(), deployNrgTf.getText(), deployNrgPriceTf.getText(),
                contractTxnNrgTf.getText(), contractTxnNrgPriceTf.getText(), mvnProfileTf.getText(), deployArgsTf.getText());
    }


    public static class RemoteConfigModel {
        private String web3RpcUrl;
        private String pk;
        private String account;
        private String password;
        private boolean disableCredentialStore;
        private boolean cleanAndBuildBeforeDeploy;
        private String deployNrg;
        private String deployNrgPrice;
        private String contractTxnNrg;
        private String contractTxnNrgPrice;
        private String mvnProfile;
        private String deployArgs;

        public RemoteConfigModel() {

        }

        public RemoteConfigModel(String web3RpcUrl, String pk, String account, String password,
                                 boolean disableCredentialStore, boolean cleanAndBuildBeforeDeploy, String deployNrg, String deployNrgPrice,
                                 String contractTxnNrg, String contractTxnNrgPrice, String mvnProfile, String deployArgs) {
            this.web3RpcUrl = web3RpcUrl;
            this.pk = pk;
            this.account = account;
            this.password = password;
            this.disableCredentialStore = disableCredentialStore;
            this.cleanAndBuildBeforeDeploy = cleanAndBuildBeforeDeploy;
            this.deployNrg = deployNrg;
            this.deployNrgPrice = deployNrgPrice;
            this.contractTxnNrg = contractTxnNrg;
            this.contractTxnNrgPrice = contractTxnNrgPrice;
            this.mvnProfile = mvnProfile;
            this.deployArgs = deployArgs;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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

        public String getDeployArgs() {
            return deployArgs;
        }

        public void setDeployArgs(String deployArgs) {
            this.deployArgs = deployArgs;
        }
    }

}
