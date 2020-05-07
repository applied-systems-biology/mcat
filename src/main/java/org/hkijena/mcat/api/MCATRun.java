package org.hkijena.mcat.api;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JFrame;

import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATParametersTable;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.utils.api.ACAQValidatable;
import org.hkijena.mcat.utils.api.ACAQValidityReport;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

public class MCATRun implements ACAQValidatable {
    private MCATProject project;
    private MCATAlgorithmGraph graph;
    private BiMap<String, MCATRunSample> samples = HashBiMap.create();

    private MCATParametersTable parametersTable;
    private boolean isReady = false;
    private Path outputPath;
    private List<MCATResultObject> resultObjects;

    public MCATRun(MCATProject project) {
        this.project = project;
        this.parametersTable = new MCATParametersTable(project.getParametersTable());
        this.resultObjects = new ArrayList<>();

        // TODO: Implement per-parameter set
//        switch (clusteringParameters.getClusteringHierarchy()) {
//            case PerSubject:
//                for(Map.Entry<String, MCATProjectSample> kv : project.getSamples().entrySet()) {
//                    MCATRunSample sample = new MCATRunSample(this, Arrays.asList(kv.getValue()));
//                    samples.put(kv.getKey(), sample);
//                }
//                break;
//            case PerTreatment: {
//                for(Map.Entry<String, List<MCATProjectSample>> kv : project.getSamplesByTreatment().entrySet()) {
//                    MCATRunSample sample = new MCATRunSample(this, kv.getValue());
//                    samples.put(kv.getKey(), sample);
//                }
//            }
//                break;
//            case AllInOne: {
//                MCATRunSample sample = new MCATRunSample(this, new ArrayList<>(project.getSamples().values()));
//                samples.put("all-in-one", sample);
//            }
//            break;
//        }

        this.graph = new MCATAlgorithmGraph(this);
    }

    public BiMap<String, MCATRunSample> getSamples() {
        return ImmutableBiMap.copyOf(samples);
    }
    
    public List<MCATResultObject> getResultObjects(){
    	return resultObjects;
    }
    
    public void addResultObject(MCATResultObject resultObject) {
    	this.resultObjects.add(resultObject);
    }

    public boolean isReady() {
        return isReady;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(Path outputPath) {
        if (isReady)
            throw new RuntimeException("This run's parameters are already locked!");
        this.outputPath = outputPath;
    }


    /**
     * This function must be called before running the graph
     */
    private void prepare() {
        isReady = true;

        if (!Files.exists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Apply output path to the data slots
        for (Map.Entry<String, MCATRunSample> kv : samples.entrySet()) {

            // Apply output path to the data slots
            for (MCATDataSlot slot : kv.getValue().getSlots()) {
                slot.setStorageFilePath(outputPath.resolve(kv.getKey()).resolve(slot.getName()));
                if (!Files.exists(slot.getStorageFilePath())) {
                    try {
                        Files.createDirectories(slot.getStorageFilePath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // Do the same for the subjects
            for (Map.Entry<String, MCATRunSampleSubject> kv2 : kv.getValue().getSubjects().entrySet()) {
                for (MCATDataSlot slot : kv2.getValue().getSlots()) {
                    slot.setStorageFilePath(outputPath.resolve(kv.getKey()).resolve(kv2.getKey()).resolve(slot.getName()));
                    if (!Files.exists(slot.getStorageFilePath())) {
                        try {
                            Files.createDirectories(slot.getStorageFilePath());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public void run(Consumer<Status> onProgress, Supplier<Boolean> isCancelled) {
        prepare();
        int counter = 0;
        for (MCATAlgorithm algorithm : graph.traverse()) {
            if (isCancelled.get())
                throw new RuntimeException("Execution was cancelled");
            onProgress.accept(new Status(counter, graph.size(), algorithm.getName()));
            algorithm.run();
            ++counter;
            onProgress.accept(new Status(counter, graph.size(), algorithm.getName() + " done"));
        }
        writeResults();
        
        boxplotResult();
    }
    
    public void writeResults() {
    	File outputFile = new File(getOutputPath().toString() + File.separator + "ClusteringResults.csv");
    	
    	try {
    		BufferedWriter bw;
    		
    		if(outputFile.exists()) {
    			bw = new BufferedWriter(new FileWriter(outputFile, true));
    		}else {
    			bw = new BufferedWriter(new FileWriter(outputFile));
    			bw.write("subject;treatment;roi;downsamplingFactor;channelOfInterest;clusteringHierarchy;k;postprocessingMethod;auc");
    		}
			for (MCATResultObject resultObject : resultObjects) {
				bw.newLine();
				bw.write(resultObject.toString());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void boxplotResult() {
    	  
    	DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

    	HashMap<String, ArrayList<Double>> entries = new HashMap<String, ArrayList<Double>>(); 

    	for (MCATResultObject obj : resultObjects) {
    		if(entries.get(obj.getTreatment()) == null)
    			entries.put(obj.getTreatment(), new ArrayList<Double>(Arrays.asList(obj.getAuc())));
    		else
    			entries.get(obj.getTreatment()).add(obj.getAuc());
    	}

    	for (String key : entries.keySet()) {
			dataset.add(entries.get(key), key, key);
		}
    	
    	final CategoryAxis xAxis = new CategoryAxis("Treatment");
        final NumberAxis yAxis = new NumberAxis("AUC");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setMeanVisible(true);
        renderer.setUseOutlinePaintForWhiskers(false);
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        

       
        JFrame f = new JFrame("BoxPlot");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JFreeChart chart = new JFreeChart("", plot);
//        		ChartFactory.createBoxAndWhiskerChart("Box and Whisker Chart", "Treatment", "AUC", dataset, false);
        	
        chart.removeLegend();

        f.add(new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(320, 480);
            }
        });
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    	

    public MCATProject getProject() {
        return project;
    }

    public MCATAlgorithmGraph getGraph() {
        return graph;
    }

    @Override
    public void reportValidity(ACAQValidityReport report) {
        report.forCategory("Algorithm graph").report(graph);
    }

    public static class Status {
        private int progress;
        private int maxProgress;
        private String currentTask;

        public Status(int progress, int maxProgress, String currentTask) {
            this.progress = progress;
            this.maxProgress = maxProgress;
            this.currentTask = currentTask;
        }

        public int getProgress() {
            return progress;
        }

        public int getMaxProgress() {
            return maxProgress;
        }

        public String getCurrentTask() {
            return currentTask;
        }
    }
}
