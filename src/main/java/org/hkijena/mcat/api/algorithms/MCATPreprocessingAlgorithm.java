package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATValidityReport;

public class MCATPreprocessingAlgorithm extends MCATAlgorithm {

    public MCATPreprocessingAlgorithm(MCATRunSample sample) {
        super(sample);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Preprocessing " + getSample().getName();
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return new MCATValidityReport(this, "Preprocessing", true, "");
    }
}
