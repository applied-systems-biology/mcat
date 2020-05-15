package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datainterfaces.MCATClusteredPlotGenerationInput;
import org.hkijena.mcat.api.datainterfaces.MCATClusteredPlotGenerationOutput;

/**
 * Plots that are generated for each generated cluster of a preprocessing data group
 */
public class MCATClusteredPlotGenerationAlgorithm extends MCATAlgorithm {

    private final MCATClusteredPlotGenerationInput input;
    private final MCATClusteredPlotGenerationOutput output;

    public MCATClusteredPlotGenerationAlgorithm(MCATRun run, MCATClusteredPlotGenerationInput input, MCATClusteredPlotGenerationOutput output) {
        super(run);
        this.input = input;
        this.output = output;
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

    public MCATClusteredPlotGenerationInput getInput() {
        return input;
    }

    public MCATClusteredPlotGenerationOutput getOutput() {
        return output;
    }
}
