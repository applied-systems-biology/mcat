/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.api.algorithms;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.SubstackMaker;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringInput;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringInputDataSetEntry;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringOutput;
import org.hkijena.mcat.api.datainterfaces.MCATClusteringOutputDataSetEntry;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessingOutput;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.extension.datatypes.ClusterAbundanceData;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class MCATClusteringAlgorithm extends MCATAlgorithm {

    private static final String[] colors = new String[]{
            "#BEBD7F", "#C2B078", "#C6A664", "#E5BE01", "#CDA434", "#A98307", "#E4A010", "#DC9D00", "#8A6642", "#C7B446", "#EAE6CA",
            "#E1CC4F", "#E6D690", "#EDFF21", "#F5D033", "#F8F32B", "#9E9764", "#999950", "#F3DA0B", "#FAD201", "#AEA04B", "#FFFF00",
            "#9D9101", "#F4A900", "#D6AE01", "#F3A505", "#EFA94A", "#6A5D4D", "#705335", "#F39F18", "#ED760E", "#C93C20", "#CB2821",
            "#FF7514", "#F44611", "#FF2301", "#FFA420", "#F75E25", "#F54021", "#D84B20", "#EC7C26", "#E55137", "#C35831", "#AF2B1E",
            "#A52019", "#A2231D", "#9B111E", "#75151E", "#5E2129", "#412227", "#642424", "#781F19", "#C1876B", "#A12312", "#D36E70",
            "#EA899A", "#B32821", "#E63244", "#D53032", "#CC0605", "#D95030", "#F80000", "#FE0000", "#C51D34", "#CB3234", "#B32428",
            "#721422", "#B44C43", "#6D3F5B", "#922B3E", "#DE4C8A", "#641C34", "#6C4675", "#A03472", "#4A192C", "#924E7D", "#A18594",
            "#CF3476", "#8673A1", "#6C6874", "#354D73", "#1F3438", "#20214F", "#1D1E33", "#18171C", "#1E2460", "#3E5F8A", "#26252D",
            "#025669", "#0E294B", "#231A24", "#3B83BD", "#1E213D", "#606E8C", "#2271B3", "#063971", "#3F888F", "#1B5583", "#1D334A",
            "#256D7B", "#252850", "#49678D", "#5D9B9B", "#2A6478", "#102C54", "#316650", "#287233", "#2D572C", "#424632", "#1F3A3D",
            "#2F4538", "#3E3B32", "#343B29", "#39352A", "#31372B", "#35682D", "#587246", "#343E40", "#6C7156", "#47402E", "#3B3C36",
            "#1E5945", "#4C9141", "#57A639", "#BDECB6", "#2E3A23", "#89AC76", "#25221B", "#308446", "#3D642D", "#015D52", "#84C3BE",
            "#2C5545", "#20603D", "#317F43", "#497E76", "#7FB5B5", "#1C542D", "#193737", "#008F39", "#00BB2D", "#78858B", "#8A9597",
            "#7E7B52", "#6C7059", "#969992", "#646B63", "#6D6552", "#6A5F31", "#4D5645", "#4C514A", "#434B4D", "#4E5754", "#464531",
            "#434750", "#293133", "#23282B", "#332F2C", "#686C5E", "#474A51", "#2F353B", "#8B8C7A", "#474B4E", "#B8B799", "#7D8471",
            "#8F8B66", "#D7D7D7", "#7F7679", "#7D7F7D", "#B5B8B1", "#6C6960", "#9DA1AA", "#8D948D", "#4E5452", "#CAC4B0", "#909090",
            "#82898F", "#D0D0D0", "#898176", "#826C34", "#955F20", "#6C3B2A", "#734222", "#8E402A", "#59351F", "#6F4F28", "#5B3A29",
            "#592321", "#382C1E", "#633A34", "#4C2F27", "#45322E", "#403A3A", "#212121", "#A65E2E", "#79553D", "#755C48", "#4E3B31",
            "#763C28", "#FDF4E3", "#E7EBDA", "#F4F4F4", "#282828", "#0A0A0A", "#A5A5A5", "#8F8F8F", "#FFFFFF", "#1C1C1C", "#F6F6F6",
            "#1E1E1E", "#D7D7D7", "#9C9C9C", "#828282"
    };

    private MCATPreprocessingParameters preprocessingParameters;
    private MCATClusteringParameters clusteringParameters;
    private MCATClusteringInput clusteringInput;
    private MCATClusteringOutput clusteringOutput;

    private int minLength, k;
    private HashMap<String, ImagePlus> clustered = new HashMap<>();
    private List<DoublePoint> points = new ArrayList<>();

    public MCATClusteringAlgorithm(MCATRun run,
                                   MCATPreprocessingParameters preprocessingParameters,
                                   MCATClusteringParameters clusteringParameters,
                                   MCATClusteringInput clusteringInput,
                                   MCATClusteringOutput clusteringOutput) {
        super(run);
        this.preprocessingParameters = preprocessingParameters;
        this.clusteringParameters = clusteringParameters;
        this.clusteringInput = clusteringInput;
        this.clusteringOutput = clusteringOutput;
    }


    private int hexToRGB(String hex) {
        Color col = Color.decode(hex);
        int rgb = (col.getRed() << 16) | (col.getGreen() << 8) | col.getBlue();
        return rgb;
    }

    private void loadImages() {
        System.out.println("\tLoading images...");
        List<String> keys = new ArrayList<>(clusteringInput.getDataSetEntries().keySet());
//        Collections.sort(keys);

        String[] names = new String[keys.size()];
        ImagePlus[] imps = new ImagePlus[keys.size()];

        for (int i = 0; i < keys.size(); i++) {
            MCATClusteringInputDataSetEntry samp = clusteringInput.getDataSetEntries().get(keys.get(i));
            System.out.println("\t\tSample: " + samp.getDataSetName());
            names[i] = samp.getDataSetName();

            ImagePlus imp = samp.getPreprocessedDataInterface().getPreprocessedImage().getData(HyperstackData.class).getImage();
            ImageStack is = imp.getStack();

            int width = imp.getWidth();
            int height = imp.getHeight();

            float[] tmp;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    tmp = is.getVoxels(x, y, 0, 1, 1, minLength, new float[minLength]);
                    double[] pixels = new double[tmp.length];
                    for (int j = 0; j < tmp.length; j++) {
                        pixels[j] = tmp[j];
                    }

                    if (x == 150 && y == 150) {
                        String print = "";
                        for (int j = 0; j < tmp.length; j++) {
                            print = print + tmp[j] + "; ";
                        }
                    }

                    points.add(new DoublePoint(pixels));
                }
            }

            imp.setTitle(samp.getDataSetName());
            imps[i] = new SubstackMaker().makeSubstack(imp, "1-" + minLength);
            imps[i].setTitle(samp.getDataSetName());

            samp.getPreprocessedDataInterface().getPreprocessedImage().setData(new HyperstackData(imps[i]));
        }
    }

    private void runKMeans() {

        System.out.println("\tPerforming k-means clustering with k = " + k + "...");


        int iterations = 100;
        int minSSE = Integer.MAX_VALUE;
        List<MCATCentroidCluster<DoublePoint>> finalCentroids = new ArrayList<>();

        List<Integer> currentColors = new ArrayList<Integer>();
        for (int i = 0; i < k; i++) {
            int colIndex = Math.round(colors.length / k) * i;
            currentColors.add(hexToRGB(colors[colIndex]));
        }
        getClusteringOutput().setColors(currentColors);

        for (int j = 0; j < iterations; j++) {

            int sse = 0;
            KMeansPlusPlusClusterer<DoublePoint> kmpp = new KMeansPlusPlusClusterer<DoublePoint>(k, 50, new EuclideanDistance());

            List<CentroidCluster<DoublePoint>> tmpCentroidCluster = kmpp.cluster(points);

            List<MCATCentroidCluster<DoublePoint>> centroids = new ArrayList<MCATCentroidCluster<DoublePoint>>();
            for (CentroidCluster<DoublePoint> centroidCluster : tmpCentroidCluster) {
                centroids.add(new MCATCentroidCluster<>(centroidCluster.getCenter()));
            }

            Set<String> keys = getClusteringInput().getDataSetEntries().keySet();
            for (String key : keys) {
                MCATClusteringInputDataSetEntry inputEntry = getClusteringInput().getDataSetEntries().get(key);

                ImagePlus imp = inputEntry.getPreprocessedDataInterface().getPreprocessedImage().getData(HyperstackData.class).getImage();

                ImageStack is = imp.getStack();
                int w = imp.getWidth();
                int h = imp.getHeight();

                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {

                        float[] tmp = is.getVoxels(x, y, 0, 1, 1, minLength, new float[minLength]);
                        double[] pixels = new double[tmp.length];
                        IntStream.range(0, tmp.length).forEach(index -> pixels[index] = tmp[index]);

                        double minDist = Double.MAX_VALUE;

                        for (int i = 0; i < centroids.size(); i++) {

                            double[] center = centroids.get(i).getCenter().getPoint();
                            double dist = new EuclideanDistance().compute(center, pixels);
                            if (dist < minDist) {
                                minDist = dist;
                            }
                        }
                        sse += minDist;
                    }
                }
            }

            if (sse < minSSE) {
                minSSE = sse;
                finalCentroids = centroids;
            }

//            System.out.println(j + " SSE: " + sse + "; minSSE: " + minSSE);
        }

        finalCentroids.sort(Comparator.comparingDouble(MCATCentroidCluster::getCumSum));

        getClusteringOutput().getClusterCenters().setData(new ClusterCentersData(finalCentroids));

        Set<String> keys = getClusteringInput().getDataSetEntries().keySet();


        for (String key : keys) {
            MCATClusteringInputDataSetEntry inputEntry = getClusteringInput().getDataSetEntries().get(key);
            MCATClusteringOutputDataSetEntry outputEntry = getClusteringOutput().getDataSetEntries().get(key);

            outputEntry.getClusterAbundance().setData(new ClusterAbundanceData(finalCentroids, new int[finalCentroids.size()]));

            ImagePlus imp = inputEntry.getPreprocessedDataInterface().getPreprocessedImage().getData(HyperstackData.class).getImage();

            ImageStack is = imp.getStack();
            int w = imp.getWidth();
            int h = imp.getHeight();
            int[] clusteredPixels = new int[w * h];

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {

                    float[] tmp = is.getVoxels(x, y, 0, 1, 1, minLength, new float[minLength]);
                    double[] pixels = new double[tmp.length];
                    IntStream.range(0, tmp.length).forEach(index -> pixels[index] = tmp[index]);

                    double minDist = Double.MAX_VALUE;
                    int closestCluster = -1;

                    for (int i = 0; i < finalCentroids.size(); i++) {

                        double[] center = finalCentroids.get(i).getCenter().getPoint();
                        double dist = new EuclideanDistance().compute(center, pixels);
                        if (dist < minDist) {
                            minDist = dist;
                            closestCluster = i;
                        }
                    }

                    if (closestCluster == -1) {
                        System.err.println("No closest cluster found for this pixel position (x=" + x + "; y=" + y + ")");
                    }

                    outputEntry.getClusterAbundance().getData(ClusterAbundanceData.class).incrementAbundance(closestCluster);
                    finalCentroids.get(closestCluster).addMember();
//                    int colIndex = Math.round(colors.length / k) * closestCluster;

                    /*
                     * for colorblind-friendly coloring of spatial cluster distribution
                     */
//                    String[] colors2 = new String[]{"#dedc49", "#c35831", "#18171c", "#61bce0", "#7d7d7d"};   
//                    colIndex = closestCluster;

                    clusteredPixels[y * w + x] = currentColors.get(closestCluster);
                }
            }

            outputEntry.getClusterAbundance().flush();

            ImagePlus clusteredImage = IJ.createImage(imp.getTitle() + "_clusteredImage", "RGB white", w, h, 1);
            clusteredImage.getProcessor().setPixels(clusteredPixels);

            clustered.put(outputEntry.getDataSetName(), clusteredImage);
        }
    }

    /*
     * save cluster centers and clustered images
     */
    private void saveData() {
        System.out.println("\tSaving clustering results...");

//        String group = "";
//        if(getClusteringOutput().getGroupTreatment().equals(""))
//        	group = getClusteringOutput().getGroupSubject();
//        else
//        	group = getClusteringOutput().getGroupTreatment();

//        String identifier = group + 
//        		getPreprocessingParameters().toShortenedString() +
//        		getClusteringParameters().toShortenedString();

        getClusteringOutput().getClusterCenters().flush();

        // Clustered images per dataset
        for (Map.Entry<String, ImagePlus> entry : clustered.entrySet()) {
            MCATClusteringOutputDataSetEntry dataSetEntry = getClusteringOutput().getDataSetEntries().get(entry.getKey());
            dataSetEntry.getClusterImages().setData(new HyperstackData(entry.getValue()));
            dataSetEntry.getClusterImages().flush();
        }
    }

    @Override
    public void run() {

        System.out.println("Starting " + getName());

        k = getClusteringParameters().getkMeansK();

        minLength = getClusteringParameters().getMinLength();
        for (MCATPreprocessingOutput preprocessingOutput : getClusteringInput().getAllPreprocessingOutputs()) {
            minLength = Math.min(preprocessingOutput.getNSlices(), minLength);
        }
        minLength = Math.min(getPreprocessingParameters().getMaxTime(), minLength);
        getClusteringOutput().setMinLength(minLength);
        minLength = minLength - 1; //subtract one because of differences in indexing and slice number measurement

        loadImages();

        runKMeans();

        saveData();

    }

    @Override
    public String getName() {
        return "clustering";
    }

    @Override
    public void reportValidity(MCATValidityReport report) {

    }

    public MCATClusteringInput getClusteringInput() {
        return clusteringInput;
    }

    public MCATClusteringOutput getClusteringOutput() {
        return clusteringOutput;
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }
}
