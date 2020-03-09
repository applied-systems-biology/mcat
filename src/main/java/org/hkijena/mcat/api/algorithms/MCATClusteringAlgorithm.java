package org.hkijena.mcat.api.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATPerSampleAlgorithm;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;
import org.hkijena.mcat.api.datatypes.DerivationMatrixData;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.plugin.StackCombiner;
import ij.plugin.SubstackMaker;
import ij.plugin.frame.RoiManager;
import net.sf.ij_plugins.clustering.KMeans2D;
import net.sf.ij_plugins.clustering.KMeansConfig;

public class MCATClusteringAlgorithm extends MCATPerSampleAlgorithm {
	
	private int nSeeds = 50;
	private float tolerance = 0.0001f;

    public MCATClusteringAlgorithm(MCATRunSample sample) {
        super(sample);
    }

    @Override
    public void run() {
    	System.out.println("Starting Clustering for group");

    	int k = getRun().getClusteringParameters().getkMeansK();
    	int minLength = getRun().getClusteringParameters().getMinLength();

    	Set<String> keys = getSample().getSubjects().keySet();

    	/*
    	 * put all images from group in list
    	 * crop time range to minLength
    	 * store max width and height of images
    	 */
    	int maxWidth = 0;
    	int maxHeight = 0;
    	ImagePlus[] imps = new ImagePlus[keys.size()];
    	int index = 0;

    	for (String key : keys) {
    		MCATRunSampleSubject samp = getSample().getSubjects().get(key);
    		System.out.println("Subject: " + samp.getName());

    		ImagePlus imp = samp.getPreprocessedDataInterface().getPreprocessedImage().getData().getImage();
    		imps[index++] = new SubstackMaker().makeSubstack(imp, "1-" + minLength);

    		int width = imp.getWidth();
    		int height = imp.getHeight();

    		if(maxWidth < width)
    			maxWidth = width;
    		if(maxHeight < height)
    			maxHeight = height;
    	}

    	/*
    	 * increase canvas of all images to maxWidth and maxHeight
    	 * combine into on stack with all images aligned horizontally
    	 * (this is cheating because other kMeans Clustering algorithms were to slow, but would be nice to change this)
    	 */
    	for (ImagePlus imp : imps) {
    		IJ.run(imp, "Canvas Size...", "width=" + maxWidth + " height=" + maxHeight + " position=Center zero");
    	}
    	ImageStack combinedStack = new StackCombiner().combineHorizontally(imps[0].getImageStack(), imps[1].getImageStack());
    	for (int i = 2; i < imps.length; i++) {
    		combinedStack = new StackCombiner().combineHorizontally(combinedStack, imps[i].getImageStack());
    	}

    	/*
    	 * set kmeans config params
    	 */
    	KMeansConfig kmc = new KMeansConfig();
    	kmc.setClusterAnimationEnabled(false);
    	kmc.setNumberOfClusters(k);
    	kmc.setPrintTraceEnabled(false);
    	kmc.setRandomizationSeed(nSeeds);
    	kmc.setRandomizationSeedEnabled(true);
    	kmc.setTolerance(tolerance);

    	KMeans2D km = new KMeans2D(kmc);
    	km.run(combinedStack);

    	/*
    	 * get results image with all clustered images
    	 */
    	ImageStack clusters = km.getCentroidValueImage();
    	ImagePlus res = new ImagePlus("test", clusters);
    	res.setSlice(0);
    	ImagePlus crop = res.crop();
    	crop.show();

    	float[][] centroids = km.getClusterCenters();
    	//TODO save clusterCenters

    	/*
    	 * split clustered image stack into single images of individual animals
    	 */
    	ImagePlus [] clusteredImages = new ImagePlus[imps.length];
    	for (int i = 0; i < imps.length; i++) {
    		int posX = i * maxWidth;
    		IJ.run(crop, "Specify...", "width=" + maxWidth + " height=" + maxHeight + " x=" + posX + " y=0");
    		clusteredImages[i] = crop.duplicate();
    	}
    	//TODO save clustered image, set colors according to clusters
    	for (int i = 0; i < keys.size(); i++) {
    		String key = (String) keys.toArray()[i];
    		MCATRunSampleSubject samp = getSample().getSubjects().get(key);
    	}   	
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
