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
public class UnlockAccountAction extends AvmRemoteBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();

        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);
        AvmConfigStateService.State state = getConfigState(project);

        if(state == null) {
            IdeaUtil.showNotification(project, "Unlock Account",
                    "Account cannot be empty for an unlock command.", NotificationType.ERROR, IdeaUtil.AVM_REMOTE_CONFIG_ACTION);
            return;
        }

        if (!StringUtil.isEmptyOrSpaces(state.pk)) {
            IdeaUtil.showNotification(project, "Unlock Account",
                    "Account unlock is not required when private key is set", NotificationType.INFORMATION, null);
            return;
        }

        if (StringUtil.isEmptyOrSpaces(state.account)) {
            IdeaUtil.showNotification(project, "Unlock Account",
                    "Account cannot be empty for an unlock command.", NotificationType.ERROR, IdeaUtil.AVM_REMOTE_CONFIG_ACTION);
            return;
        }


        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        goals.add("aion4j:unlock");

        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(e, project, goals);


        Map<String, String> settingMap = mavenRunnerSettings.getMavenProperties();

        settingMap.put("address", state.account);
//        settingMap.put("password", state.password);

        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
            IdeaUtil.showNotification(project, "Unlock Account",
                    "Account was unlocked successfully", NotificationType.INFORMATION, null);

        });
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.UNLOCK_ICON;
    }

    @Override
    protected void configureAVMProperties(Project project, Map<String, String> properties) {
        populateKernelInfoAndAccount(project, properties);
    }
}
