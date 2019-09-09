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

package org.aion4j.avm.idea.misc;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.twelvemonkeys.lang.StringUtil;
import org.aion4j.avm.idea.service.AvmConfigStateService;
import org.jetbrains.idea.maven.project.MavenProject;

import java.io.File;

public class ResultCacheUtil {
    private final static String STORAGE_DIR = "storage";

    public static ResultCache getResultCache(Project project, AnActionEvent event) {
        MavenProject mavenProject = PsiCustomUtil.getMavenProject(project, event);
        if(mavenProject == null) {
            return null;
        }

        String storagePath = null;
        //Check if storage path is set in config
        AvmConfigStateService configService = ServiceManager.getService(project, AvmConfigStateService.class);

        AvmConfigStateService.State state = null;
        if(configService != null)
            state = configService.getState();

        if(!StringUtil.isEmpty(state.avmStoragePath)) {
            storagePath = state.avmStoragePath;
        } else {
            String targetFolder = mavenProject.getBuildDirectory();
            storagePath = targetFolder + File.separator + STORAGE_DIR;
        }

        String prjName = mavenProject.getMavenId().getArtifactId(); //rely on artifact id as maven plugin uses that
        if (prjName == null) {
            return null;
        }

        ResultCache resultCache = new ResultCache(prjName, storagePath);
        return resultCache;

    }
}
