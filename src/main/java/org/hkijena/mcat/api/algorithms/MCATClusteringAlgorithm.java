package org.hkijena.mcat.api.algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer.EmptyClusterStrategy;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
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
import net.sf.ij_plugins.clustering.KMeans2D;

public class MCATClusteringAlgorithm extends MCATPerSampleAlgorithm {
	
	private int nSeeds = 50;
	private float tolerance = 0.0001f;
	private int maxWidth = -1, maxHeight = -1, minLength = -1, k = -1;
	private String[] names;
	private ImagePlus[] imps;
	private KMeans2D kMeans;
	private List<DoublePoint> points = new ArrayList<DoublePoint>(); //TODO should not be class variable
	
    public MCATClusteringAlgorithm(MCATRunSample sample) {
        super(sample);
    }
    
    /*
	 * put all images from group in list
	 * crop time range to minLength
	 * store max width and height of images
	 */
    private void loadImages(){
    	Set<String> keys = getSample().getSubjects().keySet();
    	names = new String[keys.size()];
    	imps = new ImagePlus[keys.size()];
    	
    	System.out.println("loading images");
    	
    	for (int i = 0; i < keys.size(); i++) {
    		MCATRunSampleSubject samp = getSample().getSubjects().get(keys.toArray()[i]);
    		System.out.println("Subject: " + samp.getName());
    		names[i] = samp.getName();

    		ImagePlus imp = samp.getPreprocessedDataInterface().getPreprocessedImage().getData().getImage();
    		ImageStack is = imp.getStack();
    		
    		int width = imp.getWidth();
    		int height = imp.getHeight();
//    		int pixSum = width * height;
//    		int slices = imp.getNSlices();
    		
    		for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					float[] tmp = is.getVoxels(x, y, 0, 1, 1, minLength, new float[minLength]);
					double[] pixels = new double[tmp.length];
					IntStream.range(0, tmp.length).forEach(index -> pixels[index] = tmp[index]);
					
					points.add(new DoublePoint(pixels));
				}
			}
    		
//    		imp.setSlice(0);
//    		float[] pixels = (float[]) imp.getProcessor().getPixels();
//    		
//    		imp.getProcessor().convertToFloat();
//    		
//    		for (int j = 1; j < slices; j++) {
//				imp.setSlice(j);
//				pixels = ArrayUtils.addAll(pixels, (float[]) imp.getProcessor().getPixels());
//			}
//    		
////    		System.out.println("imp dim: " + (pixSum * slices) + " pixels dim: " + pixels.length);
//    		
//    		
//    		for (int j = 0; j < width * height; j++) {
//				//get all pixels for time series of this position
//    			double[] timeArray = new double[minLength];
//    			for (int k = 0; k < minLength; k++) {
//					timeArray[k] = pixels[k * pixSum + j];
//				}
//    			
//    			points.add(new DoublePoint(timeArray));
//			}
    		
//    		System.out.println("finished pixel processing");
    		
    		imp.setTitle(samp.getName());
    		imps[i] = new SubstackMaker().makeSubstack(imp, "1-" + minLength);
    		imps[i].setTitle(samp.getName());
    		
    		samp.getPreprocessedDataInterface().getPreprocessedImage().setData(new HyperstackData(imps[i]));

