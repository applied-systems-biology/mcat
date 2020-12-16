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
package org.hkijena.mcat.extension.datatypes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDocumentation;

/**
 * Contains cluster centers
 */
@MCATDocumentation(name = "Cluster centers")
public class ClusterCentersData implements MCATData {

    List<MCATCentroidCluster<DoublePoint>> clusterCenters;

    public ClusterCentersData(List<MCATCentroidCluster<DoublePoint>> centroids) {
        super();
        this.clusterCenters = centroids;
    }

    public List<MCATCentroidCluster<DoublePoint>> getCentroids() {
        return clusterCenters;
    }

    public void setCentroids(List<MCATCentroidCluster<DoublePoint>> centroids) {
        this.clusterCenters = centroids;
    }

    @Override
    public String toString() {
        String centroidsString = "";

        for (MCATCentroidCluster<DoublePoint> centroidCluster : clusterCenters) {
            centroidsString += centroidCluster.getCenter().toString() + System.lineSeparator();
        }
        centroidsString = centroidsString.replace("[", "").replace("]", "");

        return centroidsString;
    }

    @Override
    public void saveTo(Path folder, Path fileName) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder.resolve(fileName).toString())));
            bw.write(this.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
