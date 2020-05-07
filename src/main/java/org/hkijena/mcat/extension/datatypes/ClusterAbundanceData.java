package org.hkijena.mcat.extension.datatypes;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.utils.api.ACAQDocumentation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@ACAQDocumentation(name = "Cluster abundance")
public class ClusterAbundanceData implements MCATData {

    private List<MCATCentroidCluster<DoublePoint>> clusterCenters;
    private int[] abundance;


    public ClusterAbundanceData(List<MCATCentroidCluster<DoublePoint>> centroids) {
        super();
        this.clusterCenters = centroids;
        abundance = new int[centroids.size()];
    }

    public ClusterAbundanceData(List<MCATCentroidCluster<DoublePoint>> centroids, int[] abundance) {
        super();
        this.clusterCenters = centroids;
        this.abundance = abundance;
    }

    public List<MCATCentroidCluster<DoublePoint>> getCentroids() {
        return clusterCenters;
    }

    public void setCentroids(List<MCATCentroidCluster<DoublePoint>> centroids) {
        this.clusterCenters = centroids;
    }


    public int[] getAbundance() {
        return abundance;
    }

    public void setAbundance(int[] abundance) {
        this.abundance = abundance;
    }

    public void incrementAbundance(int index) {
        this.abundance[index]++;
    }

    @Override
    public String toString() {
        String abundanceString = "";

        for (int i = 0; i < clusterCenters.size(); i++) {
            abundanceString = abundanceString + abundance[i] + ": " + clusterCenters.get(i).getCenter().toString() + System.lineSeparator();
        }
        abundanceString = abundanceString.replace("[", "").replace("]", "");

        return abundanceString;
    }

    @Override
    public void saveTo(Path folder, String name, String identifier) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder.resolve(identifier + name + ".csv").toString())));
            bw.write(this.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
