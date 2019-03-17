package org.aion4j.avm.idea.action.remote.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.aion4j.avm.idea.action.InvokeParam;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.ui.ListCellRendererWrapper.createSeparator;

public class CallMethodInputDialog extends DialogWrapper {

    private String method;
    private List<JTextField> paramTfs = new ArrayList<>();
    private JTextField valueTf;
    private JTextField contractTf;
    private JCheckBox useLastDeployedContractCB;

    private List<InvokeParam> params = new ArrayList<>();

    public CallMethodInputDialog(String method, java.util.List<InvokeParam> params) {
        super(false);

        this.method = method;
        this.params = params;
        init();
        setTitle(method + " - "+ "Method parameters");
    }

    private void initComponents() {
        for(InvokeParam param: params) {
            JTextField paramTf = new JTextField();
            paramTfs.add(paramTf);

            if(!StringUtil.isEmptyOrSpaces(param.getDefaultValue())) {
                paramTf.setText(param.getDefaultValue());
            }
        }

        valueTf = new JTextField();
        contractTf = new JTextField();
        useLastDeployedContractCB = new JCheckBox();

        valueTf.setText(0 + "");
        useLastDeployedContractCB.setSelected(true);
        contractTf.setEnabled(false);

        useLastDeployedContractCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(useLastDeployedContractCB.isSelected()) {
                   contractTf.setText("");
                   contractTf.setEnabled(false);
                } else {
                    contractTf.setEnabled(true);
                }
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        initComponents();
        FormLayout layout = new FormLayout(
                "right:max(40dlu;pref), 3dlu, 70dlu, 7dlu, "
                        + "right:max(40dlu;pref), 3dlu, 70dlu, 3dlu, left:max(40dlu;pref)",
                "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");

                JPanel panel = new JPanel(layout);
        panel.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();

        panel.add(createSeparator("Method Parameters"),  cc.xyw(1,  1, 9));

        int row = 3;
        Font font = panel.getFont();

        for(int i=0; i < params.size(); i++) {
            String paramType = params.get(i).getType();
            if(params.get(i).isArray())
                paramType += "[]";

            row += 2;
            JLabel paramLabel = new JLabel(params.get(i).getName());
            paramLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

            panel.add(paramLabel, cc.xy (1,  row  ));
            panel.add(paramTfs.get(i),                 cc.xyw(3,  row, 5));
            panel.add(new JLabel("( " + paramType  + " )" ),  cc.xy(9,  row));
        }

        //Contract details section
        row += 2;
        panel.add(createSeparator("Contract Details"),  cc.xyw(1,  row, 7));

        row += 2;
        panel.add(new JLabel("Value"), cc.xy (1,  row  ));
        panel.add(valueTf, cc.xyw(3,  row , 5));

        row += 2;
        //Contract address field
        JLabel lastContractCBLabel = new JLabel("Use Last Deployed Contract Address");
        panel.add(useLastDeployedContractCB, cc.xy (1,  row ));
        panel.add(lastContractCBLabel, cc.xyw(3,  row, 5));

        row += 2;
        JLabel contractLabel = new JLabel("Contract Address");
        panel.add(contractLabel, cc.xy (1,  row ));
        panel.add(contractTf, cc.xyw(3,  row, 5));

        return panel;

    }

    public List<InvokeParam> getParamsWithValues() {
        for(int i=0; i < params.size(); i++) {
            params.get(i).setValue(paramTfs.get(i).getText().trim());
        }

        return params;
    }

    public long getValue() {
        try {
            return Long.parseLong(valueTf.getText().trim());
        } catch(Exception e) {
            return 0;
        }
    }

    public String getContractAddress() {
        if(useLastDeployedContractCB.isSelected())
            return null;
        else
            return contractTf.getText().trim();
    }

}
