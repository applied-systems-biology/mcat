package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATValidityReport;

public class MCATPostprocessingAlgorithm extends MCATAlgorithm {

    public MCATPostprocessingAlgorithm(MCATRunSample sample) {
        super(sample);
    }

    @Override
    public void run() {

    }

    @Override
    public String getName() {
        return "Postprocessing " + getSample().getName();
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return new MCATValidityReport(this, "Postprocessing", true, "");
    }
}
