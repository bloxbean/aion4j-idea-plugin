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

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.remote.ui.TransferDialog;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Satya
 */
public class TransferAction extends AvmRemoteBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);

        TransferDialog dialog = new TransferDialog(project);

        boolean result = dialog.showAndGet();
        if(result) {

            MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

            List<String> goals = new ArrayList<>();
            goals.add("aion4j:transfer");

            MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(e, project, goals);

            Map<String, String> settingMap = mavenRunnerSettings.getMavenProperties();
            if (!StringUtil.isEmptyOrSpaces(dialog.getPrivateKey())) {
                settingMap.put("pk", dialog.getPrivateKey());
            } else {
                //Todo
                IdeaUtil.showNotification(project, "Transfer failed", "Private key can not be empty",
                        NotificationType.ERROR, null);
                return;
            }

            if(StringUtil.isEmpty(dialog.getToAccount())) {
                IdeaUtil.showNotification(project, "Transfer failed", "To account can not be empty",
                        NotificationType.ERROR, null);
                return;
            }

            settingMap.put("to", dialog.getToAccount());
            settingMap.put("value", dialog.getValue());

            settingMap.put("gas", dialog.getNrg());
            settingMap.put("gasPrice", dialog.getNrgPrice());


            mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {

            });
        }
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.TRANSFER_ICON;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> properties) {
        populateKernelInfo(project, properties);

        AvmConfigStateService.State state = getConfigState(project);
        if(state != null) {
            //Start get-receipt after transfer
            if (state.getReceiptWait) {
                properties.put("wait", "true");
            }
        }
    }
}
