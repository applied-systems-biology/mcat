package org.hkijena.mcat.api.algorithms;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.stream.IntStream;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATPerSubjectAlgorithm;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.DerivationMatrixData;
import org.hkijena.mcat.api.datatypes.HyperstackData;

import de.embl.cmci.registration.MultiStackReg_;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.ImageCalculator;
import ij.plugin.Resizer;
import ij.process.ImageStatistics;

public class MCATPreprocessingAlgorithm extends MCATPerSubjectAlgorithm {

    public MCATPreprocessingAlgorithm(MCATRunSampleSubject subject) {
        super(subject);
    }
    
    private ImagePlus registerImages(String transformFile, ImagePlus... imps) {
    	System.out.println("\t\tRegistering channels...");
    	MultiStackReg_ msr = new MultiStackReg_();
    	
    	switch (imps.length) {
		case 1: msr.setSrcImg(imps[0]);
    			msr.setTgtImg(null);
    			msr.setSrcAction("Align");
    			msr.setTgtAction("None");
    			msr.setTransformation(1);
    			msr.setSaveTransform(false);
    			msr.core(transformFile, transformFile);
			
			break;
		case 2: msr.setSrcImg(imps[0]);
    			msr.setTgtImg(imps[1]);
    			msr.setSrcAction("Align");
    			msr.setTgtAction("Load Transformation File");
    			msr.setTransformation(1);
    			msr.setSaveTransform(true);
    			msr.core(transformFile, transformFile);
			break;
		default: System.err.println("Invalid number of images specified for registration: " + imps.length);
			break;
		}
    	
    	return imps[imps.length-1];
    }
    
    private ImagePlus ztransform(ImagePlus imp) {
    	System.out.println("\t\tPerforming z-transformation...");
    	ImageStatistics is = imp.getStatistics();
    	IJ.run(imp, "Subtract...", "value=" + is.mean + " stack");
    	IJ.run(imp, "Divide...", "value=" + is.stdDev + " stack");
    	
    	return imp;
    }
    
    private ImagePlus setOuterPixels(ImagePlus imp, Roi r) {
    	System.out.println("\t\tSetting pixels outside ROI to zero...");
    	imp.setRoi(r);
    	IJ.run(imp, "Make Inverse", "");
    	IJ.run(imp,"Set...", "value=0 stack");
    	imp.setRoi(r);
    	IJ.run(imp,"Crop","");
    	IJ.run(imp,"Select None","");
    	
    	return imp;
    }
    
    private ImagePlus downsample(ImagePlus imp, int factor) {
    	System.out.println("\t\tPerforming downsampling by factor " + factor + "...");
    	int newFrames = imp.getNFrames()/factor;
    	imp = new ij.plugin.Resizer().zScale(imp, newFrames, 1);
    	
    	//check if minimum number of time frames has to be updated
    	int slices = imp.getNSlices();
    	int minLength = getRun().getClusteringParameters().getMinLength();
    	if(slices < minLength)
    		getRun().getClusteringParameters().setMinLength(slices);
    	
    	return imp;
    }

    private ImagePlus toTimeDerivativeImage(ImagePlus imp) {
    	ImagePlus dup = imp.duplicate();
    	dup.getStack().deleteLastSlice();
    	ImagePlus dup2 = imp.duplicate();
    	dup2.getStack().deleteSlice(1);
    	
    	imp = new ImageCalculator().run("Subtract 32-bit stack create", dup2, dup);
    	
    	ImageStatistics is = imp.getStatistics();
    	
    	dup.close();
    	dup2.close();
    	
    	return imp;
    }
    
