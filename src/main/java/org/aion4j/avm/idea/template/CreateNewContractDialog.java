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

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.aion4j.avm.idea.template.service.ContractParam;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.ui.ListCellRendererWrapper.createSeparator;

public class CreateNewContractDialog extends DialogWrapper {

    private String method;
    private List<JTextField> paramTfs = new ArrayList<>();
    private JTextField classTf;

    private List<ContractParam> params = new ArrayList<>();

    public CreateNewContractDialog(String contractName, List<ContractParam> params) {
        super(false);

        this.method = method;
        this.params = params;
        init();
        setTitle(contractName);
    }

    private void initComponents() {
        for(ContractParam param: params) {
            JTextField paramTf = new JTextField();
            paramTfs.add(paramTf);

            if(!StringUtil.isEmptyOrSpaces(param.getDefaultValue())) {
                paramTf.setText(param.getDefaultValue());
            }
        }

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
        Font font = panel.getFont();

        JLabel classLabel = new JLabel("Contract Class");
        classLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

        classTf =  new JTextField();
        panel.add(classLabel, cc.xy (1,  1  ));
        panel.add(classTf,                 cc.xyw(3,  1, 5));
        panel.add(new JLabel(" " ),  cc.xy(9,  1));

        if(params.size() != 0)
            panel.add(createSeparator("Contract Parameters"),  cc.xyw(1,  3, 9));

        int row = 5;

        for(int i=0; i < params.size(); i++) {
            String paramType = params.get(i).getType();
            if(params.get(i).isArray())
                paramType += "[]";
            else if(params.get(i).is2DArray())
                paramType += "[][]";

            row += 2;
            JLabel paramLabel = new JLabel(params.get(i).getTitle());
            paramLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

            panel.add(paramLabel, cc.xy (1,  row  ));
            panel.add(paramTfs.get(i),                 cc.xyw(3,  row, 5));
            panel.add(new JLabel("( " + paramType  + " )" ),  cc.xy(9,  row));
        }

        return panel;

    }

    public List<ContractParam> getParamsWithValues() {
        for(int i=0; i < params.size(); i++) {
            params.get(i).setValue(paramTfs.get(i).getText().trim());
        }

        return params;
    }

    public String getClassName() {
        return classTf.getText().trim();
    }

}
