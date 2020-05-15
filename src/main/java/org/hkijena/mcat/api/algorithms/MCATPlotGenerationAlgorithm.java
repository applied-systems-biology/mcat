package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATDataInterfaceKey;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringOutput;
import org.hkijena.mcat.api.datainterfaces.MCATPlotGenerationOutput;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessingOutput;
import org.hkijena.mcat.api.parameters.MCATAUCDataConditions;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.extension.datatypes.AUCData;
import org.hkijena.mcat.extension.datatypes.AUCPlotData;

import java.util.Arrays;
import java.util.Map;

public class MCATPlotGenerationAlgorithm extends MCATAlgorithm {

    private final MCATPreprocessingParameters preprocessingParameters;
    private final MCATPostprocessingParameters postprocessingParameters;
    private final MCATClusteringParameters clusteringParameters;
    private MCATAUCDataConditions AUCConditions;
    private final MCATClusteringOutput clusteringOutput;
    private final MCATPostprocessingOutput postprocessingOutput;
    private final MCATPlotGenerationOutput plotGenerationOutput;

    public MCATPlotGenerationAlgorithm(MCATRun run,
                                       MCATPreprocessingParameters preprocessingParameters,
                                       MCATPostprocessingParameters postprocessingParameters,
                                       MCATClusteringParameters clusteringParameters,
                                       MCATAUCDataConditions conditions,
                                       MCATClusteringOutput clusteringOutput,
                                       MCATPostprocessingOutput postprocessingOutput,
                                       MCATPlotGenerationOutput plotGenerationOutput) {
        super(run);
        this.preprocessingParameters = preprocessingParameters;
        this.postprocessingParameters = postprocessingParameters;
        this.clusteringParameters = clusteringParameters;
        this.AUCConditions = conditions;
        this.clusteringOutput = clusteringOutput;
        this.postprocessingOutput = postprocessingOutput;
        this.plotGenerationOutput = plotGenerationOutput;
    }

    @Override
    public void run() {
        generateAUCPlot();
    }

    private void generateAUCPlot() {
        AUCData aucData = getPostprocessingOutput().getAuc().getData(AUCData.class);
        AUCPlotData plotData = new AUCPlotData(Arrays.asList(preprocessingParameters, clusteringParameters, postprocessingParameters, AUCConditions));
        for (Map.Entry<MCATDataInterfaceKey, AUCData.Row> entry : aucData.getAucMap().entrySet()) {
            String subject = String.join(",", entry.getKey().getDataSetNames());
            String treatment = getRun().getProject().getDataSets().get(subject).getParameters().getTreatment();
            plotData.addRow(subject, treatment, entry.getValue().getAuc(), entry.getValue().getAucCum());
        }

        getPlotGenerationOutput().getAucPlotData().setData(plotData);
        getPlotGenerationOutput().getAucPlotData().flush("auc");
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

    public MCATClusteringOutput getClusteringOutput() {
        return clusteringOutput;
    }

    public MCATPostprocessingOutput getPostprocessingOutput() {
        return postprocessingOutput;
    }

    public MCATPlotGenerationOutput getPlotGenerationOutput() {
        return plotGenerationOutput;
    }

    public MCATAUCDataConditions getAUCConditions() {
        return AUCConditions;
    }
}
