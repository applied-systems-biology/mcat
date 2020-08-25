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
package org.hkijena.mcat.ui.resultanalysis;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.hkijena.mcat.api.MCATResult;
import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.MCATWorkbenchUIPanel;
import org.hkijena.mcat.utils.UIUtils;

public class MCATResultUI extends MCATWorkbenchUIPanel {
    private MCATResult result;

    public MCATResultUI(MCATWorkbenchUI workbenchUI, MCATResult result) {
        super(workbenchUI);
        this.result = result;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        JButton openFolderButton = new JButton("Open output folder", UIUtils.getIconFromResources("open.png"));
        openFolderButton.addActionListener(e -> openOutputFolder());
        toolBar.add(openFolderButton);
        add(toolBar, BorderLayout.NORTH);
    }

    private void openOutputFolder() {
        try {
            Desktop.getDesktop().open(result.getOutputFolder().toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MCATResult getResult() {
        return result;
    }
}
