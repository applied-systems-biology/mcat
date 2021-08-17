/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.api;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public DefaultDirectedGraph<MCATAlgorithm, DefaultEdge> getGraph() {
        return graph;
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
