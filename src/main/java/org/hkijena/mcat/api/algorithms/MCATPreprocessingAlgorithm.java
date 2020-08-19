/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat.api.algorithms;


import de.embl.cmci.registration.MultiStackReg_;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.Duplicator;
import ij.plugin.ImageCalculator;
import ij.process.ImageStatistics;

import java.nio.file.Paths;
import java.util.Set;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATDataSlot;
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
    private int startFrame = -1, endFrame = -1;

    public MCATPreprocessingAlgorithm(MCATRun run,
                                      MCATPreprocessingParameters preprocessingParameters,
                                      MCATPreprocessingInput preprocessingInput,
                                      MCATPreprocessingOutput preprocessingOutput) {
        super(run);
        this.preprocessingParameters = preprocessingParameters;
        this.preprocessingInput = preprocessingInput;
        this.preprocessingOutput = preprocessingOutput;
    }

    private ImagePlus cropTimeFrames(ImagePlus imp) {
    	System.out.println("\tCropping stack to time range " + startFrame + " - " + endFrame + "...");
    	if(startFrame < 1 | startFrame >= imp.getNFrames() | startFrame > endFrame | endFrame < 1 | endFrame >= imp.getNFrames() | endFrame < startFrame)
        	throw new IllegalArgumentException("Illegal value for Start time frame and/or End time frame! Will not exclude time frames from stack.");

    	ImagePlus imp2 = new Duplicator().run(imp, 1, imp.getNChannels(), 1, 1, startFrame, endFrame);
  
    	return imp2;
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
        if (downFactor < 1)
            throw new IllegalArgumentException("Downsampling factor must be specified and > 1!");
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
        getPreprocessingOutput().getDerivativeMatrix().flush();
    }

    private void saveImage(ImagePlus imp) {
        System.out.println("\tWriting pre-processed image...");
        getPreprocessingOutput().getPreprocessedImage().setData(new HyperstackData(imp));
        getPreprocessingOutput().getPreprocessedImage().flush();
    }

    @Override
    public void run() {
        getPreprocessingInput().getRawImage().resetFromCurrentProvider();
        ImagePlus imp = getPreprocessingInput().getRawImage().getData(HyperstackData.class).getImage();
        
        saveRaw = getPreprocessingParameters().isSaveRawImage();
        saveRoi = getPreprocessingParameters().isSaveRoi();
        channelAnatomy = getPreprocessingParameters().getAnatomicChannel();
        channelOfInterest = getPreprocessingParameters().getChannelOfInterest();
        startFrame = getPreprocessingParameters().getMinTime();
        endFrame = getPreprocessingParameters().getMaxTime();

        MCATDataSlot tissueROI = getPreprocessingInput().getTissueROI();
        tissueROI.resetFromCurrentProvider();
        
        roi = tissueROI.getData(ROIData.class).getRoi();
        
        //TODO check what happens if there is no ROI
        if(roi != null)
        	roiName = roi.getName();
        tissueROI.setData(new ROIData(roi, roi.getName()));

        System.out.println("Start pre-processing for " + imp.getTitle() +
        		" dimensions " + imp.getNDimensions() +
                " width " + imp.getWidth() +
                " height " + imp.getHeight() +
                " frames " + imp.getNFrames() +
                " channels " + imp.getNChannels() +
                " roi name: " + roiName);
        
        /*
         * remove slices before Start time frame and after End time frame if necessary
         */
        if(startFrame != getPreprocessingParameters().MIN_TIME_DEFAULT | endFrame != getPreprocessingParameters().MAX_TIME_DEFAULT) {
        	try {
        		imp = cropTimeFrames(imp);
    		} catch (Exception e) {
    			System.err.println(e.getMessage());
    		}
        }
        
        
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

        /*
         * check if anatomic channel is provided and perform rigid registration
         */
    	if(anatomyProvided) {
    		ImagePlus anatomy = channels[channelAnatomy - 1];
    		interest = registerImages(transforms, anatomy, interest);
    		anatomy.close();
    	}else{
    		System.out.println("WARNING: no anatomy channel provided. Images will not be registered.");
    	}

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

        if (saveRaw)
            getPreprocessingInput().getRawImage().flush();
        if (saveRoi && tissueROI.getFileName() != null) {
            String name = tissueROI.getFileName().toString();
            name += "_" + tissueROI.getData(ROIData.class).getName() + "_roiFile.roi";
            tissueROI.setFileName(Paths.get(name));
            tissueROI.flush();
        }

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
