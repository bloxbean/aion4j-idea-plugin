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

package org.aion4j.avm.idea.action.local;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.account.TopUpAction;
import org.aion4j.avm.idea.service.AvmConfigStateService;

import java.util.Map;

public class LocalAccountTopupAction extends TopUpAction {

    @Override
    protected boolean isRemote() {
        return false;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> settingMap) {
        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        if(state != null) { //set avm properties
            settingMap.put("preserveDebuggability", String.valueOf(state.preserveDebugMode));
            settingMap.put("enableVerboseConcurrentExecutor", String.valueOf(state.verboseConcurrentExecutor));
            settingMap.put("enableVerboseContractErrors", String.valueOf(state.verboseContractError));

            if(!StringUtil.isEmptyOrSpaces(state.avmStoragePath))
                settingMap.put("storage-path", state.avmStoragePath);
        }
    }
}
