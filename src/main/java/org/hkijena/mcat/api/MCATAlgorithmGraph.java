package org.hkijena.mcat.api;

import org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPostprocessingAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPreprocessingAlgorithm;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Manages multiple {@link MCATAlgorithm} instances as graph
 */
public class MCATAlgorithmGraph implements MCATValidatable {

    private DefaultDirectedGraph<MCATAlgorithm, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private Set<MCATAlgorithm> algorithms = new HashSet<>();
    private MCATRun run;
    private MCATAlgorithm rootNode;

    public MCATAlgorithmGraph(MCATRun run) {
        this.run = run;
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

            @Override
            public String getName() {
                return "Root";
            }
        };
        insertNode(rootNode);
        for(MCATRunSample sample : run.getSamples().values()) {
            initializeSample(sample);
        }
    }

    private void initializeSample(MCATRunSample sample) {
        MCATClusteringAlgorithm clusteringAlgorithm = new MCATClusteringAlgorithm(sample);
        MCATPostprocessingAlgorithm postprocessingAlgorithm = new MCATPostprocessingAlgorithm(sample);

        insertNode(clusteringAlgorithm);
        insertNode(postprocessingAlgorithm);

        for(MCATRunSampleSubject subject : sample.getSubjects().values()) {
            MCATPreprocessingAlgorithm preprocessingAlgorithm = new MCATPreprocessingAlgorithm(subject);
            insertNode(preprocessingAlgorithm);

            connect(rootNode, preprocessingAlgorithm); // Should be the first algorithm in the chain
            connect(preprocessingAlgorithm, clusteringAlgorithm);
            connect(clusteringAlgorithm, postprocessingAlgorithm);
        }
    }

    public void insertNode(MCATAlgorithm algorithm) {
        algorithms.add(algorithm);
        graph.addVertex(algorithm);
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

    public List<MCATAlgorithm> traverse() {
        GraphIterator<MCATAlgorithm, DefaultEdge> iterator = new TopologicalOrderIterator<>(graph);
        List<MCATAlgorithm> result = new ArrayList<>();
        while(iterator.hasNext()) {
            MCATAlgorithm algorithm = iterator.next();
            result.add(algorithm);
        }
        return result;
    }

    public int size() {
        return graph.vertexSet().size();
    }
}
