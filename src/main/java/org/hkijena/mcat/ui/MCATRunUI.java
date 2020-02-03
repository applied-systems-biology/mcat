package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATAlgorithmGraph;
import org.hkijena.mcat.api.MCATValidityReport;

public class MCATRunUI extends MCATUIPanel {

    private MCATAlgorithmGraph algorithmGraph;
    private MCATValidityReport validityReport;

    public MCATRunUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        algorithmGraph = new MCATAlgorithmGraph(workbenchUI.getProject());
        validityReport = algorithmGraph.getValidityReport();
        initialize();
    }

    private void initialize() {

    }


}
