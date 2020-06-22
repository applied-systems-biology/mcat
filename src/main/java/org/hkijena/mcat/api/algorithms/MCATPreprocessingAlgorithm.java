package org.hkijena.mcat.api.algorithms;


import de.embl.cmci.registration.MultiStackReg_;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.ImageCalculator;
import ij.process.ImageStatistics;

import java.util.Set;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessingInput;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessingOutput;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.extension.datatypes.DerivativeMatrixData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;
import org.hkijena.mcat.extension.datatypes.ROIData;

public class MCATPreprocessingAlgorithm extends MCATAlgorithm {

    private final MCATPreprocessingParameters preprocessingParameters;
    private MCATPreprocessingInput preprocessingInput;
    private MCATPreprocessingOutput preprocessingOutput;

    private boolean saveRaw = false, saveRoi = false;
    private int downFactor = 1;
    private int channelAnatomy, channelOfInterest = -1;
    private Roi roi = null;
    private String roiName = "noROI";
    private String identifier = "";

    public MCATPreprocessingAlgorithm(MCATRun run,
                                      MCATPreprocessingParameters preprocessingParameters,
                                      MCATPreprocessingInput preprocessingInput,
                                      MCATPreprocessingOutput preprocessingOutput) {
        super(run);
        this.preprocessingParameters = preprocessingParameters;
        this.preprocessingInput = preprocessingInput;
        this.preprocessingOutput = preprocessingOutput;
    }

    private ImagePlus registerImages(String transformFile, ImagePlus... imps) {
        System.out.println("\tRegistering channels...");
        MultiStackReg_ msr = new MultiStackReg_();

        switch (imps.length) {
            case 1:
                msr.setSrcImg(imps[0]);
                msr.setTgtImg(null);
                msr.setSrcAction("Align");
                msr.setTgtAction("None");
                msr.setTransformation(1);
                msr.setSaveTransform(false);
                msr.core(transformFile, transformFile);

                break;
            case 2:
                msr.setSrcImg(imps[0]);
                msr.setTgtImg(imps[1]);
                msr.setSrcAction("Align");
                msr.setTgtAction("Load Transformation File");
                msr.setTransformation(1);
                msr.setSaveTransform(true);
                msr.core(transformFile, transformFile);
                break;
            default:
                System.err.println("Invalid number of images specified for registration: " + imps.length);
                break;
        }

        return imps[imps.length - 1];
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
        if (roi == null) {
            System.out.println("WARNING: no ROI specified, background will not be set to zero!");
            return imp;
        }

        imp.setRoi(roi);
        IJ.run(imp, "Make Inverse", "");
        IJ.run(imp, "Set...", "value=0 stack");
        imp.setRoi(roi);
        IJ.run(imp, "Crop", "");
        IJ.run(imp, "Select None", "");

        return imp;
    }

    private ImagePlus downsample(ImagePlus imp) {
        downFactor = getPreprocessingParameters().getDownsamplingFactor();
        System.out.println("\tPerforming downsampling by factor " + downFactor + "...");
        if (downFactor < 0)
            throw new IllegalArgumentException("Downsampling factor must be specified and > 0!");
        else {
            int newFrames = imp.getNFrames() / downFactor;
            imp = new ij.plugin.Resizer().zScale(imp, newFrames, 1);
        }
        
        int slices = imp.getNSlices();
        getPreprocessingOutput().setNSlices(slices);

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
        int slices = imp.getNSlices() == 1 ? imp.getNFrames() : imp.getNSlices();

        double[][] derivativeMatrix = new double[width * height][slices];
        float[] tmp = new float[slices];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tmp = is.getVoxels(x, y, 0, 1, 1, slices, new float[slices]);
                double[] pixels = new double[tmp.length];
                for (int j = 0; j < tmp.length; j++) {
                    pixels[j] = tmp[j];
                }
                derivativeMatrix[y * width + x] = pixels;
            }
        }
        getPreprocessingOutput().getDerivativeMatrix().setData(new DerivativeMatrixData(derivativeMatrix));
    }

    private void saveTimeDerivativeMatrix() {
        System.out.println("\tWriting time derivative matrix...");
        getPreprocessingOutput().getDerivativeMatrix().flush(identifier);
    }

    private void saveImage(ImagePlus imp) {
        System.out.println("\tWriting pre-processed image...");
        getPreprocessingOutput().getPreprocessedImage().setData(new HyperstackData(imp));
        getPreprocessingOutput().getPreprocessedImage().flush(identifier);
    }

    @Override
    public void run() {
        getPreprocessingInput().getRawImage().resetFromCurrentProvider();
        ImagePlus imp = getPreprocessingInput().getRawImage().getData(HyperstackData.class).getImage();
        
        saveRaw = getPreprocessingParameters().isSaveRawImage();
        saveRoi = getPreprocessingParameters().isSaveRoi();
        channelAnatomy = getPreprocessingParameters().getAnatomicChannel();
        channelOfInterest = getPreprocessingParameters().getChannelOfInterest();
        
        getPreprocessingInput().getTissueROI().resetFromCurrentProvider();
        
        roi = getPreprocessingInput().getTissueROI().getData(ROIData.class).getRoi();
        
        //TODO check what happens if there is no ROI
        if(roi != null)
        	roiName = roi.getName();
        getPreprocessingInput().getTissueROI().setData(new ROIData(roi));

        System.out.println("Start pre-processing for " + imp.getTitle() +
                " width " + imp.getWidth() +
                " height " + imp.getHeight() +
                " frames " + imp.getNFrames() +
                " channels " + imp.getNChannels() +
                " roi name: " + roiName);

        ImagePlus[] channels = ij.plugin.ChannelSplitter.split(imp.duplicate());

        /*
         * check if anatomy channel should be used for registration and if channel of interest is specified
         * perform image registration accordingly
         */
        boolean anatomyProvided = false;
        if (channelAnatomy > 0 && channelAnatomy < channels.length + 1)
            anatomyProvided = true;

        if (!(channelOfInterest > 0 && channelOfInterest < channels.length + 1))
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
        
        Set<String> keys = getPreprocessingInput().getSlots().keySet();
        
        
        for (String key : keys) {
			System.out.println(getPreprocessingInput().getSlots().get(key).toString());
		}
        
        identifier = imp.getShortTitle() + 
        			getPreprocessingParameters().toShortenedString() +
        			"_roi-" + roiName + "_";
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

        if (saveRaw)
            getPreprocessingInput().getRawImage().flush(getName() + "_");
        if (saveRoi)
            getPreprocessingInput().getTissueROI().flush(getName() + "_" + roiName + "_");

        interest.close();

        getPreprocessingInput().getRawImage().setData(null);

        IJ.freeMemory();
        System.gc();

        System.out.println("Finished Preprocessing.");
    }

    @Override
    public String getName() {
        return "preprocessing";
    }

    @Override
    public void reportValidity(MCATValidityReport report) {

    }

    public MCATPreprocessingInput getPreprocessingInput() {
        return preprocessingInput;
    }

    public MCATPreprocessingOutput getPreprocessingOutput() {
        return preprocessingOutput;
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }
}
