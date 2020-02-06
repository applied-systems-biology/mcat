package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATPerSampleAlgorithm;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATValidityReport;

public class MCATClusteringAlgorithm extends MCATPerSampleAlgorithm {

    public MCATClusteringAlgorithm(MCATRunSample sample) {
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
        return "Clustering " + getSample().getName();
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return new MCATValidityReport(this, "Clustering", true, "");
    }
}
