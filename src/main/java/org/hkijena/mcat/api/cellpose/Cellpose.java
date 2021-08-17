package org.hkijena.mcat.api.cellpose;

import ij.IJ;
import ij.ImagePlus;
import org.hkijena.mcat.utils.PathUtils;
import org.hkijena.mcat.utils.PythonEnvironment;
import org.hkijena.mcat.utils.PythonUtils;
import org.hkijena.mcat.utils.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cellpose implements Runnable {

    private final Path tmpPath;
    private List<ImagePlus> inputImages = new ArrayList<>();
    private List<ImagePlus> outputProbabilities = new ArrayList<>();

    private double diameter = 30;
    private boolean normalize = true;
    private boolean netAverage = true;
    private boolean augment = false;
    private boolean interpolate = true;
    private boolean enableAnisotropy = false;
    private double anisotropy = 1.0;
    private int batchSize = 8;
    private boolean tile = true;
    private double tileOverlap = 0.1;
    private boolean resample = false;
    private boolean withGPU = true;
    private double modelMeanDiameter = 210;
    private PythonEnvironment pythonEnvironment;

    public Cellpose(Path tmpPath) {
        this.tmpPath = tmpPath;
    }

    @Override
    public void run() {

        if (!Files.isDirectory(tmpPath)) {
            try {
                Files.createDirectories(tmpPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Save the model
        System.out.println("Saving pretrained model ...");
        Path modelPath = tmpPath.resolve("cellpose_model");
        try {
            Files.copy(ResourceUtils.getPluginResourceAsStream("models/cellpose_model"), modelPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder code = new StringBuilder();
        code.append("from cellpose import models\n");
        code.append("from cellpose import utils, io\n");
        code.append("import json\n");
        code.append("import time\n");
        code.append("import numpy as np\n\n");

        List<Path> inputImagePaths = new ArrayList<>();
        List<Path> outputProbabilitiesPaths = new ArrayList<>();
        for (int i = 0; i < inputImages.size(); i++) {
            Path rawPath = tmpPath.resolve("raw" + i + ".tif");
            Path probabilitiesPath = tmpPath.resolve("probabilities" + i + ".tif");
            inputImagePaths.add(rawPath);
            outputProbabilitiesPaths.add(probabilitiesPath);

            System.out.println("Saving raw image " + rawPath);
            IJ.save(inputImages.get(i), rawPath.toString());
        }

        code.append("input_image_paths = ").append(PythonUtils.objectToPython(inputImagePaths)).append("\n");
        code.append("output_probabilities_paths = ").append(PythonUtils.objectToPython(outputProbabilitiesPaths)).append("\n");

        code.append("for image_index in range(len(").append("input_image_paths").append(")):\n");

        // Load image
        code.append("    input_file = input_image_paths[image_index]\n");
        code.append("    img = io.imread(input_file)\n");
        code.append("    print(\"Read image with index\", image_index, \"shape\", img.shape)\n");

        // Create model
        injectCustomCellposeClass(code);
        setupCustomCellposeModel(code, modelPath);

        // Evaluate model
        setupModelEval(code);

        // Run the workload
        PythonUtils.runPython(code.toString(), pythonEnvironment);

        // Extract results
        for (Path path : outputProbabilitiesPaths) {
            System.out.println("Reading result " + path + " ...");
            outputProbabilities.add(IJ.openImage(path.toString()));
        }

        // Cleanup
        PathUtils.deleteDirectoryRecursively(tmpPath);
    }

    private void setupModelEval(StringBuilder code) {
        Map<String, Object> evalParameterMap = new HashMap<>();
        evalParameterMap.put("x", PythonUtils.rawPythonCode("img"));
        evalParameterMap.put("diameter", diameter);
        evalParameterMap.put("channels", PythonUtils.rawPythonCode("[[0, 0]]"));
        evalParameterMap.put("do_3D", PythonUtils.rawPythonCode("enable_3d_segmentation[image_index]"));
        evalParameterMap.put("normalize", normalize);
        evalParameterMap.put("anisotropy", enableAnisotropy ?
                anisotropy : null);
        evalParameterMap.put("net_avg", netAverage);
        evalParameterMap.put("augment", augment);
        evalParameterMap.put("tile", tile);
        evalParameterMap.put("tile_overlap", tileOverlap);
        evalParameterMap.put("resample", resample);
        evalParameterMap.put("interp", interpolate);
        evalParameterMap.put("flow_threshold", 0.4);
        evalParameterMap.put("cellprob_threshold", 0);
        evalParameterMap.put("min_size", 15);
        evalParameterMap.put("stitch_threshold", 0);

        code.append(String.format("    masks, flows, styles, diams = model.eval(%s)\n", PythonUtils.mapToPythonArguments(evalParameterMap)));
        code.append("    io.imsave(").append("output_probabilities_paths[image_index]").append(", probs)\n");
    }

    public PythonEnvironment getPythonEnvironment() {
        return pythonEnvironment;
    }

    public void setPythonEnvironment(PythonEnvironment pythonEnvironment) {
        this.pythonEnvironment = pythonEnvironment;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public boolean isNetAverage() {
        return netAverage;
    }

    public void setNetAverage(boolean netAverage) {
        this.netAverage = netAverage;
    }

    public boolean isAugment() {
        return augment;
    }

    public void setAugment(boolean augment) {
        this.augment = augment;
    }

    public boolean isInterpolate() {
        return interpolate;
    }

    public void setInterpolate(boolean interpolate) {
        this.interpolate = interpolate;
    }

    public boolean isEnableAnisotropy() {
        return enableAnisotropy;
    }

    public void setEnableAnisotropy(boolean enableAnisotropy) {
        this.enableAnisotropy = enableAnisotropy;
    }

    public double getAnisotropy() {
        return anisotropy;
    }

    public void setAnisotropy(double anisotropy) {
        this.anisotropy = anisotropy;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public boolean isTile() {
        return tile;
    }

    public void setTile(boolean tile) {
        this.tile = tile;
    }

    public double getTileOverlap() {
        return tileOverlap;
    }

    public void setTileOverlap(double tileOverlap) {
        this.tileOverlap = tileOverlap;
    }

    public boolean isResample() {
        return resample;
    }

    public void setResample(boolean resample) {
        this.resample = resample;
    }

    public double getModelMeanDiameter() {
        return modelMeanDiameter;
    }

    public void setModelMeanDiameter(double modelMeanDiameter) {
        this.modelMeanDiameter = modelMeanDiameter;
    }

    public void addInputImage(ImagePlus img) {
        if (img.getNDimensions() != 2) {
            throw new IllegalArgumentException("Only 2D images are supported!");
        }
        inputImages.add(img);
    }

    public boolean isWithGPU() {
        return withGPU;
    }

    public void setWithGPU(boolean withGPU) {
        this.withGPU = withGPU;
    }

    public List<ImagePlus> getInputImages() {
        return inputImages;
    }

    public List<ImagePlus> getOutputProbabilities() {
        return outputProbabilities;
    }

    private void setupCustomCellposeModel(StringBuilder code, Path customModelPath) {
        injectCustomCellposeClass(code);
        Map<String, Object> modelParameterMap = new HashMap<>();
        modelParameterMap.put("pretrained_model", customModelPath);
        modelParameterMap.put("net_avg", netAverage);
        modelParameterMap.put("gpu", withGPU);
        modelParameterMap.put("diam_mean", modelMeanDiameter);
        code.append(String.format("model = CellposeCustom(%s)\n", PythonUtils.mapToPythonArguments(modelParameterMap)));
    }

    private void injectCustomCellposeClass(StringBuilder code) {
        // This is code that allows to embed a custom model
        code.append("\n\nclass CellposeCustom():\n" +
                "    def __init__(self, gpu=False, pretrained_model=None, diam_mean=None, pretrained_size=None, net_avg=True, device=None, torch=True):\n" +
                "        super(CellposeCustom, self).__init__()\n" +
                "        from cellpose.core import UnetModel, assign_device, check_mkl, use_gpu, MXNET_ENABLED, parse_model_string\n" +
                "        from cellpose.models import CellposeModel, SizeModel\n\n" +
                "        if not torch:\n" +
                "            if not MXNET_ENABLED:\n" +
                "                torch = True\n" +
                "        self.torch = torch\n" +
                "        torch_str = ['','torch'][self.torch]\n" +
                "        \n" +
                "        # assign device (GPU or CPU)\n" +
                "        sdevice, gpu = assign_device(self.torch, gpu)\n" +
                "        self.device = device if device is not None else sdevice\n" +
                "        self.gpu = gpu\n" +
                "        self.pretrained_model = pretrained_model\n" +
                "        self.pretrained_size = pretrained_size\n" +
                "        self.diam_mean = diam_mean\n" +
                "        \n" +
                "        if not net_avg:\n" +
                "            self.pretrained_model = self.pretrained_model[0]\n" +
                "\n" +
                "        self.cp = CellposeModel(device=self.device, gpu=self.gpu,\n" +
                "                                pretrained_model=self.pretrained_model,\n" +
                "                                diam_mean=self.diam_mean, torch=self.torch)\n" +
                "        if pretrained_size is not None:\n" +
                "            self.sz = SizeModel(device=self.device, pretrained_size=self.pretrained_size,\n" +
                "                            cp_model=self.cp)\n" +
                "        else:\n" +
                "            self.sz = None\n" +
                "\n" +
                "    def eval(self, x, batch_size=8, channels=None, channel_axis=None, z_axis=None,\n" +
                "             invert=False, normalize=True, diameter=30., do_3D=False, anisotropy=None,\n" +
                "             net_avg=True, augment=False, tile=True, tile_overlap=0.1, resample=False, interp=True,\n" +
                "             flow_threshold=0.4, cellprob_threshold=0.0, min_size=15, \n" +
                "              stitch_threshold=0.0, rescale=None, progress=None):\n" +
                "        from cellpose.models import models_logger\n" +
                "        tic0 = time.time()\n" +
                "\n" +
                "        estimate_size = True if (diameter is None or diameter==0) else False\n" +
                "        models_logger.info('Estimate size: ' + str(estimate_size))\n" +
                "        if estimate_size and self.pretrained_size is not None and not do_3D and x[0].ndim < 4:\n" +
                "            tic = time.time()\n" +
                "            models_logger.info('~~~ ESTIMATING CELL DIAMETER(S) ~~~')\n" +
                "            diams, _ = self.sz.eval(x, channels=channels, channel_axis=channel_axis, invert=invert, batch_size=batch_size, \n" +
                "                                    augment=augment, tile=tile)\n" +
                "            rescale = self.diam_mean / np.array(diams)\n" +
                "            diameter = None\n" +
                "            models_logger.info('estimated cell diameter(s) in %0.2f sec'%(time.time()-tic))\n" +
                "            models_logger.info('>>> diameter(s) = ')\n" +
                "            if isinstance(diams, list) or isinstance(diams, np.ndarray):\n" +
                "                diam_string = '[' + ''.join(['%0.2f, '%d for d in diams]) + ']'\n" +
                "            else:\n" +
                "                diam_string = '[ %0.2f ]'%diams\n" +
                "            models_logger.info(diam_string)\n" +
                "        elif estimate_size:\n" +
                "            if self.pretrained_size is None:\n" +
                "                reason = 'no pretrained size model specified in model Cellpose'\n" +
                "            else:\n" +
                "                reason = 'does not work on non-2D images'\n" +
                "            models_logger.warning(f'could not estimate diameter, {reason}')\n" +
                "            diams = self.diam_mean \n" +
                "        else:\n" +
                "            diams = diameter\n" +
                "\n" +
                "        tic = time.time()\n" +
                "        models_logger.info('~~~ FINDING MASKS ~~~')\n" +
                "        masks, flows, styles = self.cp.eval(x, \n" +
                "                                            batch_size=batch_size, \n" +
                "                                            invert=invert, \n" +
                "                                            diameter=diameter,\n" +
                "                                            rescale=rescale, \n" +
                "                                            anisotropy=anisotropy, \n" +
                "                                            channels=channels,\n" +
                "                                            channel_axis=channel_axis, \n" +
                "                                            z_axis=z_axis,\n" +
                "                                            augment=augment, \n" +
                "                                            tile=tile, \n" +
                "                                            do_3D=do_3D, \n" +
                "                                            net_avg=net_avg, \n" +
                "                                            progress=progress,\n" +
                "                                            tile_overlap=tile_overlap,\n" +
                "                                            resample=resample,\n" +
                "                                            interp=interp,\n" +
                "                                            flow_threshold=flow_threshold, \n" +
                "                                            cellprob_threshold=cellprob_threshold,\n" +
                "                                            min_size=min_size, \n" +
                "                                            stitch_threshold=stitch_threshold)\n" +
                "        models_logger.info('>>>> TOTAL TIME %0.2f sec'%(time.time()-tic0))\n" +
                "    \n" +
                "        return masks, flows, styles, diams\n\n");
    }

}
