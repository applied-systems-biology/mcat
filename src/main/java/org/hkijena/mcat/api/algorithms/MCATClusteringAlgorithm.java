package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATValidityReport;

public class MCATClusteringAlgorithm extends MCATAlgorithm {

    public MCATClusteringAlgorithm(MCATRunSample sample) {
        super(sample);
    }

    @Override
    public void run() {
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
