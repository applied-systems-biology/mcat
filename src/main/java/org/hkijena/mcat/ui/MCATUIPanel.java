package org.hkijena.mcat.ui;

import javax.swing.*;

public class MCATUIPanel extends JPanel {

    private final MCATWorkbenchUI workbenchUI;

    public MCATUIPanel(MCATWorkbenchUI workbenchUI) {
        this.workbenchUI = workbenchUI;
    }

    public MCATWorkbenchUI getWorkbenchUI() {
        return workbenchUI;
    }
}
