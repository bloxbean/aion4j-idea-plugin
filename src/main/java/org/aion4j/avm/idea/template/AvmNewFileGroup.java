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

package org.aion4j.avm.idea.template;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.service.AvmService;
import org.aion4j.avm.idea.template.service.ContractType;
import org.aion4j.avm.idea.template.service.ContractTypeRepositoryService;

import java.util.List;

public class AvmNewFileGroup extends DefaultActionGroup {

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();

        event.getPresentation().setVisible(false);

        if(project == null)
            return;

        AvmService avmService = ServiceManager.getService(project, AvmService.class);

        if(avmService == null || !avmService.isAvmProject())
            return;

        PsiElement psiElement =  event.getDataContext().getData(CommonDataKeys.PSI_ELEMENT);

        if(psiElement == null) {
            return;
        }

        PsiDirectory directory = null;
        if(psiElement instanceof PsiDirectory) {
            directory = (PsiDirectory)psiElement;

            if(avmService.isUnderSourceFolder(directory.getVirtualFile())) {
                event.getPresentation().setVisible(true);
                event.getPresentation().setIcon(AvmIcons.AION_ICON);
            } else {
                return;
            }
        } else {
            return;
        }

        ContractTypeRepositoryService contractTypeRepositoryService = ServiceManager.getService(ContractTypeRepositoryService.class);
        if(!contractTypeRepositoryService.isInitialized()) {
            contractTypeRepositoryService.init();

            List<ContractType> contractTypes = contractTypeRepositoryService.getContractTypes();
            if(contractTypes == null) return;

            for(ContractType contractType: contractTypes) {
                NewAvmFileAction newAvmFileAction = new NewAvmFileAction(contractType.getId(), contractType.getName(), contractType.getName(),AvmIcons.CONFIG_ICON);
                add(newAvmFileAction);
            }
        }

    }

}
