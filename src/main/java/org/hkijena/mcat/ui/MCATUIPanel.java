package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATProject;

import javax.swing.*;

public class MCATUIPanel extends JPanel {

    private final MCATWorkbenchUI workbenchUI;

    public MCATUIPanel(MCATWorkbenchUI workbenchUI) {
        this.workbenchUI = workbenchUI;
    }

    public MCATWorkbenchUI getWorkbenchUI() {
        return workbenchUI;
    }

    public MCATProject getProject() {
        return getWorkbenchUI().getProject();
    }
}