    		if(maxWidth < width)
    			maxWidth = width;
    		if(maxHeight < height)
    			maxHeight = height;
    	}
    }

    /*
//	 * increase canvas of all images to maxWidth and maxHeight
//	 * combine into one stack with all images aligned horizontally
//	 * (this is cheating because other kMeans Clustering algorithms were to slow, but would be nice to change this)
//	 */
//    private ImageStack getCombinedStack() {
//    	
//    	System.out.println(imps.length);
//    	for (ImagePlus imp : imps) {
//    		IJ.run(imp, "Canvas Size...", "width=" + maxWidth + " height=" + maxHeight + " position=Center zero");
//    		imp.show();
//    	}
//    	
//    	System.out.println("in combine nSlices: " + imps[0].getNSlices() + " frames: " + imps[0].getNFrames());
//    	
//    	ImageStack combinedStack;
//    	if(imps.length == 1)
//    		combinedStack = imps[0].getImageStack();
//    	else {
//    		combinedStack = new StackCombiner().combineHorizontally(imps[0].getImageStack(), imps[1].getImageStack());
//        	for (int i = 2; i < imps.length; i++) {
//        		combinedStack = new StackCombiner().combineHorizontally(combinedStack, imps[i].getImageStack());
//        	}
//    	}
//    	
//    	System.out.println("in combine 2 nSlices: " + imps[0].getNSlices() + " frames: " + imps[0].getNFrames());
//    	
//    	return combinedStack;
//    }
    
    private void runKMeans(){
    	
    	System.out.println("   performing kmeans on double points");
    	
    	KMeansPlusPlusClusterer<DoublePoint> kmpp = new KMeansPlusPlusClusterer<DoublePoint>(k, 50, new EuclideanDistance(), new JDKRandomGenerator(nSeeds), EmptyClusterStrategy.FARTHEST_POINT);
    	
    	List<CentroidCluster<DoublePoint>> centroids = kmpp.cluster(points);
    	
    	for (CentroidCluster<DoublePoint> centroidCluster : centroids) {
			System.out.println(centroidCluster.getCenter());
		}
    	
    	
    	//TODO
    	// cluster individual images accordingly
    	
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
    	
    	System.out.println("   finished");
    	
//    	/*
//    	 * set kmeans config params
//    	 */
//    	KMeansConfig kmc = new KMeansConfig();
//    	kmc.setClusterAnimationEnabled(false);
//    	kmc.setNumberOfClusters(k);
//    	kmc.setPrintTraceEnabled(false);
//    	kmc.setRandomizationSeed(nSeeds);
//    	kmc.setRandomizationSeedEnabled(true);
//    	kmc.setTolerance(tolerance);
//
//    	kMeans = new KMeans2D(kmc);
//    	kMeans.run(stack);
//    	
//    	/*
//    	 * get results image with all clustered images
//    	 */
//    	ImageStack clusters = kMeans.getCentroidValueImage();
//    	ImagePlus res = new ImagePlus("clusters", clusters);
//    	res.setSlice(0);
//    	res = res.crop();
//    	res.show();
//    	
//    	
//    	return res;
//    	
//    	
    }
    
    /*
	 * split clustered image stack into single images of individual animals
	 * combine into z-stack with animal name being the slice label
	 */
    private ImageStack getClusteredImages(ImagePlus imp) {
    	ImageStack clusteredImages = new ImageStack(maxWidth, maxHeight);
		for (int i = 0; i < imps.length; i++) {
			int posX = i * maxWidth;
			IJ.run(imp, "Specify...", "width=" + maxWidth + " height=" + maxHeight + " x=" + posX + " y=0");
//			System.out.println("Mul: " + maxWidth * maxHeight);
			
			//TODO set colors according to clusters
			ImagePlus slice = imp.duplicate();
//			System.out.println("Pixels: " + slice.getProcessor().getPixelCount());
			clusteredImages.addSlice(names[i], slice.getProcessor(), i);
		}
		
		return clusteredImages;
    }
    
    private void saveData(float[][] centroids, ImageStack clusteredImages) {
//    	/*
//    	 * save cluster centers
//    	 */
//    	getSample().getClusteredDataInterface().getClusterCenters().setData(new ClusterCentersData(centroids));
//    	System.out.println(getSample().getClusteredDataInterface().getClusterCenters().getData());
//    	Path storageFilePathCenters = getSample().getClusteredDataInterface().getClusterCenters().getStorageFilePath();
//    	String outNameCenters = getSample().getName() + "_clustereCenters.csv";
//    	try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(storageFilePathCenters.toString() + System.getProperty("file.separator") + outNameCenters)));
//			bw.write(getSample().getClusteredDataInterface().getClusterCenters().getData().toString());
//			bw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    	
//    	//TODO convert image to 8-bit
//    	
//    	/*
//    	 * save clustered images
//    	 */
//    	getSample().getClusteredDataInterface().getClusterImages().setData(new HyperstackData(new ImagePlus("ClusteredImages", clusteredImages)));
//    	Path storageFilePathClusters = getSample().getClusteredDataInterface().getClusterImages().getStorageFilePath();
//    	String outNameClusters = getSample().getName() + "_clusteredImages.tif";
//    	IJ.save(new ImagePlus("ClusteredImages",clusteredImages), storageFilePathClusters.toString() + System.getProperty("file.separator") + outNameClusters);
    	
    	
    	Set<String> keys = getSample().getSubjects().keySet();
    	
    	for (String key : keys) {
    		MCATRunSampleSubject samp = getSample().getSubjects().get(key);
    	
    		Path storageFilePathClusteredImage = getSample().getClusteredDataInterface().getClusterImages().getStorageFilePath();
    		String outNameClusteredImage = samp.getName() + "_clusteredImage.tif";
    		IJ.save(samp.getClusteredDataInterface().getSingleClusterImage().getData().getImage(), 
    				storageFilePathClusteredImage.toString() + System.getProperty("file.separator") + outNameClusteredImage);
    		
    	}
    	
    }
    
    @Override
    public void run() {
    	System.out.println("Starting Clustering for group");

    	k = getRun().getClusteringParameters().getkMeansK();
    	minLength = getRun().getClusteringParameters().getMinLength() - 1; //subtract one because of differences in indexing and slice number measurement

    	loadImages();
    	
//    	ImageStack combinedStack = getCombinedStack();
    	
    	System.out.println("after combine nSlices: " + imps[0].getNSlices() + " frames: " + imps[0].getNFrames());
    	
    	runKMeans();
    	
//    	ImagePlus clustersImage = runKMeans(combinedStack);
//    	ImageStack clusteredImages = getClusteredImages(clustersImage);
    	
//    	float[][] centroids = kMeans.getClusterCenters();

    	saveData(null, null);
    	
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
