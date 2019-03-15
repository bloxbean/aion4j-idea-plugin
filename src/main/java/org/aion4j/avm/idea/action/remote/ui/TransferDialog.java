package org.aion4j.avm.idea.action.remote.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import org.aion4j.avm.idea.action.remote.NrgConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.ArrayList;
import java.util.List;

public class TransferDialog extends DialogWrapper {
    private JTextField toAccountTf;
    private JPanel mainPanel;
    private JTextField fromAccountTf;
    private JTextField passwordTf;
    private JTextField privateKeyTf;
    private JTextField nrgTf;
    private JTextField nrgPriceTf;
    private JTextField valueTf;

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
        fromAccountTf.getDocument().addDocumentListener(l);

        nrgTf.setText(String.valueOf(NrgConstants.defaultTransferNrg));
        nrgPriceTf.setText(String.valueOf(NrgConstants.defaultTransferNrgPrice));
        valueTf.setText("0");
    }

    private void doValidateInput() {
        List<String> errors = new ArrayList();

        if(!StringUtil.isEmptyOrSpaces(fromAccountTf.getText()) && (!fromAccountTf.getText().startsWith("0xa0")
                && !fromAccountTf.getText().startsWith("a0"))) {
            errors.add("fromAccount");
        }

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
            Long.parseLong(valueTf.getText());
        } catch(NumberFormatException e) {
            errors.add("Amount");
        }

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

    public String getPassword() {
        return passwordTf.getText().trim();
    }

    public String getPrivateKey() {
        return privateKeyTf.getText().trim();
    }

    public String getFromAccount() {
        return fromAccountTf.getText().trim();
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
