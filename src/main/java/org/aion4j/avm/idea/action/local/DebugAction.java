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

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DebugAction extends AvmLocalBaseAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        if(project == null) {
            IdeaUtil.showNotification(project, "Debug failure", "Debugger could not be started",
                    NotificationType.WARNING, null);
            return;
        }

//        RemoteConnection remoteConnection = new RemoteConnection(true, "localhost", "8001", false);
//        final RemoteState remoteState = new RemoteStateState(project, remoteConnection);
//
//        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);
//
//        List<String> goals = new ArrayList<>();
//        goals.add("aion4j:call");
//
//        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(e, project, goals);
//        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);
//        mavenRunnerSettings.getMavenProperties().put("method", "getString");
//        mavenRunnerSettings.getMavenProperties().put("args", "-T ssa");
//        mavenRunnerSettings.getMavenProperties().put("preserveDebuggability", "true");
//
//        //mavenRunnerSettings.set
//        mavenRunnerSettings.setVmOptions("-Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=y");
//
//            try {
//                MavenRunConfiguration configuration;
//                XDebuggerManager.getInstance(project).startSessionAndShowTab("Avm", null, new XDebugProcessStarter() {
//                    @NotNull
//                    @Override
//                    public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
//
//                        RunnerAndConfigurationSettings settings = new RunnerAndConfigurationSettingsImpl(RunManagerImpl.getInstanceImpl(project));
//
//
//                        ExecutionEnvironment  ex = new ExecutionEnvironment(new DefaultDebugExecutor(), DefaultJavaProgramRunner.getInstance(), settings, project );
//
//                        final DebuggerSession debuggerSession =
//                                DebuggerManagerEx.getInstanceEx(project).attachVirtualMachine(new DefaultDebugEnvironment(ex, EmptyRunProfileState.INSTANCE, remoteConnection, true));
//
//                        JavaDebugConnectionData javaDebugConnectionData = new JavaDebugConnectionData("localhost", 8001);
//                        JavaDebuggerLauncher.getInstance().startDebugSession(javaDebugConnectionData, ex, null);
//
//                        return JavaDebugProcess.create(debuggerSession.getXDebugSession(), debuggerSession);
//                    }
//                });
//
////                RunnerAndConfigurationSettings settings = new RunnerAndConfigurationSettingsImpl(RunManagerImpl.getInstanceImpl(project));
////
////                        ExecutionEnvironment  ex = new ExecutionEnvironment(new DefaultDebugExecutor(), DefaultJavaProgramRunner.getInstance(), settings, project );
////
////
////                JavaDebugConnectionData javaDebugConnectionData = new JavaDebugConnectionData("localhost", 8001);
////                JavaDebuggerLauncher.getInstance().startDebugSession(javaDebugConnectionData, ex, new RemoteServerImpl("avm", ServerType.EP_NAME, null));
//
//
//            } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {
//            IdeaUtil.showNotification(project, "Account creation", "Account created successfully",
//                    NotificationType.INFORMATION, null);
//
//
//        });

    }

//    protected DebuggerSession attachVirtualMachine(Project project, RunProfileState state,
//                                                   ExecutionEnvironment environment,
//                                                   RemoteConnection remoteConnection,
//                                                   boolean pollConnection) throws ExecutionException {
//        final DebuggerSession debuggerSession =
//                DebuggerManagerEx.getInstanceEx(project).attachVirtualMachine(new DefaultDebugEnvironment(environment, state, remoteConnection, pollConnection));
//        XDebuggerManager.getInstance(project).startSession(environment, new XDebugProcessStarter() {
//            @Override
//            @NotNull
//            public XDebugProcess start(@NotNull XDebugSession session) {
//                return JavaDebugProcess.create(session, debuggerSession);
//            }
//        });
//        return debuggerSession;
//    }

    @Override
    public Icon getIcon() {
        return AvmIcons.CONTRACT_DEBUG;
    }
}
