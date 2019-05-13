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

package org.aion4j.avm.idea.action.remote;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.AvmBaseAction;
import org.aion4j.avm.idea.action.AvmConfiguration;
import org.aion4j.avm.idea.action.ui.AvmConfigUI;
import org.aion4j.avm.idea.service.AvmConfigStateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Satya
 */
public abstract class AvmRemoteBaseAction extends AvmBaseAction {
    @Override
    protected boolean isRemote() {
        return true;
    }


    /**
     * Get web3rpc url and  credentials of the set account
     * @param project
     * @param settingMap
     * @return
     */
    protected AvmConfigStateService.State populateCredentialInfo(Project project, Map<String, String> settingMap) {

        AvmConfigStateService.State state = getConfigState(project);

        List<String> reqFields = new ArrayList<>();
        if(StringUtil.isEmptyOrSpaces(state.web3RpcUrl)) {
            reqFields.add("Web3 Rpc Url");
        }


        if(StringUtil.isEmptyOrSpaces(state.pk)) {
            reqFields.add("Private Key");
        }

        AvmConfigUI.RemoteConfigModel configDialogResponse = null;
        if(reqFields.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Please provide ");

            reqFields.stream().forEach(fl -> sb.append(fl + "  "));

            configDialogResponse = AvmConfiguration.showAvmRemoteConfig(project, sb.toString());

            if(configDialogResponse != null) {
                state = getConfigState(project);//configService.getState();
            } else {
                return null;
            }
        }

        if(state != null) {

            settingMap.put("web3rpc.url", state.web3RpcUrl);


            if(configDialogResponse == null) { //store credential option
                if (!StringUtil.isEmptyOrSpaces(state.pk)) {
                    settingMap.put("pk", state.pk);
                }
//                else {
//                    settingMap.put("address", state.account);
//                    settingMap.put("password", state.password);
//                }
            } else { //seems like it's nostorecredential option
                if (!StringUtil.isEmptyOrSpaces(configDialogResponse.getPk())) {
                    settingMap.put("pk", configDialogResponse.getPk());
                }
//                else {
//                    settingMap.put("address", configDialogResponse.getAccount());
//                    settingMap.put("password", configDialogResponse.getPassword());
//                }
            }
        }

        return state;
    }

    protected AvmConfigStateService.State populateKernelInfo(Project project, Map<String, String> settingMap) {

        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);
        AvmConfigStateService.State state = configService.getState();

        AvmConfigUI.RemoteConfigModel configDialogResponse = null;

        if(StringUtil.isEmptyOrSpaces(state.web3RpcUrl)) {
            configDialogResponse = AvmConfiguration.showAvmRemoteConfig(project, "Please configure Web3 Rpc Url.");

            if(configDialogResponse != null) {
                state = configService.getState();
            } else {
                return null;
            }
        }

        if(state != null) {
            settingMap.put("web3rpc.url", state.web3RpcUrl);
        }

        return state;
    }

    protected AvmConfigStateService.State populateKernelInfoAndAccount(Project project, Map<String, String> settingMap) {

        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);
        AvmConfigStateService.State state = configService.getState();

        AvmConfigUI.RemoteConfigModel configDialogResponse = null;

        if(StringUtil.isEmptyOrSpaces(state.web3RpcUrl) || StringUtil.isEmptyOrSpaces(state.account)) {
            configDialogResponse = AvmConfiguration.showAvmRemoteConfig(project, "Please configure Web3 Rpc Url and account.");

            if(configDialogResponse != null) {
                state = configService.getState();
            } else {
                return null;
            }
        }

        if(state != null) {
            settingMap.put("web3rpc.url", state.web3RpcUrl);
        }

        return state;
    }

}
