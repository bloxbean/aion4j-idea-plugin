package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.action.remote.AvmRemoteBaseAction;
import org.aion4j.avm.idea.action.ui.AvmConfigUI;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AvmConfiguration extends AvmRemoteBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        showAvmRemoteConfig(e.getProject(), null);
    }

    public static AvmConfigUI.RemoteConfigModel showAvmRemoteConfig(@NotNull Project project, String customMessage) {
        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigUI configDialog = new AvmConfigUI(project, customMessage);

        AvmConfigUI.RemoteConfigModel configModel = new AvmConfigUI.RemoteConfigModel();

        configModel.setWeb3RpcUrl(configService.getState().web3RpcUrl);
        configModel.setPk(configService.getState().pk);
        configModel.setAccount(configService.getState().account);
        configModel.setPassword(configService.getState().password);
        configModel.setDisableCredentialStore(configService.getState().disableCredentialStore);
        configModel.setCleanAndBuildBeforeDeploy(configService.getState().cleanAndBuildBeforeDeploy);

        configModel.setDeployNrg(configService.getState().deployNrg);
        configModel.setDeployNrgPrice(configService.getState().deployNrgPrice);

        configModel.setContractTxnNrg(configService.getState().contractTxnNrg);
        configModel.setContractTxnNrgPrice(configService.getState().contractTxnNrgPrice);
        configModel.setMvnProfile(configService.getState().mvnProfile);

        configModel.setDeployArgs(configService.getState().deployArgs);

        configDialog.setState(configModel);

        //Show the dialog
        boolean result = configDialog.showAndGet();
        if(result) {
            // user pressed ok. Store value to state
            AvmConfigUI.RemoteConfigModel remoteConfigModel = configDialog.getRemoteConfig();

            AvmConfigStateService.State state = configService.getState();//new AvmConfigStateService.State();
            state.web3RpcUrl = remoteConfigModel.getWeb3RpcUrl();
            state.disableCredentialStore = remoteConfigModel.isDisableCredentialStore();
            state.cleanAndBuildBeforeDeploy = remoteConfigModel.isCleanAndBuildBeforeDeploy();

            //Additional details..
            state.deployNrg = remoteConfigModel.getDeployNrg();
            state.deployNrgPrice = remoteConfigModel.getDeployNrgPrice();
            state.contractTxnNrg = remoteConfigModel.getContractTxnNrg();
            state.contractTxnNrgPrice = remoteConfigModel.getContractTxnNrgPrice();
            state.mvnProfile = remoteConfigModel.getMvnProfile();

            state.deployArgs = remoteConfigModel.getDeployArgs();

            if(remoteConfigModel.isDisableCredentialStore()) { //don't store credentials
                state.pk = "";
                state.password = "";
            } else {
                state.pk = remoteConfigModel.getPk();
                state.password = remoteConfigModel.getPassword();
            }

            state.account = remoteConfigModel.getAccount();

            configService.loadState(state);
            return remoteConfigModel;
        } else {
            return null;
        }
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.CONFIG_ICON;
    }
}
