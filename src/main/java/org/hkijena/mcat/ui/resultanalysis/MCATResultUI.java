/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.ui.resultanalysis;

import org.hkijena.mcat.api.MCATResult;
import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.MCATWorkbenchUIPanel;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;

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
