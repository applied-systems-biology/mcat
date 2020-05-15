package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringOutput;
import org.hkijena.mcat.api.datainterfaces.MCATPlotGenerationOutput;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessingOutput;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;

public class MCATPlotGenerationAlgorithm extends MCATAlgorithm {

    private final MCATPreprocessingParameters preprocessingParameters;
    private final MCATPostprocessingParameters postprocessingParameters;
    private final MCATClusteringParameters clusteringParameters;

    public MCATPlotGenerationAlgorithm(MCATRun run,
                                       MCATPreprocessingParameters preprocessingParameters,
                                       MCATPostprocessingParameters postprocessingParameters,
                                       MCATClusteringParameters clusteringParameters, MCATClusteringOutput clusteringOutput, MCATPostprocessingOutput postprocessingOutput, MCATPlotGenerationOutput plotGenerationOutput) {
        super(run);
        this.preprocessingParameters = preprocessingParameters;
        this.postprocessingParameters = postprocessingParameters;
        this.clusteringParameters = clusteringParameters;
    }

    @Override
    public void run() {

    }

    @Override
    public String getName() {
        return "generate-plots";
    }

    @Override
    public void reportValidity(MCATValidityReport report) {

    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }

    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }
}
