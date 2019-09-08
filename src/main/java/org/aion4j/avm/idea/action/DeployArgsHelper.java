package org.aion4j.avm.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.aion4j.avm.idea.action.ui.DeployArgsUI;
import org.aion4j.avm.idea.common.Tuple;
import org.aion4j.avm.idea.misc.PsiCustomUtil;
import org.aion4j.avm.idea.service.AvmCacheService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class DeployArgsHelper {

    public static Tuple<Map<String, String>, BigInteger> getAndSaveDeploymentArgs(@NotNull AnActionEvent e, Project project, boolean checkDontShow, boolean callFromConfig) {

        Map<String, String> resultArgs = new HashMap<>();
        BigInteger value = null;

        AvmCacheService avmCacheService = ServiceManager.getService(project, AvmCacheService.class);

        MavenProject mavenProject = PsiCustomUtil.getMavenProject(project, e);

        boolean isAggregatorProject = false;
        String moduleName = null;
        if (mavenProject != null) {
            moduleName = PsiCustomUtil.getMavenProjectName(mavenProject);
            isAggregatorProject = mavenProject.isAggregator() && mavenProject.getModulePaths().size() > 0;
        } else {
            Module module = PsiCustomUtil.getModuleFromAction(project, e);
            moduleName = module != null ? module.getName() : null;//get module nmae
        }

        if (avmCacheService != null) {
            if ((checkDontShow && avmCacheService.shouldNotAskDeployArgs(moduleName)) || isAggregatorProject) { //If called during deployment, it may ignore the UI show

                if (isAggregatorProject) { //check if its top level project
                    resultArgs.putAll(avmCacheService.getAllDeployArgsWithModuleName());
                } else {
                    String deployArgs = avmCacheService.getDeployArgs(moduleName);
                    if(!StringUtil.isEmptyOrSpaces(deployArgs))
                        resultArgs.put("args", deployArgs);
                }
            } else {
                DeployArgsUI dialog = new DeployArgsUI(project, moduleName, callFromConfig);

                String cacheArgs = avmCacheService.getDeployArgs(moduleName);
                boolean cacheDontAsk = avmCacheService.shouldNotAskDeployArgs(moduleName);

                if (cacheArgs != null)
                    dialog.setDeploymentArgs(cacheArgs);

                dialog.setDontAskSelected(cacheDontAsk);

                boolean result = dialog.showAndGet();
                if (result) {
                    String deployArgs = dialog.getDeploymentArgs();

                    avmCacheService.updateDeployArgs(moduleName, deployArgs);
                    avmCacheService.setShouldNotAskDeployArgs(moduleName, dialog.isDontAskSelected());

                    deployArgs = avmCacheService.getDeployArgs(moduleName);
                    if(!StringUtil.isEmptyOrSpaces(deployArgs))
                        resultArgs.put("args", deployArgs);

                    value = dialog.getValue();

                }
            }
        }

        return new Tuple(resultArgs, value);
    }

}
