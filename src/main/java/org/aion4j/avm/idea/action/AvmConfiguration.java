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

package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.ui.AvmConfigUI;
import org.aion4j.avm.idea.misc.AESEncryptionHelper;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.aion4j.avm.idea.service.CredentialService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;

/**
 *
 * @author Satya
 */
public class AvmConfiguration extends AvmBaseAction {

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
        configModel.setDisableCredentialStore(configService.getState().disableCredentialStore);
        configModel.setCleanAndBuildBeforeDeploy(configService.getState().cleanAndBuildBeforeDeploy);

        configModel.setDeployNrg(configService.getState().deployNrg);
        configModel.setDeployNrgPrice(configService.getState().deployNrgPrice);

        configModel.setContractTxnNrg(configService.getState().contractTxnNrg);
        configModel.setContractTxnNrgPrice(configService.getState().contractTxnNrgPrice);
        configModel.setMvnProfile(configService.getState().mvnProfile);
        configModel.setGetReceiptWait(configService.getState().getReceiptWait);

        configModel.setPreserveDebugMode(configService.getState().preserveDebugMode);
        configModel.setVerboseContractError(configService.getState().verboseContractError);
        configModel.setVerboseConcurrentExecutor(configService.getState().verboseConcurrentExecutor);
        configModel.setAvmStoragePath(configService.getState().avmStoragePath);
        configModel.setLocalDefaultAccount(configService.getState().localDefaultAccount);
        configModel.setShouldAskCallerAccountEverytime(configService.getState().shouldAskCallerAccountEverytime);

        configModel.setDisableJarOptimization(configService.getState().disableJarOptimization);
        configModel.setUseCredentialStore(configService.getState().useCredentialStore);

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
            state.getReceiptWait = remoteConfigModel.isGetReceiptWait();

            state.preserveDebugMode = remoteConfigModel.isPreserveDebugMode();
            state.verboseContractError = remoteConfigModel.isVerboseContractError();
            state.verboseConcurrentExecutor = remoteConfigModel.isVerboseConcurrentExecutor();

            state.avmStoragePath = remoteConfigModel.getAvmStoragePath();
            state.localDefaultAccount = remoteConfigModel.getLocalDefaultAccount();
            state.shouldAskCallerAccountEverytime = remoteConfigModel.shouldAskCallerAccountEverytime();

            state.disableJarOptimization = remoteConfigModel.isDisableJarOptimization();
            state.useCredentialStore = remoteConfigModel.isUseCredentialStore();

            if(remoteConfigModel.isDisableCredentialStore()) { //don't store credentials
                state.pk = "";
                state.encryptedPk = "";
            } else {
                state.pk = remoteConfigModel.getPk();
            }

            if(!StringUtil.isEmpty(state.pk)) { //If pk is not empty, encrypt and store the encrypted value
                String encryptionKey = CredentialService.getEncryptionKey(state.useCredentialStore);

                if(!StringUtil.isEmpty(encryptionKey))
                    state.encryptedPk = AESEncryptionHelper.encrypt(state.pk, encryptionKey);
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

    @Override
    protected boolean isRemote() { //Ignore .. doesn't matter for this impl
        return false;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> properties) { //Ignore..doesn't matter

    }
}
