package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATSample;
import org.hkijena.mcat.api.MCATValidityReport;

public class MCATClusteringAlgorithm extends MCATAlgorithm {

    public MCATClusteringAlgorithm(MCATSample sample) {
        super(sample);
    }

    @Override
    public void run() {

    }

    @Override
    public MCATValidityReport getValidityReport() {
        return new MCATValidityReport(this, "Clustering", true, "");
    }
}