    private void toTimeDerivativeMatrix(ImagePlus imp) {
    	System.out.println("\tConstructing time derivative matrix...");
    	
		ImageStack is = imp.getStack();
		
		int width = imp.getWidth();
		int height = imp.getHeight();
		int slices = imp.getNSlices() == 1 ? imp.getNFrames():imp.getNSlices();
		
		double[][] derivativeMatrix = new double[width*height][slices];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				float[] tmp = is.getVoxels(x, y, 0, 1, 1, slices, new float[slices]);
				double[] pixels = new double[tmp.length];
				IntStream.range(0, tmp.length).forEach(index -> pixels[index] = tmp[index]);
				
				derivativeMatrix[y * width + x] = pixels;
			}
		}
		getSubject().getPreprocessedDataInterface().getDerivationMatrix().setData(new DerivationMatrixData(derivativeMatrix));
    }
    
    private void saveTimeDerivativeMatrix(){
    	System.out.println("\t\tWriting time derivative matrix...");
    	Path storageFilePath = getSubject().getPreprocessedDataInterface().getDerivationMatrix().getStorageFilePath();
    	String outName = FileSystems.getDefault().getSeparator() + getSubject().getName() + "_timeDerivative.csv";
    	getSubject().getPreprocessedDataInterface().getDerivationMatrix().getData().saveTo(storageFilePath, outName);
    }
    
    private void saveImage(ImagePlus imp) {
    	//TODO add ROI identifier
    	String roiName = getSubject().getRawDataInterface().getTissueROI().getName();
    	Path storageFilePath = getSubject().getPreprocessedDataInterface().getPreprocessedImage().getStorageFilePath();
    	String outName = getSubject().getName() + "_preprocessed_roi-" + roiName + ".tif";
    	IJ.save(imp, storageFilePath.toString() + System.getProperty("file.separator") + outName);
    	getSubject().getPreprocessedDataInterface().getPreprocessedImage().setData(new HyperstackData(imp));
    }
    
    @Override
    public void run() {
    	ImagePlus imp = getRawDataInterface().getRawImage().getCurrentProvider().get().getImage();

    	System.out.println("Imported image " + imp.getTitle() + 
    			" width " + imp.getWidth() +
    			" height " + imp.getHeight() +
    			" frames " + imp.getNFrames() +
    			" channels " + imp.getNChannels());

    	ImagePlus[] channels = ij.plugin.ChannelSplitter.split(imp.duplicate());

    	int anatomyCh = getSample().getRun().getPreprocessingParameters().getAnatomicChannel();
    	int interestCh = getSample().getRun().getPreprocessingParameters().getChannelOfInterest();


    	//TODO check if channels exist and if two channels are to be loaded

    	/*
    	 * if anatomy = 0 -> no anatomy, just process interest
    	 * if interest = 0 -> error, specify channel of interest
    	 * if anatomy OOB -> error, but proceed with interest
    	 * if interest OOB -> error, specify channel of interest
    	 */

    	ImagePlus anatomy = channels[getSample().getRun().getPreprocessingParameters().getAnatomicChannel() - 1];
    	ImagePlus interest = channels[getSample().getRun().getPreprocessingParameters().getChannelOfInterest() - 1];
    	
    	
    	/*
    	 * perform image registration
    	 */
    	String transforms = System.getProperty("java.io.tmpdir") + "transform.txt";
    	
        if(anatomy == null)
        	interest = registerImages(transforms, interest);
        else
        	interest = registerImages(transforms, anatomy, interest);
    	
    	anatomy.close();

    	/*
    	 * perform z-transformation on pixel values of channel of interest
    	 */
    	interest = ztransform(interest);
    	ImageStatistics is = interest.getStatistics();
    	
    	/*
    	 * set pixels outside ROI to zero and crop to ROI
    	 */
    	Roi r = getRawDataInterface().getTissueROI().getCurrentProvider().get().getRoi();
    	interest = setOuterPixels(interest, r);

    	/*
    	 * downsample by specified downsampling factor
    	 */
    	int downFactor = getSample().getRun().getPreprocessingParameters().getDownsamplingFactor();
    	interest = downsample(interest, downFactor);
    	
    	/*
    	 * convert to time derivative
    	 */
    	interest = toTimeDerivativeImage(interest);
    	
    	/*
    	 * save pre-processed image
    	 */
    	saveImage(interest);
    	
    	toTimeDerivativeMatrix(interest);
    	
    	saveTimeDerivativeMatrix();
    	
    	interest.close();
    	IJ.freeMemory();
    	
    	System.out.println("\tFinished Preprocessing.");
    }

    @Override
    public String getName() {
        return "Preprocessing " + getSample().getName() + "/" + getSubject().getName();
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return new MCATValidityReport(this, "Preprocessing", true, "");
    }
}
