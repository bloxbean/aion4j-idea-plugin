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

package org.aion4j.avm.idea.action.remote.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import org.aion4j.avm.idea.action.account.AccountChooser;
import org.aion4j.avm.idea.action.account.model.Account;
import org.aion4j.avm.idea.action.remote.NrgConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Satya
 */
public class TransferDialog extends DialogWrapper {
    private JTextField toAccountTf;
    private JPanel mainPanel;
    private JTextField privateKeyTf;
    private JTextField nrgTf;
    private JTextField nrgPriceTf;
    private JTextField valueTf;
    private JTextField valueAionTf;
    private JButton fromAccountChooser;
    private JButton toAccountChooser;

    public TransferDialog(Project project) {
        super(project, false);
        init();
        setTitle("Enter transfer information");

        DocumentAdapter l = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                doValidateInput();
            }
        };

        toAccountTf.getDocument().addDocumentListener(l);

        nrgTf.setText(String.valueOf(NrgConstants.defaultTransferNrg));
        nrgPriceTf.setText(String.valueOf(NrgConstants.defaultTransferNrgPrice));
        valueTf.setText("0");
        valueAionTf.setText("0");
        valueTf.setEditable(false);

        valueAionTf.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                try {
                    double valueAion = Double.parseDouble(valueAionTf.getText());
                    double valueNAmp = valueAion * Math.pow(10, 18);

                    valueTf.setText(String.format("%.0f", valueNAmp));
                } catch(NumberFormatException ex) {

                }
            }
        });

        fromAccountChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account selectedAccount = AccountChooser.getSelectedAccount(project, true);

                if(selectedAccount != null)
                    privateKeyTf.setText(selectedAccount.getPrivateKey());
            }
        });

        toAccountChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account selectedAccount = AccountChooser.getSelectedAccount(project, true);

                if(selectedAccount != null)
                    toAccountTf.setText(selectedAccount.getAddress());
            }
        });
    }

    private void doValidateInput() {
        List<String> errors = new ArrayList();

        if(!StringUtil.isEmptyOrSpaces(toAccountTf.getText()) && (!toAccountTf.getText().startsWith("0xa0")
                && !toAccountTf.getText().startsWith("a0"))) {
            errors.add("toAccount");
        }

        try {
            Long.parseLong(nrgTf.getText());
        } catch(NumberFormatException e) {
            errors.add("nrg");
        }

        try {
            Long.parseLong(nrgPriceTf.getText());
        } catch(NumberFormatException e) {
            errors.add("nrgPrice");
        }

        try {
            Double.parseDouble(valueAionTf.getText());
        } catch(NumberFormatException e) {
            errors.add("Amount");
        }

//        try {
//            Long.parseLong(valueTf.getText());
//        } catch(NumberFormatException e) {
//            errors.add("Amount");
//        }

        if (errors.isEmpty()) {
            setErrorText(null);
            getOKAction().setEnabled(true);
            return;
        }
        String message = "Please specify valid " + StringUtil.join(errors, ", ");
        setErrorText(message);
        getOKAction().setEnabled(false);
        getRootPane().revalidate();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    public String getToAccount() {
        return toAccountTf.getText().trim();
    }

    public String getPrivateKey() {
        return privateKeyTf.getText().trim();
    }

    public String getNrg() {
        return nrgTf.getText().trim();
    }

    public String getNrgPrice() {
        return nrgPriceTf.getText().trim();
    }

    public String getValue() {
        return valueTf.getText().trim();
    }
}
