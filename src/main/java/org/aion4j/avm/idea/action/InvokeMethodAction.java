package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import org.aion4j.avm.idea.action.remote.AvmRemoteBaseAction;
import org.aion4j.avm.idea.action.remote.ui.CallMethodInputDialog;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.AvmTypeHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for Call Action and Contract Txn action
 */
public abstract class InvokeMethodAction extends AvmRemoteBaseAction {

    @Override
    public Icon getIcon() {
        return AvmIcons.EXECUTE_ICON;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        final PsiElement element = e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
        final Project project = e.getProject();

        if (!(element instanceof PsiMethod)) {
            System.out.println("Not a method");
            return;
        }

        //set kernel info
        Map<String, String> settingMap = new HashMap<>();
        initConfigInformation(project, settingMap);

        PsiMethod method = (PsiMethod) element;
        List<InvokeParam> parameters = getInvokeParams(method);

        System.out.println(method.getName());

        CallMethodInputDialog dialog = new CallMethodInputDialog(method.getName(), parameters);
        boolean result = dialog.showAndGet();

        if (!result)
            return; //User selected cancel;


        List<InvokeParam> params = dialog.getParamsWithValues();
        long value = dialog.getValue();
        String contractAddress = dialog.getContractAddress();

        params.forEach(pp -> {
            System.out.println(pp.getName() + " >> " + pp.getValue());
        });

        String argsStr = AvmTypeHelper.buildMethodArgsString(params);
        System.out.println("Args: " + argsStr);

        //Set goal parameters
        settingMap.put("method", method.getName());
        if(argsStr != null)
            settingMap.put("args", argsStr);

        if(value > 0)
            settingMap.put("value", String.valueOf(value));

        if(!StringUtil.isEmptyOrSpaces(contractAddress))
            settingMap.put("contract", contractAddress);

        System.out.println(settingMap);

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);
        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(project, getGoals());
        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings();

        mavenRunnerSettings.setMavenProperties(settingMap);
        mavenRunner.run(mavenRunnerParameters, mavenRunnerSettings, () -> {

        });
    }

    @NotNull
    protected List<InvokeParam> getInvokeParams(PsiMethod method) {
        String name = method.getName();

        PsiParameterList parameterList = method.getParameterList();

        List<InvokeParam> parameters = new ArrayList<>();

        for (PsiParameter param : parameterList.getParameters()) {
            String type = param.getType().getCanonicalText();

            boolean isArray = false;
            if (type.endsWith("[]")) {
                isArray = true;
                type = type.substring(0, type.length() - 2);
            }

            String avmType = AvmTypeHelper.getAvmType(type);

            InvokeParam invokeParam = new InvokeParam(param.getName(), type, avmType, isArray, "");
            parameters.add(invokeParam);
        }
        return parameters;
    }

    protected abstract List<String> getGoals();
    protected abstract void initConfigInformation(Project project, Map<String, String> settingMap);

}
