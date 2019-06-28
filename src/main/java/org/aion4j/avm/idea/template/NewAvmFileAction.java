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

import com.intellij.ide.IdeView;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.service.AvmService;
import org.aion4j.avm.idea.template.service.ContractParam;
import org.aion4j.avm.idea.template.service.ContractType;
import org.aion4j.avm.idea.template.service.ContractTypeRepositoryService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NewAvmFileAction extends AnAction {

    private String contractId;

    public NewAvmFileAction() {
    }

    public NewAvmFileAction(@Nullable String id, @Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
        this.contractId = id;
    }

    @Override
    public void update(final AnActionEvent e)
    {
        AvmService avmService = ServiceManager.getService(e.getProject(), AvmService.class);

        if(avmService != null && avmService.isAvmProject()) {
            e.getPresentation().setEnabledAndVisible(true);
        }

        e.getPresentation().setIcon(AvmIcons.GETRECEIPT_ICON);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();

        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        if (view == null)
        {
            return;
        }

        ContractTypeRepositoryService contractTypeRepositoryService = ServiceManager.getService(ContractTypeRepositoryService.class);
        if(contractTypeRepositoryService == null)
            return;

        ContractType contractType = contractTypeRepositoryService.getContractType(contractId);

        if(contractType == null)
            return;

        Project project = PlatformDataKeys.PROJECT.getData(dataContext);

        CreateNewContractDialog createFileForm = new CreateNewContractDialog("ATS Token", getProperties(contractType.getParams()));
        createFileForm.setTitle(contractType.getName());
        createFileForm.setResizable(false);
        createFileForm.pack();
        createFileForm.show();

        if (createFileForm.getExitCode() == DialogWrapper.OK_EXIT_CODE)
        {

            Properties properties = new Properties(FileTemplateManager.getInstance().getDefaultProperties(project));

            for(ContractParam param: createFileForm.getParamsWithValues()) {
                properties.setProperty(param.getName(), param.getValue());
            }

            PsiElement psiElement =  e.getDataContext().getData(CommonDataKeys.PSI_ELEMENT);

            PsiDirectory directory = null;
            if(psiElement instanceof PsiDirectory) {
                directory = (PsiDirectory)psiElement;
            }

            String filenameWithoutExtension = createFileForm.getClassName();

            for(ContractType.Template t: contractType.getTemplates()) {
                try {
                    FileTemplate template = FileTemplateManager.getInstance().getInternalTemplate(t.getName());
                    PsiElement element = FileTemplateUtil.createFromTemplate(template, filenameWithoutExtension, properties, (PsiDirectory) directory);
               } catch (Exception ex) {
                    ex.printStackTrace();
               }
            }

            IdeaUtil.showNotification(project, "Contract Template",
                    "Avm Contract created successfully. Please make sure to configure contract.main.class property in pom.xml",
                    NotificationType.INFORMATION, null);

        }
    }



    private List<ContractParam> getProperties(List<ContractParam> cps) {

        List<ContractParam> params = new ArrayList<>();
        for(ContractParam cp: cps) {
            params.add(new ContractParam(cp.getName(), cp.getTitle(), cp.getType(), cp.getType(), cp.isArray(), cp.is2DArray(), cp.getDefaultValue()));
        }
        return params;
    }

}
