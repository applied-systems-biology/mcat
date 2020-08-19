/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/

package org.hkijena.mcat.ui;

import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Allows adding one or multiple samples manually by providing name(s)
 */
public class MCATAddProjectDataSetsDialog extends JDialog implements WindowListener {

    private MCATWorkbenchUI workbenchUI;
    private JTextArea samplesInput;

    public MCATAddProjectDataSetsDialog(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        this.workbenchUI = workbenchUI;
        initialize();
        addWindowListener(this);
    }

    private void initialize() {
        setSize(400, 300);
        getContentPane().setLayout(new BorderLayout(8, 8));
        setTitle("Add data sets");
        setIconImage(UIUtils.getIconFromResources("module.png").getImage());

        JTextArea infoArea = new JTextArea("Please insert the name of the data set. You can also add multiple data sets at once by writing multiple lines. Each line represents one data set.");
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setBorder(null);
        infoArea.setWrapStyleWord(true);
        infoArea.setLineWrap(true);
        add(infoArea, BorderLayout.NORTH);

        samplesInput = new JTextArea();
        add(new JScrollPane(samplesInput), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        buttonPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addFromInput());
        buttonPanel.add(addButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFromInput() {
        if (samplesInput.getText() != null && !samplesInput.getText().isEmpty()) {
            for (String line : samplesInput.getText().split("\n")) {
                String modified = line.trim();
                if (!modified.isEmpty()) {
                    if (!workbenchUI.getProject().getDataSets().containsKey(modified)) {
                        workbenchUI.getProject().addSample(modified);
                    }
                }
            }
        }
        setVisible(false);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        samplesInput.requestFocus();
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
