package org.aion4j.avm.idea.action;

import com.intellij.execution.PsiLocation;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import org.aion4j.avm.idea.action.remote.AvmRemoteBaseAction;
import org.aion4j.avm.idea.action.remote.ui.CallMethodInputDialog;
import org.aion4j.avm.idea.misc.AvmApiConstant;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.aion4j.avm.idea.misc.AvmMethodArgsHelper;
import org.aion4j.avm.idea.misc.IdeaUtil;
import org.aion4j.avm.idea.service.AvmCacheService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

import javax.swing.*;
import java.math.BigInteger;
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

        if(!calledFromGutterAction()) { //Called from context menu
            try {
                element = e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
            } catch (Error ex) {

            }

            if(element != null && element instanceof PsiMethod) { //Check if callable method
                if(!((PsiMethod) element).hasAnnotation(AvmApiConstant.CALLABLE_ANNOTATION))
                    element = null; //Need to check through PsiLocation
            }
        }

        final Project project = e.getProject();

        if(element == null || !(element instanceof PsiMethod)) {
            //Let's check through location
            PsiLocation psiLocation = e.getData(DataKey.create("Location"));
            if(psiLocation != null) {
                element = psiLocation.getPsiElement();
            }
        }

        if (element == null || !(element instanceof PsiMethod)) {
            IdeaUtil.showNotification(project, "Avm - Call Method", "Please right click on a @callable method name",
                    NotificationType.WARNING, null);
            return;
        }

        if(!preExecute(e, project)) return;

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

        List<String> contractAddresses = null;
        if(isRemote())
            contractAddresses = avmCacheService.getRemoteContractAddresses();
        else
            contractAddresses = avmCacheService.getLocalContractAddresses();

        CallMethodInputDialog dialog = new CallMethodInputDialog(method.getName(), parameters, contractAddresses, isCall());
        boolean result = dialog.showAndGet();

        if (!result)
            return; //User selected cancel;

        List<InvokeParam> params = dialog.getParamsWithValues();
        BigInteger value = dialog.getValue();
        String contractAddress = dialog.getContractAddress();

        //Store to cache.
        avmCacheService.updateArgsToCache(method, params.stream().map(p -> p.getValue()).collect(Collectors.toList()));

        if(!StringUtil.isEmpty(contractAddress)) { //Add contract address to cache
            if(isRemote())
                avmCacheService.addRemoteContractAddress(contractAddress);
            else
                avmCacheService.addLocalContractAddress(contractAddress);
        }

        avmCacheService.loadState(avmCacheService.getState());

        String argsStr = AvmMethodArgsHelper.buildMethodArgsString(params);

        Map<String, String> settingMap = mavenRunnerSettings.getMavenProperties();
        //Set goal parameters
        settingMap.put("method", method.getName());
        if(argsStr != null)
            settingMap.put("args", argsStr);

        if(value != null && value.compareTo(BigInteger.ZERO) == 1)
            settingMap.put("value", value.toString());

        if(!StringUtil.isEmptyOrSpaces(contractAddress))
            settingMap.put("contract", contractAddress);
        
        execute(project, e, mavenRunner, mavenRunnerParameters, mavenRunnerSettings);
    }

    /**
     * Override in subclass if any pre-work needed before actual run
     * @param e
     * @param project
     * @return
     */
    protected boolean preExecute(AnActionEvent e, Project project) {
        return true;
    }

    /**
     * Run mavenrunner here. This method is implemented differently for DebugAction
     * @param mavenRunner
     * @param mavenRunnerParameters
     * @param mavenRunnerSettings
     */
    protected void execute(Project project, AnActionEvent evt, MavenRunner mavenRunner, MavenRunnerParameters mavenRunnerParameters, MavenRunnerSettings mavenRunnerSettings) {
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

    protected boolean isCall() {
        return false;
    }

    protected abstract List<String> getGoals();

    protected boolean calledFromGutterAction() {
        return false;
    }

}
