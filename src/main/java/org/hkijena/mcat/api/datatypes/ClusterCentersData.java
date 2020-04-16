package org.hkijena.mcat.api.datatypes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATData;

/**
 * Contains cluster centers
 */
public class ClusterCentersData extends MCATData {
	
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
			centroidsString+= centroidCluster.getCenter().toString() + System.lineSeparator();
		}
		centroidsString = centroidsString.replace("[", "").replace("]", "");
		
		return centroidsString;
	}

	@Override
    public void saveTo(Path folder, String name, String identifier) {
		System.out.println("folder: " + folder + System.lineSeparator() + "name: " + name);
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder.resolve(identifier + name + ".csv").toString())));
			bw.write(this.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
