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

package org.aion4j.avm.idea.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;

@State(name="aion4j", reloadable = true, storages = @com.intellij.openapi.components.Storage("aion4j.xml"))
public class AvmConfigStateService implements PersistentStateComponent<AvmConfigStateService.State> {

    public static class State {

        public String web3RpcUrl;
        public String account;
        public String pk;
        public String password; //removed
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
