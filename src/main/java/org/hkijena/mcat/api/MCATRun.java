package org.hkijena.mcat.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;

public class MCATRun implements MCATValidatable {
    private MCATProject project;
    private MCATAlgorithmGraph graph;
    private BiMap<String, MCATRunSample> samples = HashBiMap.create();

    private MCATPreprocessingParameters preprocessingParameters;
    private MCATClusteringParameters clusteringParameters;
    private MCATPostprocessingParameters postprocessingParameters;

    public MCATRun(MCATProject project) {
        this.project = project;
        this.graph = new MCATAlgorithmGraph(this);
        this.preprocessingParameters = new MCATPreprocessingParameters(project.getPreprocessingParameters());
        this.clusteringParameters = new MCATClusteringParameters(project.getClusteringParameters());
        this.postprocessingParameters = new MCATPostprocessingParameters(project.getPostprocessingParameters());
    }

    public BiMap<String, MCATRunSample> getSamples() {
        return ImmutableBiMap.copyOf(samples);
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return graph.getValidityReport();
    }
}
