package org.hkijena.mcat.api.datatypes;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATData;

import java.nio.file.Path;
import java.util.List;

/**
 * Contains cluster centers
 */
public class ClusterCentersData extends MCATData {
	
	float[][] clusterCenters;
	
    public ClusterCentersData(float[][] centroids) {
		super();
		this.clusterCenters = centroids;
	}

	public float[][] getCentroids() {
		return clusterCenters;
	}

	public void setCentroids(float[][] centroids) {
		this.clusterCenters = centroids;
	}

	@Override
	public String toString() {
		String centroidsString = "";
		for (int i = 0; i < clusterCenters.length; i++) {
			for (int j = 0; j < clusterCenters[i].length; j++) {
				if(j == 0)
					centroidsString+= String.format("%.2f", clusterCenters[i][j]);
				else
					centroidsString+= ";" + String.format("%.2f", clusterCenters[i][j]);
			}
			if(i != clusterCenters.length -1)
				centroidsString += System.lineSeparator();
		}
		
		return centroidsString;
	}



	@Override
    public void saveTo(Path folder, String name) {

    }
}
