package org.aion4j.avm.idea.action.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.aion4j.avm.idea.misc.AionConversionUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.math.BigInteger;

public class DeployArgsUI extends DialogWrapper {

    private JPanel mainPanel;
    private JTextField deployArgsTf;
    private JCheckBox dontAskCB;
    private JPanel panel;
    private JTextField valueTf;
    private JLabel valueLabel;

    public DeployArgsUI(Project project, String module, boolean callFromDeployConfig) {
        super(project, false);

        init();
        if(module != null)
            setTitle("Deployment Arguments : " + module);
        else
            setTitle("Deployment Arguments");

        valueTf.setText(String.valueOf(0));

        if(callFromDeployConfig) { //If called from deploy configuration, don't show
            valueLabel.setVisible(false);
            valueTf.setVisible(false);
        }

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

    public BigInteger getValue() {
        try {
            double aionValue = Double.parseDouble(valueTf.getText().trim());
            return AionConversionUtil.aionTonAmp(aionValue);
        } catch(Exception e) {
            return BigInteger.ZERO;
        }
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
