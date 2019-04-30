package org.aion4j.avm.idea.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import org.aion4j.avm.idea.action.remote.AvmRemoteBaseAction;
import org.aion4j.avm.idea.action.remote.ui.CallMethodInputDialog;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.AvmMethodArgsHelper;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.service.AvmCacheService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base class for Call Action and Contract Txn action
 */
public abstract class InvokeMethodAction extends AvmRemoteBaseAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        PsiFile file =  e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if(file != null && file instanceof PsiJavaFile) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public Icon getIcon() {
        return AvmIcons.EXECUTE_ICON;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        PsiElement element = null;

        try {
            element = e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
        } catch (Error ex) {

        }

        final Project project = e.getProject();

        if (element == null || !(element instanceof PsiMethod)) {
            IdeaUtil.showNotification(project, "Avm - Call Method", "Please right click on the method name",
                    NotificationType.WARNING, null);
            return;
        }

        MavenRunner mavenRunner = ServiceManager.getService(project, MavenRunner.class);
        MavenRunnerParameters mavenRunnerParameters = getMavenRunnerParameters(e, project, getGoals());
        MavenRunnerSettings mavenRunnerSettings = getMavenRunnerSettings(project);
        //set kernel info
        //Map<String, String> settingMap = new HashMap<>();
        //initConfigInformation(project, settingMap);

        PsiMethod method = (PsiMethod) element;
        List<InvokeParam> parameters = getInvokeParams(method);

        AvmCacheService avmCacheService = ServiceManager.getService(project, AvmCacheService.class);

        List<String> cacheArgs = null;
        if(avmCacheService != null) {
            cacheArgs = avmCacheService.getArgsFromCache(method);

            if(cacheArgs != null && cacheArgs.size() > 0 && parameters.size() == cacheArgs.size()) {
                for(int i=0; i < parameters.size(); i++) {
                    parameters.get(i).setDefaultValue(cacheArgs.get(i)); //set cache value as default value
                }
            }
        }

        CallMethodInputDialog dialog = new CallMethodInputDialog(method.getName(), parameters);
        boolean result = dialog.showAndGet();

        if (!result)
            return; //User selected cancel;

        List<InvokeParam> params = dialog.getParamsWithValues();
        long value = dialog.getValue();
        String contractAddress = dialog.getContractAddress();

        //Store to cache.
        avmCacheService.updateArgsToCache(method, params.stream().map(p -> p.getValue()).collect(Collectors.toList()));
        avmCacheService.loadState(avmCacheService.getState());

        String argsStr = AvmMethodArgsHelper.buildMethodArgsString(params);

        Map<String, String> settingMap = mavenRunnerSettings.getMavenProperties();
        //Set goal parameters
        settingMap.put("method", method.getName());
        if(argsStr != null)
            settingMap.put("args", argsStr);

        if(value > 0)
            settingMap.put("value", String.valueOf(value));

        if(!StringUtil.isEmptyOrSpaces(contractAddress))
            settingMap.put("contract", contractAddress);



       // mavenRunnerSettings.setMavenProperties(settingMap);
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
            boolean is2DArray = false;

            if(type.endsWith("[][]")) {
                is2DArray = true;
                type = type.substring(0, type.length() - 4);
            }else if (type.endsWith("[]")) {
                isArray = true;
                type = type.substring(0, type.length() - 2);
            }

            String avmType = AvmMethodArgsHelper.getAvmType(type);

            InvokeParam invokeParam = new InvokeParam(param.getName(), type, avmType, isArray, is2DArray,"");
            parameters.add(invokeParam);
        }
        return parameters;
    }

    protected abstract List<String> getGoals();
}
