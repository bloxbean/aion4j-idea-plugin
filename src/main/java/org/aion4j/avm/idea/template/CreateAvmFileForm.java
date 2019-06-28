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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.aion4j.avm.idea.action.InvokeParam;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.ui.ListCellRendererWrapper.createSeparator;

public class CreateAvmFileForm extends DialogWrapper {

    private JPanel mainPanel;
    private JTextField classTf;
    private JTextField tokenTf;
    private JTextField symbolTf;
    private JTextField granularityTf;
    private JTextField supplyTf;
    private JPanel dyPanel;

    private List<JTextField> paramTfs = new ArrayList<>();
    private List<InvokeParam> params = new ArrayList<>();

    protected CreateAvmFileForm(@Nullable Project project) {
        super(project);
        init();

        paramTfs.add(new JTextField());
        params.add(new InvokeParam("Token Name", "String", "String", false, false, ""));

        paramTfs.add(new JTextField());
        params.add(new InvokeParam("Token Symbol", "String", "String", false, false, ""));

        paramTfs.add(new JTextField());
        params.add(new InvokeParam("Token Granularity", "Int", "Int", false, false, ""));

        paramTfs.add(new JTextField());
        params.add(new InvokeParam("Token Supply", "Int", "Int", false, false, ""));

    }

    private void initComponents() {
        FormLayout layout = new FormLayout(
                "right:max(40dlu;pref), 3dlu, 70dlu, 7dlu, "
                        + "right:max(40dlu;pref), 3dlu, 70dlu, 3dlu, left:max(40dlu;pref)",
                "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");

       // dyPanel = new JPanel(layout);
        dyPanel.setLayout(layout);

        dyPanel.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();

        if(params.size() != 0)
            dyPanel.add(createSeparator("Method Parameters"),  cc.xyw(1,  1, 9));

        int row = 3;
        Font font = dyPanel.getFont();

        for(int i=0; i < params.size(); i++) {
            String paramType = params.get(i).getType();
            if(params.get(i).isArray())
                paramType += "[]";
            else if(params.get(i).is2DArray())
                paramType += "[][]";

            row += 2;
            JLabel paramLabel = new JLabel(params.get(i).getName());
            paramLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

            dyPanel.add(paramLabel, cc.xy (1,  row  ));
            dyPanel.add(paramTfs.get(i),                 cc.xyw(3,  row, 5));
            dyPanel.add(new JLabel("( " + paramType  + " )" ),  cc.xy(9,  row));
        }

        //Contract details section

        row += 2;
        dyPanel.add(createSeparator("Contract Details"),  cc.xyw(1,  row, 7));

        row += 2;
        dyPanel.add(new JLabel("Value (nAmp)"), cc.xy (1,  row  ));
        dyPanel.add(new JTextField(), cc.xyw(3,  row , 5));

        row += 2;
        //Contract address field
        JLabel lastContractCBLabel = new JLabel("Use Last Deployed Contract Address");
        dyPanel.add(new JCheckBox(), cc.xy (1,  row ));
        dyPanel.add(lastContractCBLabel, cc.xyw(3,  row, 5));

        row += 2;
        JLabel contractLabel = new JLabel("Contract Address");
        dyPanel.add(contractLabel, cc.xy (1,  row ));
        dyPanel.add(new JTextField(), cc.xyw(3,  row, 5));

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    public String getClassName() {
        return classTf.getText().trim();
    }

    public String getTokenName() {
        return tokenTf.getText().trim();
    }

    public String getTokenSymbol() {
        return symbolTf.getText().trim();
    }

    public String getTokenGranularity() {
        return granularityTf.getText().trim();
    }

    public String getTokenSupply() {
        return supplyTf.getText().trim();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        initComponents();
    }
}
