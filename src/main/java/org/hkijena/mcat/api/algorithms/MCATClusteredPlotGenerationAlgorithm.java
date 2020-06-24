package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datainterfaces.MCATClusteredPlotGenerationInput;
import org.hkijena.mcat.api.datainterfaces.MCATClusteredPlotGenerationOutput;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringOutput;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;
import org.hkijena.mcat.extension.datatypes.TimeDerivativePlotData;

import java.util.Map;

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
        TimeDerivativePlotData plotData = new TimeDerivativePlotData();
        for (Map.Entry<String, MCATClusteringOutput> entry : getInput().getClusteringOutputMap().entrySet()) {
            ClusterCentersData clusterCentersData = entry.getValue().getClusterCenters().getData(ClusterCentersData.class);

            TimeDerivativePlotData.Series series = new TimeDerivativePlotData.Series();
            series.setGroup(entry.getKey());
            series.setData(clusterCentersData.getCentroids());
            
            plotData.getDataSeries().put(entry.getKey() + ".csv", series);
        }

        output.getTimeDerivativePlot().setData(plotData);
        output.getTimeDerivativePlot().flush();
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
