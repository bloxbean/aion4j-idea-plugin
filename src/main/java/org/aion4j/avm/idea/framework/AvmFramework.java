package org.aion4j.avm.idea.framework;

import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.icons.AllIcons;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModel;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class AvmFramework extends FrameworkTypeEx {

    public static final String FRAMEWORK_ID = "AVM";

    protected AvmFramework() {
        super(FRAMEWORK_ID);
    }

    @NotNull
    @Override
    public FrameworkSupportInModuleProvider createProvider() {
        return  new FrameworkSupportInModuleProvider() {
            @NotNull
            @Override
            public FrameworkTypeEx getFrameworkType() {
                return AvmFramework.this;
            }

            @NotNull
            @Override
            public FrameworkSupportInModuleConfigurable createConfigurable(@NotNull FrameworkSupportModel model) {
                return new FrameworkSupportInModuleConfigurable() {
                    @Nullable
                    @Override
                    public JComponent createComponent() {
                        return new JCheckBox("Extra Option");
                    }

                    @Override
                    public void addSupport(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ModifiableModelsProvider provider) {
                        //do what you want here: setup a library, generate a specific file, etc

                        //Copy avm.jar to lib folder.
                        System.out.println(">>>>>>>>>>>> Module Path>>>>>>>>>> " + module.getProject().getBasePath());

                        File pluginPath = PluginManager.getPlugin(PluginId.getId("org.aion4j.avm")).getPath();

                        FrameworkInitializer frameworkInitializer = new FrameworkInitializer(module.getProject().getBasePath(), pluginPath.getAbsolutePath());

                        try {
                            frameworkInitializer.execute();
                        } catch (AvmFrameworkException e) {
                            e.printStackTrace();
                        }

                        Library avmLibrary = model.getModuleLibraryTable().createLibrary("AVM");
                        avmLibrary.getModifiableModel().addRoot("file://" + module.getProject().getBasePath() +
                                "lib/" + FrameworkInitializer.AVM_USERLIB_JAR, OrderRootType.CLASSES);
                        avmLibrary.getModifiableModel().addRoot("file://" + module.getProject().getBasePath() +
                                "lib/" + FrameworkInitializer.AVM_API_JAR, OrderRootType.CLASSES);

                        avmLibrary.getModifiableModel().commit();
                    }
                };
            }

            @Override
            public boolean isEnabledForModuleType(@NotNull ModuleType type) {
                return true;
            }
        };
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Aion Virtual Machine";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return AllIcons.Providers.Apache;
    }
}
