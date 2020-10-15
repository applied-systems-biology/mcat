/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat.api.algorithms;

import java.util.Map;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datainterfaces.MCATClusteredPlotGenerationInput;
import org.hkijena.mcat.api.datainterfaces.MCATClusteredPlotGenerationOutput;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringOutput;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;
import org.hkijena.mcat.extension.datatypes.TimeDerivativePlotData;

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
            // Hier Farben
            series.setColors(entry.getValue().getColors());
            
            plotData.getDataSeries().put(entry.getKey(), series);
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
