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
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.twelvemonkeys.lang.StringUtil;
import org.aion4j.avm.idea.misc.*;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DebugAction extends LocalCallAction {
    private final static String STORAGE_DIR = "storage";

    @Override
    protected boolean preExecute(AnActionEvent evt, Project project) {
        MavenProject mavenProject = PsiCustomUtil.getMavenProject(project, evt);
        if(mavenProject == null) {
           // IdeaUtil.showNotification(project, "Contract Debug", "Debug failed. Please check if it's a valid Maven project", NotificationType.ERROR, null);
            //Don't proceed. Just try to run debug.
            return true;
        }

        String targetFolder = mavenProject.getBuildDirectory();
        String storagePath = targetFolder + File.separator + STORAGE_DIR;

        String prjName = mavenProject.getDisplayName();
        if(prjName == null) {
            //IdeaUtil.showNotification(project, "Contract Debug", "Something is wrong. Please reload the project and try again.", NotificationType.ERROR, null);
            //Don't proceed. Just try to run debug.
            return true;
        }

        //Check if storage path is set
        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        if(!StringUtil.isEmpty(state.avmStoragePath)) {
            storagePath = state.avmStoragePath;
        }

        ResultCache resultCache = new ResultCache(prjName, storagePath);

        if(resultCache != null && (!resultCache.getDebugEnabledInLastDeploy() || StringUtil.isEmpty(resultCache.getLastDeployedAddress()))) {
            IdeaUtil.showNotification(project, "Debug", "Please deploy the contract in debug mode first. \n" +
                    "Aion Virtual Machine -> Embedded -> Deploy (Debug Mode)", NotificationType.ERROR, null);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(Project project, MavenRunner mavenRunner, MavenRunnerParameters mavenRunnerParameters, MavenRunnerSettings mavenRunnerSettings) {

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
