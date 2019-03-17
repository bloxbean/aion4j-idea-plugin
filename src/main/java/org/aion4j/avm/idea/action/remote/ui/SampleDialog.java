package org.aion4j.avm.idea.action.remote.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.aion4j.avm.idea.action.InvokeParam;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.ui.ListCellRendererWrapper.createSeparator;

public class SampleDialog extends DialogWrapper {

    private JTextField companyNameField;
    private JTextField contactPersonField;
    private JTextField orderNoField;
    private JTextField inspectorField;
    private JTextField referenceNoField;
    private JComboBox  approvalStatusComboBox;
    private JTextField shipYardField;
    private JTextField registerNoField;
    private JTextField hullNumbersField;
    private JComboBox  projectTypeComboBox;

    public SampleDialog(java.util.List<InvokeParam> params) {
        super(false); // use current window as parent
        init();
        setTitle("Provide method args");
    }

    private void initComponents() {
        companyNameField       = new JTextField();
        contactPersonField     = new JTextField();
        orderNoField           = new JTextField();
        inspectorField         = new JTextField();
        referenceNoField       = new JTextField();
        approvalStatusComboBox = new JComboBox();
        shipYardField          = new JTextField();
        registerNoField        = new JTextField();
        hullNumbersField       = new JTextField();
        projectTypeComboBox    = new JComboBox();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        initComponents();
        FormLayout layout = new FormLayout(
                "right:max(40dlu;pref), 3dlu, 70dlu, 7dlu, "
                        + "right:max(40dlu;pref), 3dlu, 70dlu",
                "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p");

        JPanel panel = new JPanel(layout);
        panel.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();
        panel.add(createSeparator("Manufacturer"),  cc.xyw(1,  1, 7));
        panel.add(new JLabel("Company"),            cc.xy (1,  3));
        panel.add(companyNameField,                 cc.xyw(3,  3, 5));
        panel.add(new JLabel("Contact"),            cc.xy (1,  5));
        panel.add(contactPersonField,               cc.xyw(3,  5, 5));
        panel.add(new JLabel("Order No"),           cc.xy (1, 7));
        panel.add(orderNoField,                     cc.xy (3, 7));

        panel.add(createSeparator("Inspector"),     cc.xyw(1, 9, 7));
        panel.add(new JLabel("Name"),               cc.xy (1, 11));
        panel.add(inspectorField,                   cc.xyw(3, 11, 5));
        panel.add(new JLabel("Reference No"),       cc.xy (1, 13));
        panel.add(referenceNoField,                 cc.xy (3, 13));
        panel.add(new JLabel("Status"),             cc.xy (1, 15));
        panel.add(approvalStatusComboBox,           cc.xy (3, 15));

        panel.add(createSeparator("Ship"),          cc.xyw(1, 17, 7));
        panel.add(new JLabel("Shipyard"),           cc.xy (1, 19));
        panel.add(shipYardField,                    cc.xyw(3, 19, 5));
        panel.add(new JLabel("Register No"),        cc.xy (1, 21));
        panel.add(registerNoField,                  cc.xy (3, 21));
        panel.add(new JLabel("Hull No"),            cc.xy (5, 21));
        panel.add(hullNumbersField,                 cc.xy (7, 21));
        panel.add(new JLabel("Project Type"),       cc.xy (1, 23));
        panel.add(projectTypeComboBox,              cc.xy (3, 23));

        return panel;

    }


}
