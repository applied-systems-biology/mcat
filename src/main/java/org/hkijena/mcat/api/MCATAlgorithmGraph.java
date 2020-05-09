package org.hkijena.mcat.api;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

/**
 * Manages multiple {@link MCATAlgorithm} instances as graph
 */
public class MCATAlgorithmGraph implements MCATValidatable {

    private DefaultDirectedGraph<MCATAlgorithm, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private Set<MCATAlgorithm> algorithms = new HashSet<>();

    public MCATAlgorithmGraph() {
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

    public List<MCATAlgorithm> traverse() {
        GraphIterator<MCATAlgorithm, DefaultEdge> iterator = new TopologicalOrderIterator<>(graph);
        List<MCATAlgorithm> result = new ArrayList<>();
        while (iterator.hasNext()) {
            MCATAlgorithm algorithm = iterator.next();
            result.add(algorithm);
        }
        return result;
    }

    public int size() {
        return graph.vertexSet().size();
    }

    @Override
    public void reportValidity(MCATValidityReport report) {
        for (MCATAlgorithm algorithm : algorithms) {
            report.forCategory(algorithm.toString()).report(algorithm);
        }
    }
}
