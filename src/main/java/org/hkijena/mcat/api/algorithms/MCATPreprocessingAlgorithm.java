package org.hkijena.mcat.api.algorithms;


import de.embl.cmci.registration.MultiStackReg_;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.ImageCalculator;
import ij.process.ImageStatistics;
import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.extension.datatypes.DerivativeMatrixData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;
import org.hkijena.mcat.extension.datatypes.ROIData;
import org.hkijena.mcat.utils.api.ACAQValidityReport;

import java.util.Arrays;
import java.util.List;

public class MCATPreprocessingAlgorithm extends MCATAlgorithm {

    private MCATRawDataInterface rawDataInterface;
    private MCATPreprocessedDataInterface preprocessedDataInterface;

    private boolean saveRaw = false, saveRoi = false;
    private int downFactor = 1;
    private int channelAnatomy, channelOfInterest = -1;
    private String roiName = "noROI";

    public MCATPreprocessingAlgorithm(MCATRun run,
                                      MCATPreprocessingParameters preprocessingParameters,
                                      MCATPostprocessingParameters postprocessingParameters,
                                      MCATClusteringParameters clusteringParameters,
                                      MCATRawDataInterface rawDataInterface,
                                      MCATPreprocessedDataInterface preprocessedDataInterface) {
        super(run, preprocessingParameters, postprocessingParameters, clusteringParameters);
        this.rawDataInterface = rawDataInterface;
        this.preprocessedDataInterface = preprocessedDataInterface;
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
        getRawDataInterface().getTissueROI().resetFromCurrentProvider();
        Roi r = getRawDataInterface().getTissueROI().getData(ROIData.class).getRoi();
        if (r == null) {
            System.out.println("WARNING: no ROI specified, background will not be set to zero!");
            return imp;
        }

        System.out.println("\t\tRoi name: " + r.getName());
        getRawDataInterface().getTissueROI().setData(new ROIData(r));

        roiName = r.getName();
        imp.setRoi(r);
        IJ.run(imp, "Make Inverse", "");
        IJ.run(imp, "Set...", "value=0 stack");
        imp.setRoi(r);
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
        //check if minimum number of time frames has to be updated
        int slices = imp.getNSlices();
        int minLength = getClusteringParameters().getMinLength();

        // Setting parameters during runtime is not allowed
//        if (slices < minLength)
//            getClusteringParameters().setMinLength(slices);

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
        getPreprocessedDataInterface().getDerivativeMatrix().setData(new DerivativeMatrixData(derivativeMatrix));
    }

    private void saveTimeDerivativeMatrix() {
        System.out.println("\tWriting time derivative matrix...");
        String identifier = getName() + "_roi-" + roiName + "_downsampling-" + downFactor + "_anatomyCh-" + channelAnatomy + "_interestCh-" + channelOfInterest + "_";
        getPreprocessedDataInterface().getDerivativeMatrix().flush(identifier);
    }

    private void saveImage(ImagePlus imp) {
        System.out.println("\tWriting pre-processed image...");
        getPreprocessedDataInterface().getPreprocessedImage().setData(new HyperstackData(imp));
        String identifier = getName() + "_roi-" + roiName + "_downsampling-" + downFactor + "_anatomyCh-" + channelAnatomy + "_interestCh-" + channelOfInterest + "_";
        getPreprocessedDataInterface().getPreprocessedImage().flush(identifier);
    }

    @Override
    public void run() {
        getRawDataInterface().getRawImage().resetFromCurrentProvider();
        ImagePlus imp = getRawDataInterface().getRawImage().getData(HyperstackData.class).getImage();

        saveRaw = getPreprocessingParameters().isSaveRawImage();
        saveRoi = getPreprocessingParameters().isSaveRoi();

        System.out.println("Start pre-processing for " + imp.getTitle() +
                " width " + imp.getWidth() +
                " height " + imp.getHeight() +
                " frames " + imp.getNFrames() +
                " channels " + imp.getNChannels());

        ImagePlus[] channels = ij.plugin.ChannelSplitter.split(imp.duplicate());

        channelAnatomy = getPreprocessingParameters().getAnatomicChannel();
        channelOfInterest = getPreprocessingParameters().getChannelOfInterest();


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
            getRawDataInterface().getRawImage().flush(getName() + "_");
        if (saveRoi)
            getRawDataInterface().getTissueROI().flush(getName() + "_" + roiName + "_");

        interest.close();

        getRawDataInterface().getRawImage().setData(null);

        IJ.freeMemory();
        System.gc();

        System.out.println("Finished Preprocessing.");
    }

    @Override
    public String getName() {
        return "preprocessing";
    }

    @Override
    public List<MCATDataInterface> getInputDataInterfaces() {
        return Arrays.asList(rawDataInterface);
    }

    @Override
    public List<MCATDataInterface> getOutputDataInterfaces() {
        return Arrays.asList(rawDataInterface, preprocessedDataInterface);
    }

    @Override
    public void reportValidity(ACAQValidityReport report) {

    }

    public MCATRawDataInterface getRawDataInterface() {
        return rawDataInterface;
    }

    public MCATPreprocessedDataInterface getPreprocessedDataInterface() {
        return preprocessedDataInterface;
    }
}
