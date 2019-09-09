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

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.twelvemonkeys.lang.StringUtil;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.misc.ResultCache;
import org.aion4j.avm.idea.misc.ResultCacheUtil;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;

import javax.swing.*;

public class LocalDebugAction extends LocalCallAction {

    @Override
    protected boolean preExecute(AnActionEvent evt, Project project) {
        ResultCache resultCache = ResultCacheUtil.getResultCache(project, evt);

        if(resultCache == null) {
            return true; //Just ignore any error and continue
        } else if(resultCache != null && (!resultCache.getDebugEnabledInLastDeploy() || StringUtil.isEmpty(resultCache.getLastDeployedAddress()))) {
            IdeaUtil.showNotification(project, "Debug", "Please deploy the contract in debug mode first. \n" +
                    "Aion Virtual Machine -> Embedded -> Deploy (Debug Mode)", NotificationType.ERROR, null);
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void execute(Project project, AnActionEvent evt, MavenRunner mavenRunner, MavenRunnerParameters mavenRunnerParameters, MavenRunnerSettings mavenRunnerSettings) {

        mavenRunnerSettings.getMavenProperties().put("preserveDebuggability", "true");

        MavenRunConfigurationType mavenRunConfigurationType = ConfigurationTypeUtil.findConfigurationType(MavenRunConfigurationType.class);
        RunnerAndConfigurationSettings runnerConfigurationSettings = MavenRunConfigurationType.createRunnerAndConfigurationSettings(new MavenGeneralSettings(), mavenRunnerSettings, mavenRunnerParameters, project);
        ProgramRunnerUtil.executeConfiguration(runnerConfigurationSettings, DefaultDebugExecutor.getDebugExecutorInstance());
    }


    @Override
    public Icon getIcon() {
        return AvmIcons.CONTRACT_DEBUG;
    }

}
