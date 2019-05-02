package org.aion4j.avm.idea.action.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DeployArgsUI extends DialogWrapper {

    private JPanel mainPanel;
    private JTextField deployArgsTf;
    private JCheckBox dontAskCB;
    private JPanel panel;

    public DeployArgsUI(Project project, String module) {
        super(project, false);

        init();
        if(module != null)
            setTitle("Deployment Arguments : " + module);
        else
            setTitle("Deployment Arguments");

        getButton(getCancelAction()).setVisible(false);
        getButton(getCancelAction()).setEnabled(false);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    public String getDeploymentArgs() {
        return deployArgsTf.getText();
    }

    public boolean isDontAskSelected() {
        return dontAskCB.isSelected();
    }

    public void setDeploymentArgs(String args) {
        deployArgsTf.setText(args);
    }

    public void setDontAskSelected(boolean flag) {
        dontAskCB.setSelected(flag);
    }
}
