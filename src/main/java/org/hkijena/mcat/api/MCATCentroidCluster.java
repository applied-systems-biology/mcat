package org.hkijena.mcat.api;

import java.util.Comparator;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;

public class MCATCentroidCluster<T extends Clusterable> extends Cluster<T> {

	private static final long serialVersionUID = 2948234966776902823L;
	private double cumSum;
	private final Clusterable center;
	
    public MCATCentroidCluster(final Clusterable center) {
        super();
        this.center = center;
        setCumSum();
    }

    public Clusterable getCenter() {
        return center;
    }
	
	public void setCumSum() {
		cumSum = 0;
		double[] points = center.getPoint();
		for (int j = 0; j < points.length; j++) {
			cumSum += points[j];
		}
	}

	public double getCumSum() {
		return this.cumSum;
	}
}
