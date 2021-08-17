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

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;

public class MCATCentroidCluster<T extends Clusterable> extends Cluster<T> {

    private static final long serialVersionUID = 2948234966776902823L;
    private final Clusterable center;
    private double cumSum;
    private long abundance;

    public MCATCentroidCluster(final Clusterable center) {
        super();
        this.center = center;
        setCumSum();
        abundance = 0;
    }

    public Clusterable getCenter() {
        return center;
    }

    public void setCumSum() {
        cumSum = 0;
        double[] points = center.getPoint();
        for (int i = 0; i < points.length; i++) {
            cumSum += points[i];
        }
    }

    public double getCumSum() {
        return this.cumSum;
    }

    public long getAbundance() {
        return abundance;
    }

    public void setAbundance(long abundance) {
        this.abundance = abundance;
    }

    public void addMembers(int num) {
        this.abundance += num;
    }

    public void addMember() {
        this.abundance++;
    }

    public double getMeanValue() {
        double mean = 0;
        double[] points = center.getPoint();
        for (int i = 0; i < points.length; i++) {
            mean += points[i];
        }

        return mean / points.length;
    }

    public double getMeanDifferenceFromZero() {
        double diff = 0;
        double[] points = center.getPoint();
        for (int i = 0; i < points.length; i++) {
            diff += Math.abs(points[i]);
        }

        return diff / points.length;
    }

    public double[] multiply(double scalar) {
        double[] points = center.getPoint();
        double[] res = new double[points.length];
        for (int i = 0; i < points.length; i++) {
            res[i] = points[i] * scalar;
        }

        return res;
    }

    public double getMaxValue() {
        double max = 0;
        double[] points = center.getPoint();
        for (int i = 0; i < points.length; i++) {
            if (max < points[i])
                max = points[i];
        }

        return max;
    }

    public double getMinValue() {
        double min = Double.MAX_VALUE;
        double[] points = center.getPoint();
        for (int i = 0; i < points.length; i++) {
            if (min > points[i])
                min = points[i];
        }

        return min;
    }
}
