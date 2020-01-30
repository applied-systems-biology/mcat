package org.hkijena.mcat.api.parameters;

import org.hkijena.mcat.api.MCATClusteringHierarchy;
import org.hkijena.mcat.api.MCATParameters;

/**
 * Data class that contains all clustering parameters
 */
public class MCATClusteringParameters extends MCATParameters {
    private int kMeansK = 3;
    private MCATClusteringHierarchy clusteringHierarchy = MCATClusteringHierarchy.PerTreatment;

    public MCATClusteringHierarchy getClusteringHierarchy() {
        return clusteringHierarchy;
    }

    public void setClusteringHierarchy(MCATClusteringHierarchy clusteringHierarchy) {
        this.clusteringHierarchy = clusteringHierarchy;

    }

    public int getkMeansK() {
        return kMeansK;
    }

    public void setkMeansK(int kMeansK) {
        this.kMeansK = kMeansK;
    }
}
