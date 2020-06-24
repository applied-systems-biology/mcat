package org.hkijena.mcat.api.algorithms;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.*;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringOutput;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringOutputDataSetEntry;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessingOutput;
import org.hkijena.mcat.api.parameters.MCATAUCDataConditions;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.extension.datatypes.AUCData;
import org.hkijena.mcat.extension.datatypes.ClusterAbundanceData;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;
import org.hkijena.mcat.extension.datatypes.ROIData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MCATPostprocessingAlgorithm extends MCATAlgorithm {

    // compensate for rounding errors when checking which curves have net decrease/increase; omit zero line
    private final double epsilon = 0.1;
    private final MCATPostprocessingOutput postprocessingOutput;
    private final MCATPreprocessingParameters preprocessingParameters;
    private final MCATPostprocessingParameters postprocessingParameters;
    private final MCATClusteringParameters clusteringParameters;
    private List<MCATCentroidCluster<DoublePoint>> clusterCenters;
    private MCATClusteringOutput clusteringOutput;
    private AUCData aucData = new AUCData();

    public MCATPostprocessingAlgorithm(MCATRun run,
                                       MCATPreprocessingParameters preprocessingParameters,
                                       MCATPostprocessingParameters postprocessingParameters,
                                       MCATClusteringParameters clusteringParameters,
                                       MCATClusteringOutput clusteringOutput,
                                       MCATPostprocessingOutput postprocessingOutput) {
        super(run);
        this.preprocessingParameters = preprocessingParameters;
        this.postprocessingParameters = postprocessingParameters;
        this.clusteringParameters = clusteringParameters;
        this.clusteringOutput = clusteringOutput;
        this.postprocessingOutput = postprocessingOutput;
    }


    private double[] addDoubleArrays(double[] a1, double[] a2) {
        if (a1.length != a2.length)
            throw new IllegalArgumentException("Error in addDoubleArrays: lengths differ!");

        for (int i = 0; i < a1.length; i++) {
            a1[i] += a2[i];
        }
        return a1;
    }

    private double[] divideDoubleArray(double[] a, double scalar) {
        for (int i = 0; i < a.length; i++) {
            a[i] /= scalar;
        }
        return a;
    }

    private double[] getCumulativeCurve(double[] a) {
        double[] cumCurve = new double[a.length + 1];
        cumCurve[0] = 0;
        for (int i = 0; i < a.length; i++) {
            cumCurve[i + 1] = cumCurve[i] + a[i];
        }
        return cumCurve;
    }

    private double getAucValue(double[] a) {
        double auc = 0;
        for (int i = 0; i < a.length; i++) {
            auc += a[i];
        }
        return auc;
    }

    private void postProcess() {
        if (getPostprocessingParameters().isAnalyzeMaxDecrease())
            postProcessMaxDecrease(clusterCenters);

        if (getPostprocessingParameters().isAnalyzeMaxIncrease())
            postProcessMaxIncrease(clusterCenters);

        if (getPostprocessingParameters().isAnalyzeNetDecrease())
            postProcessNetDecrease(clusterCenters);

        if (getPostprocessingParameters().isAnalyzeNetIncrease())
            postProcessNetIncrease(clusterCenters);
    }

    private void postProcessMaxDecrease(List<MCATCentroidCluster<DoublePoint>> clusterCenters) {
        ArrayList<Integer> indices = new ArrayList<Integer>();

        double min = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < clusterCenters.size(); i++) {
            double centerMin = clusterCenters.get(i).getMinValue();
            if (min > centerMin) {
                min = centerMin;
                index = i;
            }
        }
        try {
        	if (index != -1) {
        		indices.add(index);
        		getAUC(indices, MCATPostprocessingMethod.MaxDecrease);
        	}else
                throw new IllegalArgumentException("No cluster with max decrease found. Please select other post-processing type.");
            
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			//TODO
		}
    }

    private void postProcessMaxIncrease(List<MCATCentroidCluster<DoublePoint>> clusterCenters) {
        ArrayList<Integer> indices = new ArrayList<Integer>();

        double max = 0;
        int index = -1;
        for (int i = 0; i < clusterCenters.size(); i++) {
            double centerMax = clusterCenters.get(i).getMaxValue();
            if (max < centerMax) {
                max = centerMax;
                index = i;
            }
        }
        try {
        	if (index != -1) {
        		indices.add(index);
        		getAUC(indices, MCATPostprocessingMethod.MaxIncrease);
        	}else
                throw new IllegalArgumentException("No cluster with max increase found. Please select other post-processing type.");
            
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			//TODO
		}
    }

    private void postProcessNetDecrease(List<MCATCentroidCluster<DoublePoint>> clusterCenters) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < clusterCenters.size(); i++) {
            if (clusterCenters.get(i).getCumSum() < 0 && Math.abs(clusterCenters.get(i).getMeanDifferenceFromZero()) > epsilon) {
                indices.add(i);
            }
        }
        try {
        	if (indices.size() > 0) {
        		getAUC(indices, MCATPostprocessingMethod.NetDecrease);
        	}else
                throw new IllegalArgumentException("No cluster with net decrease found. Please select other post-processing type.");
            
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			//TODO
		}
    }

    private void postProcessNetIncrease(List<MCATCentroidCluster<DoublePoint>> clusterCenters) {
        System.out.println("netIncrease");

        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < clusterCenters.size(); i++) {
            if (clusterCenters.get(i).getCumSum() > 0 && Math.abs(clusterCenters.get(i).getMeanDifferenceFromZero()) > epsilon) {
                indices.add(i);
            }
        }
        try {
        	if (indices.size() > 0) {
        		getAUC(indices, MCATPostprocessingMethod.NetIncrease);
        	}else
                throw new IllegalArgumentException("No cluster with net increase found. Please select other post-processing type.");
            
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			//TODO
		}
    }

    private void getAUC(ArrayList<Integer> indices, MCATPostprocessingMethod postprocessingMethod) {
        System.out.println("Getting AUCS...");

        Set<String> keys = getClusteringOutput().getDataSetEntries().keySet();

        for (String key : keys) {
            MCATClusteringOutputDataSetEntry samp = getClusteringOutput().getDataSetEntries().get(key);

            ClusterAbundanceData clusterAbundance = samp.getClusterAbundance().getData(ClusterAbundanceData.class);

            int sumAbundance = 0;
            for (int i = 0; i < clusterAbundance.getAbundance().length; i++) {
                sumAbundance += clusterAbundance.getAbundance()[i];
            }

            double[] weightedAverage = new double[getClusteringOutput().getMinLength() - 1];
            for (Integer index : indices) {
                int abun = clusterAbundance.getAbundance()[index];

                double[] weighted = clusterAbundance.getCentroids().get(index).multiply(abun);
                weightedAverage = addDoubleArrays(weightedAverage, weighted);
            }

            weightedAverage = divideDoubleArray(weightedAverage, sumAbundance);

            double[] cumCurve = getCumulativeCurve(weightedAverage);

            double auc = getAucValue(weightedAverage);
            double aucCum = getAucValue(cumCurve);

            // Create the output object
            MCATDataInterfaceKey outputKey = new MCATDataInterfaceKey("auc");
            outputKey.addDataSet(key);
            outputKey.addParameter(getPreprocessingParameters());
            outputKey.addParameter(getClusteringParameters());
            outputKey.addParameter(getPostprocessingParameters());
            outputKey.addParameter(new MCATAUCDataConditions(postprocessingMethod));

            aucData.getAucMap().put(outputKey, new AUCData.Row(auc, aucCum));
        }
    }

    @Override
    public void run() {

        clusterCenters = getClusteringOutput().getClusterCenters()
                .getData(ClusterCentersData.class).getCentroids();

        postProcess();

        System.out.println("treatment: " + getClusteringOutput().getGroupTreatment());
        System.out.println("subject: " + getClusteringOutput().getGroupSubject());

        getPostprocessingOutput().getAuc().setData(aucData);
        getPostprocessingOutput().getAuc().flush();
    }

    @Override
    public String getName() {
        return "postprocessing";
    }

    @Override
    public void reportValidity(MCATValidityReport report) {

    }

    public MCATClusteringOutput getClusteringOutput() {
        return clusteringOutput;
    }

    public MCATPostprocessingOutput getPostprocessingOutput() {
        return postprocessingOutput;
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }

    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }
}
