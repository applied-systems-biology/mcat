package org.hkijena.mcat.api.algorithms;



import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.stream.IntStream;

import org.hkijena.mcat.api.MCATPerSubjectAlgorithm;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.DerivativeMatrixData;
import org.hkijena.mcat.api.datatypes.HyperstackData;
import org.hkijena.mcat.api.datatypes.ROIData;

import de.embl.cmci.registration.MultiStackReg_;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.ImageCalculator;
import ij.process.ImageStatistics;

public class MCATPreprocessingAlgorithm extends MCATPerSubjectAlgorithm {
	
	private boolean saveRaw = false, saveRoi = false;
	private int downFactor = 1;
	private int channelAnatomy, channelOfInterest = -1;
	private String roiName = "noROI";

    public MCATPreprocessingAlgorithm(MCATRunSampleSubject subject) {
        super(subject);
    }
    
    private ImagePlus registerImages(String transformFile, ImagePlus... imps) {
    	System.out.println("\tRegistering channels...");
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
    	System.out.println("\tPerforming z-transformation...");
    	ImageStatistics is = imp.getStatistics();
    	IJ.run(imp, "Subtract...", "value=" + is.mean + " stack");
    	IJ.run(imp, "Divide...", "value=" + is.stdDev + " stack");
    	
    	return imp;
    }
    
    private ImagePlus setOuterPixels(ImagePlus imp) {
    	System.out.println("\tSetting pixels outside ROI to zero...");
    	Roi r = getRawDataInterface().getTissueROI().getCurrentProvider().get().getRoi();
    	if(r == null) {
    		System.out.println("WARNING: no ROI specified, background will not be set to zero!");
    		return imp;
    	}
    	
    	System.out.println("\t\tRoi name: " + r.getName());
    	getRawDataInterface().getTissueROI().setData(new ROIData(r));
    	
    	roiName = r.getName();
	    imp.setRoi(r);
	    IJ.run(imp, "Make Inverse", "");
	    IJ.run(imp,"Set...", "value=0 stack");
	    imp.setRoi(r);
	    IJ.run(imp,"Crop","");
	    IJ.run(imp,"Select None","");
    	
    	return imp;
    }
    
    private ImagePlus downsample(ImagePlus imp) {
    	downFactor = getSample().getRun().getPreprocessingParameters().getDownsamplingFactor();
    	System.out.println("\tPerforming downsampling by factor " + downFactor + "...");
    	if(downFactor < 0)
    		throw new IllegalArgumentException("Downsampling factor must be specified and > 0!");
    	else {
    		int newFrames = imp.getNFrames()/downFactor;
    		imp = new ij.plugin.Resizer().zScale(imp, newFrames, 1);
    	}
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
		float[] tmp = new float[slices];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tmp = is.getVoxels(x, y, 0, 1, 1, slices, new float[slices]);
				double[] pixels = new double[tmp.length];
			    for (int j = 0; j < tmp.length; j++){
			        pixels[j] = tmp[j];
			    }
				derivativeMatrix[y * width + x] = pixels;
			}
		}
		getSubject().getPreprocessedDataInterface().getDerivativeMatrix().setData(new DerivativeMatrixData(derivativeMatrix));
    }
    
    private void saveTimeDerivativeMatrix(){
    	System.out.println("\tWriting time derivative matrix...");
    	String identifier = getSubject().getName() + "_roi-" + roiName + "_downsampling-" + downFactor + "_anatomyCh-" + channelAnatomy + "_interestCh-" + channelOfInterest + "_";
    	getSubject().getPreprocessedDataInterface().getDerivativeMatrix().flush(identifier);
    }
    
    private void saveImage(ImagePlus imp) {
    	System.out.println("\tWriting pre-processed image...");
    	getSubject().getPreprocessedDataInterface().getPreprocessedImage().setData(new HyperstackData(imp));
    	String identifier = getSubject().getName() + "_roi-" + roiName + "_downsampling-" + downFactor + "_anatomyCh-" + channelAnatomy + "_interestCh-" + channelOfInterest + "_";
    	getSubject().getPreprocessedDataInterface().getPreprocessedImage().flush(identifier);
    }
    
    @Override
    public void run() {
    	ImagePlus imp = getRawDataInterface().getRawImage().getCurrentProvider().get().getImage();
    	getRawDataInterface().getRawImage().setData(new HyperstackData(imp));
    	
    	saveRaw = getSample().getRun().getPreprocessingParameters().isSaveRawImage();
    	saveRoi = getSample().getRun().getPreprocessingParameters().isSaveRoi();

    	System.out.println("Start pre-processing for " + imp.getTitle() + 
    			" width " + imp.getWidth() +
    			" height " + imp.getHeight() +
    			" frames " + imp.getNFrames() +
    			" channels " + imp.getNChannels());

    	ImagePlus[] channels = ij.plugin.ChannelSplitter.split(imp.duplicate());

    	channelAnatomy = getSample().getRun().getPreprocessingParameters().getAnatomicChannel();
    	channelOfInterest = getSample().getRun().getPreprocessingParameters().getChannelOfInterest();


    	/*
    	 * check if anatomy channel should be used for registration and if channel of interest is specified
    	 * perform image registration accordingly
    	 */
    	boolean anatomyProvided = false;
    	if(channelAnatomy > 0 && channelAnatomy < channels.length + 1)
    		anatomyProvided = true;
    		
    	if(!(channelOfInterest > 0 && channelOfInterest < channels.length + 1))
    		throw new IllegalArgumentException("Channel of interest has to be specified for image processing!");
    	
    	ImagePlus interest = channels[channelOfInterest - 1];
    	String transforms = System.getProperty("java.io.tmpdir") + "transform.txt";
    	
//    	commented to save time when testing
//    	
//    	if(anatomyProvided) {
//    		ImagePlus anatomy = channels[channelAnatomy - 1];
//    		interest = registerImages(transforms, anatomy, interest);
//    		anatomy.close();
//    	}else{
//    		System.out.println("WARNING: no anatomy channel provided for image registration. Will register channel of interest without anatomy information.");
//    		interest = registerImages(transforms, interest);
//    	}
    	
    	/*
    	 * perform z-transformation on pixel values of channel of interest
    	 */
    	interest = ztransform(interest);
    	
    	/*
    	 * set pixels outside ROI to zero and crop to ROI
    	 */
    	
    	interest = setOuterPixels(interest);

    	/*
    	 * downsample by specified downsampling factor
    	 */
    	interest = downsample(interest);
    	
    	/*
    	 * convert to time derivative
    	 */
    	interest = toTimeDerivativeImage(interest);
    	
    	/*
    	 * save pre-processed image
    	 */
    	saveImage(interest);
    	
    	/*
    	 * construct time derivative matrix from pre-processed image
    	 */
    	toTimeDerivativeMatrix(interest);
    	
    	/*
    	 * save time-derivative matrix
    	 */
    	saveTimeDerivativeMatrix();
    	
    	if(saveRaw)
    		getSubject().getRawDataInterface().getRawImage().flush(getSubject().getName() + "_");
    	if(saveRoi)
    		getSubject().getRawDataInterface().getTissueROI().flush(getSubject().getName() + "_" + roiName + "_");
    	
    	interest.close();
    	
    	getRawDataInterface().getRawImage().setData(null);
    	
    	IJ.freeMemory();
    	System.gc();
    	
    	System.out.println("Finished Preprocessing.");
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
