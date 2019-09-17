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
import com.intellij.openapi.ui.Messages;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
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

public class CreateAccountAction extends AvmRemoteBaseAction {

    @Override
    protected boolean isRemote() {
        return true;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> properties) {
        populateKernelInfo(project, properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        boolean topup = false;
        int ret = Messages.showOkCancelDialog(project, "Do you also want to top-up the account with Aion tokens from Mastery Testnet?",
                "Aion4j Faucet - Topup", Messages.getQuestionIcon());

        if(ret == Messages.OK) {
           topup = true;
        }

        List<String> goals = new ArrayList<>();
        goals.add("aion4j:account");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(e, project, goals);
        MavenRunnerSettings mavenRunnerSettings = null;

        if(topup) { //Only when topup, remote kernel config is required
            mavenRunnerSettings = getMavenRunnerSettings(project);

            mavenRunnerSettings.getMavenProperties().put("create", "true");
            mavenRunnerSettings.getMavenProperties().put("topup", "true");
        } else {
            //For account creation remote kernel config is not required
            mavenRunnerSettings = new MavenRunnerSettings();
            mavenRunnerSettings.setDelegateBuildToMaven(true);

            mavenRunnerSettings.getMavenProperties().put("create", "true");
        }


        //TODO
        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            IdeaUtil.showNotification(project, "Account creation", "Account created successfully",
                    NotificationType.INFORMATION, null);
        });
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.CONFIG_ICON;
    }
}
