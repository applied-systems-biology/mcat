package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;

/**
 * Plots that are generated for each generated cluster of a preprocessing data group
 */
public class MCATClusteredPlotGenerationAlgorithm extends MCATAlgorithm {

    public MCATClusteredPlotGenerationAlgorithm(MCATRun run) {
        super(run);
    }

    @Override
    public void run() {

    }

    @Override
    public String getName() {
        return "generate-clustered-plots";
    }

    @Override
    public void reportValidity(MCATValidityReport report) {

    }
}
