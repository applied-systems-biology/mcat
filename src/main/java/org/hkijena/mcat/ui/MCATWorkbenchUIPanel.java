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

import org.hkijena.mcat.api.MCATProject;

import javax.swing.*;

/**
 * {@link JPanel} that contains a reference to a {@link MCATWorkbenchUI}
 */
public class MCATWorkbenchUIPanel extends JPanel {

    private final MCATWorkbenchUI workbenchUI;

    public MCATWorkbenchUIPanel(MCATWorkbenchUI workbenchUI) {
        this.workbenchUI = workbenchUI;
    }

    public MCATWorkbenchUI getWorkbenchUI() {
        return workbenchUI;
    }

    public MCATProject getProject() {
        return getWorkbenchUI().getProject();
    }
}
