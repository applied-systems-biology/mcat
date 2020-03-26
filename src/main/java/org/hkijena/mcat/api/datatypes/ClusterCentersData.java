package org.hkijena.mcat.api.datatypes;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATData;

import ij.IJ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Contains cluster centers
 */
public class ClusterCentersData extends MCATData {
	
	List<CentroidCluster<DoublePoint>> clusterCenters;
	
    public ClusterCentersData(List<CentroidCluster<DoublePoint>> centroids) {
		super();
		this.clusterCenters = centroids;
	}

	public List<CentroidCluster<DoublePoint>> getCentroids() {
		return clusterCenters;
	}

	public void setCentroids(List<CentroidCluster<DoublePoint>> centroids) {
		this.clusterCenters = centroids;
	}

	@Override
	public String toString() {
		String centroidsString = "";
		
		for (CentroidCluster<DoublePoint> centroidCluster : clusterCenters) {
			centroidsString+= centroidCluster.getCenter().toString() + System.lineSeparator();
		}
		centroidsString = centroidsString.replace("[", "").replace("]", "");
		
		return centroidsString;
	}

	@Override
    public void saveTo(Path folder, String name, String identifier) {
		System.out.println("folder: " + folder + System.lineSeparator() + "name: " + name);
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder.resolve(name + identifier + ".csv").toString())));
			bw.write(this.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
