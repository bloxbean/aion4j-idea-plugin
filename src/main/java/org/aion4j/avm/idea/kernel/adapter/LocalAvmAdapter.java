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

package org.aion4j.avm.idea.kernel.adapter;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.account.AccountResultReader;
import org.aion4j.avm.idea.action.account.model.Account;
import org.aion4j.avm.idea.action.account.ui.ListAccountDialog;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalAvmAdapter {

    private String workingDir;
    private Project project;

    public LocalAvmAdapter(Project project) {
        this.project = project;
    }

    public LocalAvmAdapter(Project project, String workingDir) {
        this.project = project;
        this.workingDir = workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public void getLocalAvmAccounts(ListAccountDialog listAccountDialog) {
        if(StringUtil.isEmpty(workingDir)) {
            IdeaUtil.showNotification(project, "Account List", "Error fetching account list. Unknown module working directory.", NotificationType.ERROR, null);
            return;
        }

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);

        List<String> goals = new ArrayList<>();
        goals.add("aion4j:account");

        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);
        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, new ArrayList<>());
        mavenRunnerParameters.getGoals().addAll(goals);

        try {
            final File outputFile = File.createTempFile(".aion4j", ".accList");

            mavenRunnerSettings.getMavenProperties().put("list-with-balance", "true");
            mavenRunnerSettings.getMavenProperties().put("output", outputFile.getAbsolutePath());

             //TODOl
            mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
                if(outputFile.exists()) {
                    List<Account> accounts = AccountResultReader.getAccounts(outputFile);

                    if(accounts != null && accounts.size() > 0) {
                        listAccountDialog.updateAccount(accounts);
                    }
                    outputFile.delete();
                } else {
                    IdeaUtil.showNotification(project, "Account List", "Something wrong while fetching account list", NotificationType.ERROR, null);
                }
            });
        } catch (IOException ex) {
            IdeaUtil.showNotification(project, "Account List", "Error fetching account list", NotificationType.ERROR, null);

        }
    }

    private MavenRunnerParameters getMavenRunnerParameters(Project project, List<String> goals) {

        AvmConfigStateService avmConfigStateService = ServiceManager.getService(project, AvmConfigStateService.class);

        MavenRunnerParameters mavenRunnerParameters = new MavenRunnerParameters();
        mavenRunnerParameters.setWorkingDirPath(workingDir);

        mavenRunnerParameters.setPomFileName("pom.xml");
        mavenRunnerParameters.setGoals(goals);
//        mavenRunnerParameters.setWorkingDirPath(project.getBasePath());

        Map<String, Boolean> profileMap = new HashMap();

        mavenRunnerParameters.setProfilesMap(profileMap);

        return mavenRunnerParameters;

    }

    private MavenRunnerSettings getMavenRunnerSettings(Project project) {
        MavenRunnerSettings mavenRunnerSettings = new MavenRunnerSettings();
        mavenRunnerSettings.setDelegateBuildToMaven(true);

        Map<String, String> map = mavenRunnerSettings.getMavenProperties();
        if(map == null) {
            mavenRunnerSettings.setMavenProperties(new HashMap<>());
        }

        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        if(state != null) { //set avm properties
            mavenRunnerSettings.getMavenProperties().put("preserveDebuggability", String.valueOf(state.preserveDebugMode));
            mavenRunnerSettings.getMavenProperties().put("enableVerboseConcurrentExecutor", String.valueOf(state.verboseConcurrentExecutor));
            mavenRunnerSettings.getMavenProperties().put("enableVerboseContractErrors", String.valueOf(state.verboseContractError));

            if(!StringUtil.isEmptyOrSpaces(state.avmStoragePath))
                mavenRunnerSettings.getMavenProperties().put("storage-path", state.avmStoragePath);
        }

        return mavenRunnerSettings;
    }

}
