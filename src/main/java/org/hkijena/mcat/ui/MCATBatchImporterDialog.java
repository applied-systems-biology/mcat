package org.hkijena.mcat.ui;

import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class MCATBatchImporterDialog extends JDialog {
    private MCATWorkbenchUI workbenchUI;

    public MCATBatchImporterDialog(MCATWorkbenchUI workbenchUI) {
        this.workbenchUI = workbenchUI;
        initialize();
    }

    private void initialize() {
        setSize(400, 300);
        getContentPane().setLayout(new BorderLayout(8, 8));
        setTitle("Batch import samples");
        setIconImage(UIUtils.getIconFromResources("mcat.png").getImage());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        buttonPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);

        JButton addButton = new JButton("Import");
        addButton.addActionListener(e -> runImport());
        buttonPanel.add(addButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void runImport() {
        setVisible(false);
    }
}
