package org.aion4j.avm.idea.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;

@State(name="aion4j", reloadable = true, storages = @com.intellij.openapi.components.Storage("aion4j.xml"))
public class AvmConfigStateService implements PersistentStateComponent<AvmConfigStateService.State> {

    public static class State {

        public String web3RpcUrl;
        public String account;
        public String pk;
        public String password;
        public boolean disableCredentialStore;
        public boolean cleanAndBuildBeforeDeploy = true;
        public String deployNrg;
        public String deployNrgPrice;
        public String contractTxnNrg;
        public String contractTxnNrgPrice;
        public String mvnProfile;
        //public String deployArgs;
        public boolean getReceiptWait = true;

        //local avm props
        public boolean preserveDebugMode;
        public boolean verboseContractError;
        public boolean verboseConcurrentExecutor;
        public String avmStoragePath;
        public String localDefaultAccount;
        public boolean shouldAskCallerAccountEverytime;

        //common
        public boolean disableJarOptimization;
    }

    State state;

    public State getState() {
        if(state == null)
            return new State();
        else
            return state;
    }

    public void loadState(State state) {
        this.state = state;
    }
}
