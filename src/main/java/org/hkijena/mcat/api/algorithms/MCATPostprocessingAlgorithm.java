package org.hkijena.mcat.api.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATPerSampleAlgorithm;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.ClusterAbundanceData;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;

import com.google.common.collect.BiMap;

public class MCATPostprocessingAlgorithm extends MCATPerSampleAlgorithm {
	
	// compensate for rounding errors when checking which curves have net decrease/increase; omit zero line
	private final double epsilon = 0.1;
	
	private final int maxDecrease = 1;
	private final int maxIncrease = 2;
	private final int netDecrease = 4;
	private final int netIncrease = 8;
	private int mode = 0;
	
	private List<MCATCentroidCluster<DoublePoint>> clusterCenters;
	
    public MCATPostprocessingAlgorithm(MCATRunSample sample) {
        super(sample);
    }
    
    private double[] addDoubleArrays(double[] a1, double[] a2) {
    	if(a1.length != a2.length)
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
		double[] cumCurve = new double[a.length+1];
		cumCurve[0] = 0;
		for (int i = 0; i < a.length; i++) {
			cumCurve[i+1] = cumCurve[i] + a[i];
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
    	if((mode & maxDecrease) != 0)
    		postProcessMaxDecrease(clusterCenters);
    		
    	if((mode & maxIncrease) != 0)
    		postProcessMaxIncrease(clusterCenters);
    	
    	if((mode & netDecrease) != 0)
    		postProcessNetDecrease(clusterCenters);
    	
    	if((mode & netIncrease) != 0)
    		postProcessNetIncrease(clusterCenters);
    }
    
    private void postProcessMaxDecrease(List<MCATCentroidCluster<DoublePoint>> clusterCenters) {
    	System.out.println("maxDecrease");
		
    	ArrayList<Integer> indices = new ArrayList<Integer>();
    	
		double min = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < clusterCenters.size(); i++) {
			double centerMin = clusterCenters.get(i).getMinValue();
			if(min > centerMin) {
				min = centerMin;
				index = i;
			}		
		}
		if(index == -1)
			throw new IllegalArgumentException("No cluster with max decrease found. Please select other post-processing type.");
	
		System.out.println("   max decrease curve: " + index);
//		System.out.println("   cluster abundance: " + clusterCenters.get(index).getAbundance());
		
		indices.add(index);
		
		getAUC(indices);
	}
    
    private void postProcessMaxIncrease(List<MCATCentroidCluster<DoublePoint>> clusterCenters) {
    	System.out.println("maxIncrease");
		
    	ArrayList<Integer> indices = new ArrayList<Integer>();
    	
		double max = 0;
		int index = -1;
		for (int i = 0; i < clusterCenters.size(); i++) {
			double centerMax = clusterCenters.get(i).getMaxValue();
			if(max < centerMax) {
				max = centerMax;
				index = i;
			}		
		}
		if(index == -1)
			throw new IllegalArgumentException("No cluster with max increase found. Please select other post-processing type.");
	
		System.out.println("   max increase curve: " + index);
//		System.out.println("   cluster abundance: " + clusterCenters.get(index).getAbundance());
		
		indices.add(index);
		
		getAUC(indices);
    }
    
    private void postProcessNetDecrease(List<MCATCentroidCluster<DoublePoint>> clusterCenters) {
    	System.out.println("netDecrease");
		
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < clusterCenters.size(); i++) {
//			System.out.println("curve " + i + " cumSum: " + clusterCenters.get(i).getCumSum() + 
//					" abundance: " + clusterCenters.get(i).getAbundance() + 
//					" mean: " + clusterCenters.get(i).getMeanDifferenceFromZero());
			if(clusterCenters.get(i).getCumSum() < 0 && Math.abs(clusterCenters.get(i).getMeanDifferenceFromZero()) > epsilon) {
				indices.add(i);
			}		
		}
		
		if(!(indices.size() > 0))
			throw new IllegalArgumentException("No cluster centers with net decrease found. Please select other post-processing type.");
	
		System.out.println("net decrease curves: " + indices.size());
		
		getAUC(indices);
    }

    private void postProcessNetIncrease(List<MCATCentroidCluster<DoublePoint>> clusterCenters) {
    	System.out.println("netIncrease");
		
    	ArrayList<Integer> indices = new ArrayList<Integer>();
    
		for (int i = 0; i < clusterCenters.size(); i++) {
//			System.out.println("curve " + i + " cumSum: " + clusterCenters.get(i).getCumSum() + 
//					" abundance: " + clusterCenters.get(i).getAbundance() + 
//					" mean: " + clusterCenters.get(i).getMeanDifferenceFromZero());
			if(clusterCenters.get(i).getCumSum() > 0 && Math.abs(clusterCenters.get(i).getMeanDifferenceFromZero()) > epsilon) {
				indices.add(i);
			}		
		}
		if(!(indices.size() > 0))
			throw new IllegalArgumentException("No cluster centers with net increase found. Please select other post-processing type.");
	
		System.out.println("net increase curves: " + indices.size());
		
		getAUC(indices);
	}
    
    private void getAUC(ArrayList<Integer> indices) {
    	System.out.println("Getting AUCS...");
    	
    	Set<String> keys = getSample().getSubjects().keySet();
    	
    	for (String key : keys) {
    		MCATRunSampleSubject samp = getSample().getSubjects().get(key);
    		
    		ClusterAbundanceData clusterAbundance = samp.getClusterAbundanceDataInterface().getClusterAbundance().getData();
    		
    		System.out.println(samp.getName());
    		
    		int sumAbundance = 0;
    		double[] weightedAverage = new double[getRun().getClusteringParameters().getMinLength() - 1];
    		for (Integer index : indices) {
    			int abun = clusterAbundance.getAbundance()[index];
				sumAbundance += abun;
				
				double[] weighted = clusterAbundance.getCentroids().get(index).multiply(abun);
				weightedAverage = addDoubleArrays(weightedAverage, weighted);
    		}
    		
    		weightedAverage = divideDoubleArray(weightedAverage, sumAbundance);
    		double[] cumCurve = getCumulativeCurve(weightedAverage);
    		double auc = getAucValue(weightedAverage);
    		double aucCum = getAucValue(cumCurve);
    		System.out.println("    AUC: " + auc);
    		System.out.println("    AUC cum: " + aucCum);
    		
    	}
    }

    @Override
    public void run() {
    	
    	System.out.println("Postprocessing: " + getSample().getName());
    	
    	 clusterCenters = getSample().getClusteredDataInterface().getClusterCenters().getData().getCentroids();
    	
    	if(getSample().getRun().getPostprocessingParameters().isAnalyzeMaxDecrease())
    		mode = mode | maxDecrease;
    	
    	if(getSample().getRun().getPostprocessingParameters().isAnalyzeMaxIncrease())
    		mode = mode | maxIncrease;
    	
    	if(getSample().getRun().getPostprocessingParameters().isAnalyzeNetDecrease())
    		mode = mode | netDecrease;
    	
    	if(getSample().getRun().getPostprocessingParameters().isAnalyzeNetIncrease())
    		mode = mode | netIncrease;
    	
    	
    	postProcess();
    	
    }

    @Override
    public String getName() {
        return "Postprocessing " + getSample().getName();
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return new MCATValidityReport(this, "Postprocessing", true, "");
    }
}
