package org.hkijena.mcat.api;

import org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPostprocessingAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPreprocessingAlgorithm;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages multiple {@link MCATAlgorithm} instances as graph
 */
public class MCATAlgorithmGraph implements MCATValidatable {

    private DefaultDirectedGraph<MCATAlgorithm, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private Set<MCATAlgorithm> algorithms = new HashSet<>();
    private MCATProject project;
    private MCATAlgorithm rootNode;

    public MCATAlgorithmGraph(MCATProject project) {
        this.project = project;
        initialize();
    }

    private void initialize() {
        rootNode = new MCATAlgorithm(null) {
            @Override
            public MCATValidityReport getValidityReport() {
                return new MCATValidityReport(this, "root", true, "");
            }

            @Override
            public void run() {

            }
        };
        for(MCATSample sample : project.getSamples().values()) {
            initializeSample(sample);
        }
    }

    private void initializeSample(MCATSample sample) {
        MCATPreprocessingAlgorithm preprocessingAlgorithm = new MCATPreprocessingAlgorithm(sample);
        MCATClusteringAlgorithm clusteringAlgorithm = new MCATClusteringAlgorithm(sample);
        MCATPostprocessingAlgorithm postprocessingAlgorithm = new MCATPostprocessingAlgorithm(sample);

        insertNode(preprocessingAlgorithm);
        insertNode(clusteringAlgorithm);
        insertNode(postprocessingAlgorithm);

        connect(preprocessingAlgorithm, clusteringAlgorithm);
        connect(clusteringAlgorithm, postprocessingAlgorithm);
        connect(postprocessingAlgorithm, rootNode);
    }

    public void insertNode(MCATAlgorithm algorithm) {
        algorithms.add(algorithm);
    }

    public void removeNode(MCATAlgorithm algorithm) {
        algorithms.remove(algorithm);
    }

    public void connect(MCATAlgorithm source, MCATAlgorithm target) {
        graph.addEdge(source, target);
    }

    public Set<MCATAlgorithm> getNodes() {
        return Collections.unmodifiableSet(algorithms);
    }

    @Override
    public MCATValidityReport getValidityReport() {
        MCATValidityReport report = new MCATValidityReport(this, "Algorithm graph", true, "");
        for(MCATAlgorithm algorithm : algorithms) {
            report.merge(algorithm.getValidityReport(), "Algorithm graph", "Node");
        }
        return report;
    }
}
