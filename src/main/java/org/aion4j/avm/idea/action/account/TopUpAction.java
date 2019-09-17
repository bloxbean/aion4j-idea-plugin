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

package org.aion4j.avm.idea.action.account;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.account.ui.TopupAccountDialog;
import org.aion4j.avm.idea.action.remote.AvmRemoteBaseAction;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class TopUpAction extends AvmRemoteBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        TopupAccountDialog dialog = new TopupAccountDialog(project, isRemote());
        boolean result = dialog.showAndGet();

        if(!result) {
            return;
        }

        String account = dialog.getAccount();
        String pk = dialog.getPrivateKey();
        BigInteger balance = dialog.getBalance();

        if(StringUtil.isEmpty(account)) {
            IdeaUtil.showNotification(project, "Account Topup Failed", "Empty account", NotificationType.ERROR, null);
            return;
        }

        if(isRemote()) { //only for remote mode
            if(StringUtil.isEmpty(pk)) {
                IdeaUtil.showNotification(project, "Account Topup Failed", "Empty private key", NotificationType.ERROR, null);
                return;
            }

        } else { //only for local mode
            if (balance == null || balance.equals(BigInteger.ZERO)) {
                IdeaUtil.showNotification(project, "Account Topup Failed", "Invalid Balance", NotificationType.ERROR, null);
                return;
            }
        }

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        goals.add("aion4j:account");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(e, project, goals);
        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);

        mavenRunnerSettings.getMavenProperties().put("topup", "true");

        if(!StringUtil.isEmpty(account))
            mavenRunnerSettings.getMavenProperties().put("address", account);

        if(isRemote()) {
            mavenRunnerSettings.getMavenProperties().put("pk", pk);
        } else {
            if (balance != null && balance.compareTo(BigInteger.ZERO) == 1) {
                mavenRunnerSettings.getMavenProperties().put("balance", String.valueOf(balance));
            } else {
                mavenRunnerSettings.getMavenProperties().put("balance", BigInteger.ZERO.toString());
            }
        }

        //TODOl
        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            IdeaUtil.showNotification(project, "Account Topup", "Account topup was successful",
                    NotificationType.INFORMATION, null);
        });
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> properties) {

    }

    @Override
    public Icon getIcon() {
        return AvmIcons.TRANSFER_ICON;
    }

}
