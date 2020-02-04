package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hkijena.mcat.api.MCATClusteringHierarchy;
import org.hkijena.mcat.api.MCATParameters;

/**
 * Data class that contains all clustering parameters
 */
public class MCATClusteringParameters extends MCATParameters {
    private int kMeansK = 6;
    private MCATClusteringHierarchy clusteringHierarchy = MCATClusteringHierarchy.PerTreatment;

    public MCATClusteringHierarchy getClusteringHierarchy() {
        return clusteringHierarchy;
    }

    public void setClusteringHierarchy(MCATClusteringHierarchy clusteringHierarchy) {
        this.clusteringHierarchy = clusteringHierarchy;

    }

    @JsonGetter("kmeans-k")
    public int getkMeansK() {
        return kMeansK;
    }

    @JsonSetter("kmeans-k")
    public void setkMeansK(int kMeansK) {
        this.kMeansK = kMeansK;
    }
}
