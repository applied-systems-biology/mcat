package org.hkijena.mcat.api.algorithms;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer.EmptyClusterStrategy;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATPerSampleAlgorithm;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;
import org.hkijena.mcat.api.datatypes.HyperstackData;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.SubstackMaker;
import ij.process.ImageConverter;

public class MCATClusteringAlgorithm extends MCATPerSampleAlgorithm {
	
	private int nSeeds = 50;
	private int minLength, k;
	private String[] names;
	private ImagePlus[] imps;
	private List<DoublePoint> points = new ArrayList<DoublePoint>();
	
    public MCATClusteringAlgorithm(MCATRunSample sample) {
        super(sample);
    }
    
    private void loadImages(){
    	System.out.println("\tLoading images...");
    	Set<String> keys = getSample().getSubjects().keySet();
    	names = new String[keys.size()];
    	imps = new ImagePlus[keys.size()];
    	
    	for (int i = 0; i < keys.size(); i++) {
    		MCATRunSampleSubject samp = getSample().getSubjects().get(keys.toArray()[i]);
    		System.out.println("\t\tSubject: " + samp.getName());
    		names[i] = samp.getName();

    		ImagePlus imp = samp.getPreprocessedDataInterface().getPreprocessedImage().getData().getImage();
    		ImageStack is = imp.getStack();
    		
    		int width = imp.getWidth();
    		int height = imp.getHeight();
    		
    		float[] tmp = new float[minLength];
    		
    		for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					tmp = is.getVoxels(x, y, 0, 1, 1, minLength, new float[minLength]);
					double[] pixels = new double[tmp.length];
				    for (int j = 0; j < tmp.length; j++){
				        pixels[j] = tmp[j];
				    }
					
					points.add(new DoublePoint(pixels));
				}
			}
    			
    		imp.setTitle(samp.getName());
    		imps[i] = new SubstackMaker().makeSubstack(imp, "1-" + minLength);
    		imps[i].setTitle(samp.getName());
    		
    		samp.getPreprocessedDataInterface().getPreprocessedImage().setData(new HyperstackData(imps[i]));
    	}
    }

    private void runKMeans(){
    	
    	System.out.println("\tPerforming k-means algorithm...");
    	
    	KMeansPlusPlusClusterer<DoublePoint> kmpp = new KMeansPlusPlusClusterer<DoublePoint>(k, 50, new EuclideanDistance(), new JDKRandomGenerator(nSeeds), EmptyClusterStrategy.FARTHEST_POINT);
    	
    	List<CentroidCluster<DoublePoint>> tmpCentroidCluster = kmpp.cluster(points);
    	
    	List<MCATCentroidCluster<DoublePoint>> centroids = new ArrayList<MCATCentroidCluster<DoublePoint>>();
    	for (CentroidCluster<DoublePoint> centroidCluster : tmpCentroidCluster) {
			centroids.add(new MCATCentroidCluster<DoublePoint>(centroidCluster.getCenter()));
		}
    	
    	Collections.sort(centroids, new Comparator<MCATCentroidCluster<DoublePoint>>() {
			@Override
			public int compare(MCATCentroidCluster<DoublePoint> o1, MCATCentroidCluster<DoublePoint> o2) {
				return Double.compare(o1.getCumSum(), o2.getCumSum());
			}
		});
    	
    	
    	getSample().getClusteredDataInterface().getClusterCenters().setData(new ClusterCentersData(centroids));
    	
    	
    	//TODO assign colors according to cluster center 
    	
    	Set<String> keys = getSample().getSubjects().keySet();
    	
    	for (String key : keys) {
    		MCATRunSampleSubject samp = getSample().getSubjects().get(key);
    		
    		ImagePlus imp = samp.getPreprocessedDataInterface().getPreprocessedImage().getData().getImage();
		
    		ImageStack is = imp.getStack();
    		int w = imp.getWidth();
    		int h = imp.getHeight();
    		int[] clusteredPixels = new int[w*h];
    		
    		for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					
					float[] tmp = is.getVoxels(x, y, 0, 1, 1, minLength, new float[minLength]);
					double[] pixels = new double[tmp.length];
					IntStream.range(0, tmp.length).forEach(index -> pixels[index] = tmp[index]);
					
					double minDist = Double.MAX_VALUE;
					int closestCluster = -1;
					
					for (int i = 0; i < centroids.size(); i++) {
						
						double[] center = centroids.get(i).getCenter().getPoint();
						double dist = new EuclideanDistance().compute(center, pixels);
						if(dist < minDist) {
							minDist = dist;
							closestCluster = i;
						}
					}
					
					if(closestCluster == -1) {
						System.err.println("No closest cluster found for this pixel position (x=" + x + "; y=" + y + ")");
					}
					clusteredPixels[y*w+x] = Math.round(255/k) * closestCluster;
				}
			}
    		ImageStack clusteredStack = new ImageStack(w, h, 1);
    		clusteredStack.setPixels(clusteredPixels, 1);
    		
    		ImagePlus clusteredImage = new ImagePlus(imp.getTitle() + "_clusteredImage", clusteredStack);
    		
    		new ImageConverter(clusteredImage).convertToGray8();
    		clusteredImage.resetDisplayRange();
    		
    		samp.getClusteredDataInterface().getSingleClusterImage().setData(new HyperstackData(clusteredImage));
		}
    }
    
    /*
	 * save cluster centers
	 */
    private void saveData() {
    	System.out.println("\tSaving clustering results...");
    	
    	System.out.println("flush...");
    	String identifier = "_downsampling-" + getSample().getRun().getPreprocessingParameters().getDownsamplingFactor() +
    			"_anatomyCh-" + getSample().getRun().getPreprocessingParameters().getAnatomicChannel() + 
    			"_interestCh-" + getSample().getRun().getPreprocessingParameters().getChannelOfInterest() +
    			"_timeFrames-" + getSample().getRun().getClusteringParameters().getMinLength() +
    			"_k-" + getSample().getRun().getClusteringParameters().getkMeansK();
    	getSample().getClusteredDataInterface().getClusterCenters().flush(identifier);
    	

    	Set<String> keys = getSample().getSubjects().keySet();
    	
    	for (String key : keys) {
    		MCATRunSampleSubject samp = getSample().getSubjects().get(key);
    		
    		Path storageFilePathClusteredImage = getSample().getClusteredDataInterface().getClusterImages().getStorageFilePath();
    		String outNameClusteredImage = samp.getName() + "_clusteredImage.png";
    		IJ.save(samp.getClusteredDataInterface().getSingleClusterImage().getData().getImage(), 
    				storageFilePathClusteredImage.toString() + System.getProperty("file.separator") + outNameClusteredImage);
    		
    	}
    	
    }
    
    @Override
    public void run() {
    	System.out.println("Starting " + getName());

    	k = getRun().getClusteringParameters().getkMeansK();
    	minLength = getRun().getClusteringParameters().getMinLength() - 1; //subtract one because of differences in indexing and slice number measurement

    	loadImages();
    	
    	runKMeans();
    	
    	saveData();
    	
    }

    @Override
    public String getName() {
        return "Clustering " + getSample().getName();
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return new MCATValidityReport(this, "Clustering", true, "");
    }
}
