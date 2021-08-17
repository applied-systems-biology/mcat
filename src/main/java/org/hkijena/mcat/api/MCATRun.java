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
package org.hkijena.mcat.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.algorithms.MCATClusteredPlotGenerationAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPostprocessedPlotGenerationAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPostprocessingAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPreprocessingAlgorithm;
import org.hkijena.mcat.api.datainterfaces.*;
import org.hkijena.mcat.api.events.ParameterChangedEvent;
import org.hkijena.mcat.api.parameters.MCATAUCDataConditions;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATParametersTable;
import org.hkijena.mcat.api.parameters.MCATParametersTableRow;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.utils.JsonUtils;
import org.hkijena.mcat.utils.StringUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.StringComponentNameProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MCATRun implements MCATValidatable {
    private MCATProject project;
    private MCATAlgorithmGraph graph;

    private MCATParametersTable parametersTable;
    private BiMap<MCATDataInterfaceKey, MCATDataInterface> uniqueDataInterfaces = HashBiMap.create();
    private BiMap<MCATDataInterfaceKey, MCATPreprocessingAlgorithm> preprocessingAlgorithmMap = HashBiMap.create();
    private BiMap<MCATDataInterfaceKey, MCATClusteringAlgorithm> clusteringAlgorithmMap = HashBiMap.create();
    private Set<MCATDataInterfaceKey> savedDataInterfaces = new HashSet<>();
    private boolean isReady = false;
    private Path outputPath;


    public MCATRun(MCATProject project) {
        this.project = project;
        this.parametersTable = new MCATParametersTable(project.getParametersTable());
        this.graph = new MCATAlgorithmGraph();

        // Create input data keys
        for (Map.Entry<String, MCATProjectDataSet> entry : project.getDataSets().entrySet()) {
            // Create a new raw data interface
            // It is only identified by its data set name
            MCATPreprocessingInput rawDataInterface = new MCATPreprocessingInput(entry.getValue().getRawDataInterface());
            MCATDataInterfaceKey rawDataInterfaceKey = new MCATDataInterfaceKey("preprocessing-input");
            rawDataInterfaceKey.addDataSet(entry.getKey());
            getOrCreateDataInterface(rawDataInterfaceKey, rawDataInterface);
        }

        // Iterate through unique preprocessing parameters
        Set<MCATPreprocessingParameters> uniquePreprocessingParameters =
                parametersTable.getRows().stream().map(MCATParametersTableRow::getPreprocessingParameters).collect(Collectors.toSet());
        for (MCATPreprocessingParameters preprocessingParameters : uniquePreprocessingParameters) {
            System.out.println("Visiting preprocessing parameters: " + preprocessingParameters.toShortenedString());
            initializePreprocessing(preprocessingParameters);
            System.out.println("Finished visiting preprocessing parameters: " + preprocessingParameters.toShortenedString());
        }

        // Install some functionality to lock the parameters (which completely ruins some assumptions)
        for (MCATParametersTableRow row : parametersTable.getRows()) {
            row.getEventBus().register(new Object() {
                @Subscribe
                public void onParameterChanged(ParameterChangedEvent event) {
                    throw new RuntimeException("Parameter " + event.getKey() + " in " + event.getSource() + " was changed after run generation! This is not allowed. " +
                            "If you want to pass data between algorithms, use a data interface.");
                }
            });
        }
    }

    public Path getScratch(String name) {
        try {
            return Files.createTempDirectory(getOutputPath().resolve("_scratch"), name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MCATDataInterface getOrCreateDataInterface(MCATDataInterfaceKey key, MCATDataInterface defaultEntry) {
        if (!uniqueDataInterfaces.containsKey(key)) {
            System.out.println("New data interface: " + key.toString());
            uniqueDataInterfaces.put(key, defaultEntry);
            return defaultEntry;
        } else {
            return uniqueDataInterfaces.get(key);
        }
    }

    private MCATPreprocessingAlgorithm getOrCreatePreprocessingAlgorithm(MCATDataInterfaceKey key, MCATPreprocessingParameters preprocessingParameters, MCATPreprocessingInput rawDataInterface, MCATPreprocessingOutput preprocessedDataInterface) {
        MCATPreprocessingAlgorithm existing = preprocessingAlgorithmMap.getOrDefault(key, null);
        if (existing == null) {
            System.out.println("New preprocessing algorithm @ " + key + " input=" + preprocessingParameters.toShortenedString());
            existing = new MCATPreprocessingAlgorithm(this,
                    preprocessingParameters,
                    rawDataInterface,
                    preprocessedDataInterface);
            graph.insertNode(existing);
            preprocessingAlgorithmMap.put(key, existing);
            return existing;
        }
        return existing;
    }

    /**
     * Creates the algorithm graph and data interfaces for preprocessing
     *
     * @param preprocessingParameters the parameters
     */
    private void initializePreprocessing(MCATPreprocessingParameters preprocessingParameters) {

        for (Map.Entry<String, MCATProjectDataSet> entry : project.getDataSets().entrySet()) {
            // Create a new preprocessed data interface
            // It is identified by its data set name and the parameters that generated it
            MCATPreprocessingOutput preprocessedDataInterface = new MCATPreprocessingOutput();
            MCATDataInterfaceKey preprocessedDataInterfaceKey = new MCATDataInterfaceKey("preprocessing-output");
            preprocessedDataInterfaceKey.addDataSet(entry.getKey());
            preprocessedDataInterfaceKey.addParameter(preprocessingParameters);
            getOrCreateDataInterface(preprocessedDataInterfaceKey, preprocessedDataInterface);
        }

        // Find all unique clustering parameters with prepending preprocessing
        Set<MCATClusteringParameters> uniqueClusteringParameters = new HashSet<>();
        for (MCATParametersTableRow row : parametersTable.getRows()) {
            if (row.getPreprocessingParameters().equals(preprocessingParameters)) {
                uniqueClusteringParameters.add(row.getClusteringParameters());
            }
        }

        // Go through unique clustering parameters
        for (MCATClusteringParameters clusteringParameters : uniqueClusteringParameters) {
            System.out.println("Visiting clustering parameters: " + clusteringParameters.toShortenedString());
            initializeClustering(preprocessingParameters, clusteringParameters);
            System.out.println("Finished visiting clustering parameters: " + clusteringParameters.toShortenedString());
        }
    }

    private void initializeClustering(MCATPreprocessingParameters preprocessingParameters,
                                      MCATClusteringParameters clusteringParameters) {
        boolean noTreatment = clusteringParameters.getClusteringHierarchy() != MCATClusteringHierarchy.PerTreatment;
        boolean noSubject = clusteringParameters.getClusteringHierarchy() != MCATClusteringHierarchy.PerSubject;

        Set<MCATDataInterfaceKey> matchingPreprocessedInterfaceKeys = uniqueDataInterfaces.keySet().stream()
                .filter(k -> k.getParameters().contains(preprocessingParameters) &&
                        "preprocessing-output".equals(k.getDataInterfaceName())).collect(Collectors.toSet());

        // Map from dataset (subject) -> treatment -> input
        Map<String, Map<String, MCATClusteringInput>> inputGroups = new HashMap<>();
        Map<String, Map<String, MCATClusteringOutput>> outputGroups = new HashMap<>();

        // Keep the list of preprocessing interfaces
        List<MCATPreprocessingOutput> preprocessingOutputs = new ArrayList<>();

        for (MCATDataInterfaceKey preprocessedInterfaceKey : matchingPreprocessedInterfaceKeys) {
            if (preprocessedInterfaceKey.getDataSetNames().size() != 1)
                throw new RuntimeException("Must have exactly one data set reference!");
            String dataSetName = preprocessedInterfaceKey.getDataSetNames().iterator().next();
            MCATDataInterfaceKey rawInterfaceKey = new MCATDataInterfaceKey("preprocessing-input");
            rawInterfaceKey.addDataSets(preprocessedInterfaceKey.getDataSetNames());
            savedDataInterfaces.add(rawInterfaceKey);

            MCATProjectDataSet projectDataSet = project.getDataSets().get(dataSetName);
            MCATPreprocessingInput rawDataInterface = (MCATPreprocessingInput) uniqueDataInterfaces.get(rawInterfaceKey);
            MCATPreprocessingOutput preprocessedDataInterface = (MCATPreprocessingOutput) uniqueDataInterfaces.get(preprocessedInterfaceKey);
            preprocessingOutputs.add(preprocessedDataInterface);

            String groupSubject = projectDataSet.getName();
            String groupTreatment = projectDataSet.getParameters().getTreatment();

            if (noSubject)
                groupSubject = "";
            if (noTreatment)
                groupTreatment = "";

            // Add new entry into clustering input
            {
                Map<String, MCATClusteringInput> subjectMap = inputGroups.getOrDefault(groupSubject, null);
                if (subjectMap == null) {
                    subjectMap = new HashMap<>();
                    inputGroups.put(groupSubject, subjectMap);
                }

                MCATClusteringInput clusteringInput = subjectMap.getOrDefault(groupTreatment, null);
                if (clusteringInput == null) {
                    clusteringInput = new MCATClusteringInput(groupSubject, groupTreatment);
                    subjectMap.put(groupTreatment, clusteringInput);
                }

                clusteringInput.getDataSetEntries().put(projectDataSet.getName(),
                        new MCATClusteringInputDataSetEntry(projectDataSet.getName(),
                                rawDataInterface,
                                preprocessedDataInterface));
            }
            // Add new entry into clustering output
            {
                Map<String, MCATClusteringOutput> subjectMap = outputGroups.getOrDefault(groupSubject, null);
                if (subjectMap == null) {
                    subjectMap = new HashMap<>();
                    outputGroups.put(groupSubject, subjectMap);
                }

                MCATClusteringOutput clusteringOutput = subjectMap.getOrDefault(groupTreatment, null);
                if (clusteringOutput == null) {
                    clusteringOutput = new MCATClusteringOutput(groupSubject, groupTreatment);
                    subjectMap.put(groupTreatment, clusteringOutput);
                }

                clusteringOutput.getDataSetEntries().put(projectDataSet.getName(),
                        new MCATClusteringOutputDataSetEntry(projectDataSet.getName()));
            }
        }

        // Add data interfaces to the unique data set map
        for (String subject : inputGroups.keySet()) {
            for (String treatment : inputGroups.get(subject).keySet()) {
                MCATClusteringInput clusteringInput = inputGroups.get(subject).get(treatment);

                // Provide clustering input with all preprocessing data sets
                clusteringInput.getAllPreprocessingOutputs().addAll(preprocessingOutputs);

                MCATDataInterfaceKey clusteringInputKey = new MCATDataInterfaceKey("clustering-input");
                clusteringInputKey.addParameter(preprocessingParameters);
                clusteringInputKey.addDataSets(clusteringInput.getDataSetEntries().keySet());
                getOrCreateDataInterface(clusteringInputKey, clusteringInput);

                MCATClusteringOutput clusteringOutput = outputGroups.get(subject).get(treatment);
                MCATDataInterfaceKey clusteringOutputKey = new MCATDataInterfaceKey("clustering-output");
                clusteringOutputKey.addParameter(preprocessingParameters);
                clusteringOutputKey.addParameter(clusteringParameters);
                clusteringOutputKey.addDataSets(clusteringInput.getDataSetEntries().keySet());
                getOrCreateDataInterface(clusteringOutputKey, clusteringOutput);
            }
        }


        // Find all unique postprocessing parameters with prepending preprocessing
        Set<MCATPostprocessingParameters> uniquePostProcessingParameters = new HashSet<>();
        for (MCATParametersTableRow row : parametersTable.getRows()) {
            if (row.getPreprocessingParameters().equals(preprocessingParameters) &&
                    row.getClusteringParameters().equals(clusteringParameters)) {
                uniquePostProcessingParameters.add(row.getPostprocessingParameters());
            }
        }

        for (MCATPostprocessingParameters postprocessingParameters : uniquePostProcessingParameters) {
            initializePostprocessing(preprocessingParameters,
                    clusteringParameters,
                    postprocessingParameters);
        }
    }

    private void initializePostprocessing(MCATPreprocessingParameters preprocessingParameters,
                                          MCATClusteringParameters clusteringParameters,
                                          MCATPostprocessingParameters postprocessingParameters) {

        List<MCATPreprocessingAlgorithm> preprocessingAlgorithmList = new ArrayList<>();
        for (MCATDataInterfaceKey preprocessingInputInterfaceKey : uniqueDataInterfaces.keySet().stream()
                .filter(k -> "preprocessing-input".equals(k.getDataInterfaceName())).collect(Collectors.toSet())) {

            MCATDataInterfaceKey preprocessingOutputInterfaceKey = new MCATDataInterfaceKey("preprocessing-output");
            preprocessingOutputInterfaceKey.addDataSets(preprocessingInputInterfaceKey.getDataSetNames());
            preprocessingOutputInterfaceKey.addParameter(preprocessingParameters);

            MCATPreprocessingInput rawDataInterface = (MCATPreprocessingInput) uniqueDataInterfaces.get(preprocessingInputInterfaceKey);
            MCATPreprocessingOutput preprocessedDataInterface = (MCATPreprocessingOutput) uniqueDataInterfaces.get(preprocessingOutputInterfaceKey);
            savedDataInterfaces.add(preprocessingOutputInterfaceKey);

            // Preprocessing
            MCATPreprocessingAlgorithm preprocessingAlgorithm = getOrCreatePreprocessingAlgorithm(preprocessingOutputInterfaceKey,
                    preprocessingParameters,
                    rawDataInterface,
                    preprocessedDataInterface);
            preprocessingAlgorithmList.add(preprocessingAlgorithm);
        }

        List<MCATClusteringAlgorithm> allClusteringAlgorithms = new ArrayList<>();

        for (MCATDataInterfaceKey clusteringOutputInterfaceKey : uniqueDataInterfaces.keySet().stream().filter(k ->
                        "clustering-output".equals(k.getDataInterfaceName()) && k.getParameters().contains(clusteringParameters))
                .collect(Collectors.toSet())) {
            MCATDataInterfaceKey clusteringInputInterfaceKey = new MCATDataInterfaceKey("clustering-input");
            clusteringInputInterfaceKey.addDataSets(clusteringOutputInterfaceKey.getDataSetNames());
            clusteringInputInterfaceKey.addParameter(preprocessingParameters);

            MCATClusteringInput clusteringInputInterface = (MCATClusteringInput) uniqueDataInterfaces.get(clusteringInputInterfaceKey);
            MCATClusteringOutput clusteringOutputInterface = (MCATClusteringOutput) uniqueDataInterfaces.get(clusteringOutputInterfaceKey);
            savedDataInterfaces.add(clusteringOutputInterfaceKey);

            // Create clustering algorithm node, insert it, and let it depend on preprocessing
            MCATClusteringAlgorithm clusteringAlgorithm = getOrCreateClusteringAlgorithm(clusteringOutputInterfaceKey,
                    preprocessingParameters,
                    clusteringParameters,
                    clusteringInputInterface,
                    clusteringOutputInterface);
            allClusteringAlgorithms.add(clusteringAlgorithm);
            for (MCATPreprocessingAlgorithm preprocessingAlgorithm : preprocessingAlgorithmList) {
                graph.connect(preprocessingAlgorithm, clusteringAlgorithm);
            }

            // Postprocessing
            MCATPostprocessingOutput postprocessingDataInterface = new MCATPostprocessingOutput(clusteringInputInterface.getGroupSubject(), clusteringInputInterface.getGroupTreatment());
            MCATDataInterfaceKey postprocessingDataInterfaceKey = new MCATDataInterfaceKey("postprocessing-output");
            postprocessingDataInterfaceKey.addDataSets(clusteringOutputInterfaceKey.getDataSetNames());
            postprocessingDataInterfaceKey.addParameters(clusteringOutputInterfaceKey.getParameters());
            postprocessingDataInterfaceKey.addParameter(postprocessingParameters);
            postprocessingDataInterface = (MCATPostprocessingOutput) getOrCreateDataInterface(postprocessingDataInterfaceKey, postprocessingDataInterface);
            savedDataInterfaces.add(postprocessingDataInterfaceKey);

            MCATPostprocessingAlgorithm postprocessingAlgorithm = new MCATPostprocessingAlgorithm(this,
                    preprocessingParameters,
                    postprocessingParameters,
                    clusteringParameters,
                    clusteringOutputInterface,
                    postprocessingDataInterface);
            graph.insertNode(postprocessingAlgorithm);
            graph.connect(clusteringAlgorithm, postprocessingAlgorithm);

            if (postprocessingParameters.isAnalyzeNetIncrease()) {
                initializePostprocessedPlotGeneration(MCATPostprocessingMethod.NetIncrease, postprocessingAlgorithm, postprocessingDataInterfaceKey);
            }
            if (postprocessingParameters.isAnalyzeNetDecrease()) {
                initializePostprocessedPlotGeneration(MCATPostprocessingMethod.NetDecrease, postprocessingAlgorithm, postprocessingDataInterfaceKey);
            }
            if (postprocessingParameters.isAnalyzeMaxIncrease()) {
                initializePostprocessedPlotGeneration(MCATPostprocessingMethod.MaxIncrease, postprocessingAlgorithm, postprocessingDataInterfaceKey);
            }
            if (postprocessingParameters.isAnalyzeMaxDecrease()) {
                initializePostprocessedPlotGeneration(MCATPostprocessingMethod.MaxDecrease, postprocessingAlgorithm, postprocessingDataInterfaceKey);
            }
        }

        // Pass clustering algorithms to plot generation
        initializeClusteredPlotGeneration(preprocessingParameters, clusteringParameters, allClusteringAlgorithms);
    }

    private MCATClusteringAlgorithm getOrCreateClusteringAlgorithm(MCATDataInterfaceKey key, MCATPreprocessingParameters preprocessingParameters, MCATClusteringParameters clusteringParameters, MCATClusteringInput clusteringInputInterface, MCATClusteringOutput clusteringOutputInterface) {
        MCATClusteringAlgorithm existing = clusteringAlgorithmMap.getOrDefault(key, null);
        if (existing == null) {
            System.out.println("New clustering algorithm @ " + key + " input=" + preprocessingParameters.toShortenedString() + "_" + clusteringParameters.toShortenedString());
            existing = new MCATClusteringAlgorithm(this,
                    preprocessingParameters,
                    clusteringParameters,
                    clusteringInputInterface,
                    clusteringOutputInterface);
            clusteringAlgorithmMap.put(key, existing);
            graph.insertNode(existing);
        }
        return existing;
    }

    private void initializeClusteredPlotGeneration(MCATPreprocessingParameters preprocessingParameters,
                                                   MCATClusteringParameters clusteringParameters,
                                                   List<MCATClusteringAlgorithm> allClusteringAlgorithms) {
        // Create the output
        MCATDataInterfaceKey outputKey = new MCATDataInterfaceKey("clustering-plots");
        outputKey.addParameter(preprocessingParameters);
        outputKey.addParameter(clusteringParameters);
        MCATClusteredPlotGenerationOutput output = new MCATClusteredPlotGenerationOutput();
        output = (MCATClusteredPlotGenerationOutput) getOrCreateDataInterface(outputKey, output);
        savedDataInterfaces.add(outputKey);

        // Create the input interface
        MCATClusteredPlotGenerationInput input = new MCATClusteredPlotGenerationInput();
        for (MCATClusteringAlgorithm clusteringAlgorithm : allClusteringAlgorithms) {
            String subject = clusteringAlgorithm.getClusteringInput().getGroupSubject();
            String treatment = clusteringAlgorithm.getClusteringInput().getGroupTreatment();
            if (StringUtils.isNullOrEmpty(subject))
                subject = "ALL_SUBJECTS";
            if (StringUtils.isNullOrEmpty(treatment))
                treatment = "ALL_TREATMENTS";
            String id = subject + "__" + treatment;
            input.getClusteringOutputMap().put(id, clusteringAlgorithm.getClusteringOutput());
        }

        // Create the algorithm
        MCATClusteredPlotGenerationAlgorithm algorithm = new MCATClusteredPlotGenerationAlgorithm(this, input, output);
        graph.insertNode(algorithm);
        for (MCATClusteringAlgorithm clusteringAlgorithm : allClusteringAlgorithms) {
            graph.connect(clusteringAlgorithm, algorithm);
        }
    }

    private void initializePostprocessedPlotGeneration(MCATPostprocessingMethod method,
                                                       MCATPostprocessingAlgorithm postprocessingAlgorithm,
                                                       MCATDataInterfaceKey postprocessingDataInterfaceKey) {
        MCATAUCDataConditions conditions = new MCATAUCDataConditions(method);
        // Create output data
        MCATDataInterfaceKey plotDataInterfaceKey = new MCATDataInterfaceKey("postprocessing-plots");
        plotDataInterfaceKey.addDataSets(postprocessingDataInterfaceKey.getDataSetNames());
        plotDataInterfaceKey.addParameters(postprocessingDataInterfaceKey.getParameters());
        plotDataInterfaceKey.addParameter(conditions);

        MCATPostprocessedPlotGenerationOutput plotGenerationOutput = new MCATPostprocessedPlotGenerationOutput(postprocessingAlgorithm.getPostprocessingOutput().getGroupSubject(),
                postprocessingAlgorithm.getPostprocessingOutput().getGroupTreatment());
        plotGenerationOutput = (MCATPostprocessedPlotGenerationOutput) getOrCreateDataInterface(plotDataInterfaceKey, plotGenerationOutput);
        savedDataInterfaces.add(plotDataInterfaceKey);

        // Create the algorithm instance
        MCATPostprocessedPlotGenerationAlgorithm plotGenerationAlgorithm = new MCATPostprocessedPlotGenerationAlgorithm(this,
                postprocessingAlgorithm.getPreprocessingParameters(),
                postprocessingAlgorithm.getPostprocessingParameters(),
                postprocessingAlgorithm.getClusteringParameters(),
                conditions,
                postprocessingAlgorithm.getClusteringOutput(),
                postprocessingAlgorithm.getPostprocessingOutput(),
                plotGenerationOutput);
        graph.insertNode(plotGenerationAlgorithm);
        graph.connect(postprocessingAlgorithm, plotGenerationAlgorithm);
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
     * Finds the parameter keys in the parameter table that contain information
     *
     * @return parameter keys
     */
    private Set<String> getRelevantParameterKeys() {
        Set<String> result = new HashSet<>();
        for (int column = 0; column < parametersTable.getColumnCount(); column++) {
            String key = parametersTable.getColumnKey(column);
            Set<Object> values = new HashSet<>();
            for (int row = 0; row < parametersTable.getRowCount(); row++) {
                values.add(parametersTable.getValueAt(row, column));
            }
            if (values.size() > 1) {
                result.add(key);
            }
        }
        return result;
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

        try {
            JsonUtils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outputPath.resolve("project.json").toFile(), project);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (MCATDataInterfaceKey key : savedDataInterfaces) {
            setDataInterfaceStoragePath(key, uniqueDataInterfaces.get(key));
        }
        for (MCATDataInterface dataInterface : uniqueDataInterfaces.values()) {
            for (MCATDataSlot dataSlot : dataInterface.getSlots().values()) {
                if (dataSlot.getStorageFilePath() != null) {
                    if (!Files.isDirectory(dataSlot.getStorageFilePath())) {
                        try {
                            Files.createDirectories(dataSlot.getStorageFilePath());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        Set<Path> outputFileNames = new HashSet<>();
        for (MCATDataInterfaceKey key : savedDataInterfaces) {
            MCATDataInterface dataInterface = uniqueDataInterfaces.get(key);
            for (Map.Entry<String, MCATDataSlot> entry : dataInterface.getSlots().entrySet()) {
                if (entry.getValue().getStorageFilePath() != null && entry.getValue().getFileName() != null) {
                    Path outputFileName = entry.getValue().getStorageFilePath().resolve(entry.getValue().getFileName());
                    if (outputFileNames.contains(outputFileName)) {
                        System.err.println("Duplicate output file name: " + outputFileName + ", slot-id=" + entry.getKey() + ", key=" + key);
                    }
                    outputFileNames.add(outputFileName);
                }
            }

        }


//        MCATResultDataInterfaces exportedSavedInterfaces = new MCATResultDataInterfaces();
//        for (MCATDataInterfaceKey key : savedDataInterfaces) {
//            MCATResultDataInterfaces.DataInterfaceEntry dataInterfaceEntry = setDataInterfaceStoragePath(key, uniqueDataInterfaces.get(key));
//            exportedSavedInterfaces.getEntries().add(dataInterfaceEntry);
//        }
//        try {
//            JsonUtils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outputPath.resolve("data.json").toFile(), exportedSavedInterfaces);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private void setDataInterfaceStoragePath(MCATDataInterfaceKey key, MCATDataInterface dataInterface) {

        if (dataInterface instanceof MCATPreprocessingInput) {
            String subjects = key.getDataSetNames().stream().sorted().collect(Collectors.joining(","));
            MCATPreprocessingInput preprocessingInput = (MCATPreprocessingInput) dataInterface;

            // Raw images preprocessingInput/rawImages/<subject>_rawImage.tif
            preprocessingInput.getRawImage().setStorageFilePath(outputPath.resolve("preprocessingInput").resolve("rawImages"));
            preprocessingInput.getRawImage().setFileName(Paths.get(subjects + "_rawImage.tif"));

            // Tissue ROIs preprocessingInput/tissueRois/<subject>_<roiName>_roiFile.roi
            preprocessingInput.getTissueROI().setStorageFilePath(outputPath.resolve("preprocessingInput").resolve("tissueRois"));
            preprocessingInput.getTissueROI().setFileName(Paths.get(subjects)); // roiName will be set before flush
        } else if (dataInterface instanceof MCATPreprocessingOutput) {
            String subjects = key.getDataSetNames().stream().sorted().collect(Collectors.joining(","));
            MCATPreprocessingOutput preprocessingOutput = (MCATPreprocessingOutput) dataInterface;
            String identifier = getPreprocessingIdentifier(key, subjects);

            // Preprocessed images preprocessingOutput/preprocessedImages/<subject>_anatomyCh_<x>-signalCh-<x>_down-<x>_preprocessedImage.tif
            preprocessingOutput.getPreprocessedImage().setStorageFilePath(outputPath.resolve("preprocessingOutput").resolve("preprocessedImages"));
            preprocessingOutput.getPreprocessedImage().setFileName(Paths.get(identifier + "_preprocessedImage.tif"));

            // Derivative matrix preprocessingOutput/derivativeMatrices/<subject>_anatomyCh_<x>-signalCh-<x>_down-<x>_der.tf
            preprocessingOutput.getDerivativeMatrix().setStorageFilePath(outputPath.resolve("preprocessingOutput").resolve("derivativeMatrices"));
            preprocessingOutput.getDerivativeMatrix().setFileName(Paths.get(identifier + "_derivativeMatrix.csv"));
        } else if (dataInterface instanceof MCATClusteringOutput) {
            MCATClusteringOutput clusteringOutput = (MCATClusteringOutput) dataInterface;
            String identifier = getClusteringIdentifier(key);


            String group = getGroupIdentifier(clusteringOutput.getGroupTreatment(), clusteringOutput.getGroupSubject());

            for (Map.Entry<String, MCATClusteringOutputDataSetEntry> entry : clusteringOutput.getDataSetEntries().entrySet()) {
                String subjects = entry.getKey();
//               String treatment = getProject().getDataSets().get(subjects).getParameters().getTreatment(); // TODO: I'm not really sure whether to choose the groupTreatment (by which the clusteringOutput is generated) or the actual treatment of the dataset
                MCATClusteringOutputDataSetEntry dataSetEntry = entry.getValue();

                // Cluster abundance clusteringOutput/clusterAbundance/<treatment>/<subject>_anatomyCh_<x>-signalCh-<x>_down-<x>_grouping-<x>_minT-<x>_maxT-<x>_clusterAbundance.csv
                dataSetEntry.getClusterAbundance().setStorageFilePath(outputPath.resolve("clusteringOutput").resolve("clusterAbundance").resolve(group));
                dataSetEntry.getClusterAbundance().setFileName(Paths.get(subjects + "_" + identifier + "_clusterAbundance.csv"));

                // Cluster images clusteringOutput/clusteredImages/<treatment>/<subject>_anatomyCh_<x>-signalCh-<x>_down-<x>_grouping-<x>_minT-<x>_maxT-<x>_clusterAbundance.csv
                dataSetEntry.getClusterImages().setStorageFilePath(outputPath.resolve("clusteringOutput").resolve("clusteredImages").resolve(group));
                dataSetEntry.getClusterImages().setFileName(Paths.get(subjects + "_" + identifier + "_clusteredImage.tif"));
            }

            // Cluster centers clusteringOutput/clusterCenters/<treatment>_anatomyCh_<x>-signalCh-<x>_down-<x>_grouping-<x>_minT-<x>_maxT-<x>_clusterCenters.csv
            clusteringOutput.getClusterCenters().setStorageFilePath(outputPath.resolve("clusteringOutput").resolve("clusterCenters"));
            clusteringOutput.getClusterCenters().setFileName(Paths.get(group + "_" + identifier + "_clusterCenters.csv"));
        } else if (dataInterface instanceof MCATClusteredPlotGenerationOutput) {
            MCATClusteredPlotGenerationOutput plotGenerationOutput = (MCATClusteredPlotGenerationOutput) dataInterface;

            // Clustering plots clusteringPlots/anatomyCh_<x>-signalCh-<x>_down-<x>_grouping-<x>_minT-<x>_maxT-<x>_clusterCentersPlot<.png/svg/csv/...>
            plotGenerationOutput.getTimeDerivativePlot().setStorageFilePath(outputPath.resolve("clusteringPlots"));
            plotGenerationOutput.getTimeDerivativePlot().setFileName(Paths.get(getClusteringIdentifier(key) + "_clusterCentersPlot"));
        } else if (dataInterface instanceof MCATPostprocessingOutput) {
            MCATPostprocessingOutput postprocessingOutput = (MCATPostprocessingOutput) dataInterface;

            String group = getGroupIdentifier(postprocessingOutput.getGroupTreatment(), postprocessingOutput.getGroupSubject());

            // Clustering plots postprocessingOutput/<treatment>_anatomyCh_<x>-signalCh-<x>_down-<x>_grouping-<x>_minT-<x>_maxT-<x>_aucData.csv
            postprocessingOutput.getAuc().setStorageFilePath(outputPath.resolve("postprocessingOutput"));
            postprocessingOutput.getAuc().setFileName(Paths.get(group + "_" + getClusteringIdentifier(key) + "_aucData.csv"));

        } else if (dataInterface instanceof MCATPostprocessedPlotGenerationOutput) {
            MCATPostprocessedPlotGenerationOutput plotGenerationOutput = (MCATPostprocessedPlotGenerationOutput) dataInterface;
            String group = getGroupIdentifier(plotGenerationOutput.getGroupTreatment(), plotGenerationOutput.getGroupSubject());

            // AUC plot postprocessingPlots/anatomyCh_<x>-signalCh-<x>_down-<x>_grouping-<x>_minT-<x>_maxT-<x>_aucPlot.<png/svg/csv,...>
            plotGenerationOutput.getAucPlotData().setStorageFilePath(outputPath.resolve("postprocessingPlots"));
            plotGenerationOutput.getAucPlotData().setFileName(Paths.get(group + "_" + getClusteringIdentifier(key) + "_aucPlot"));
        }
    }

    private String getGroupIdentifier(String groupTreatment, String groupSubject) {
        if (StringUtils.isNullOrEmpty(groupTreatment) && StringUtils.isNullOrEmpty(groupSubject)) {
            return "ALL";
        } else if (StringUtils.isNullOrEmpty(groupTreatment)) {
            return groupSubject;
        } else if (StringUtils.isNullOrEmpty(groupSubject)) {
            return groupTreatment;
        } else {
            return groupTreatment + "-" + groupSubject;
        }
    }

    private String getPreprocessingIdentifier(MCATDataInterfaceKey key, String subjects) {
        MCATPreprocessingParameters preprocessingParameters = key.getParameterOfType(MCATPreprocessingParameters.class);
        String identifier = subjects + "_" +
                "anatomyCh-" + preprocessingParameters.getAnatomicChannel() + "_" +
                "signalCh-" + preprocessingParameters.getChannelOfInterest() + "_" +
                "down-" + preprocessingParameters.getDownsamplingFactor();
        if (preprocessingParameters.getMinTime() != MCATPreprocessingParameters.MIN_TIME_DEFAULT) {
            identifier += "_" + "startT" + "-" + preprocessingParameters.getMinTime();
        }
        if (preprocessingParameters.getMaxTime() != MCATPreprocessingParameters.MAX_TIME_DEFAULT) {
            identifier += "_" + "endT" + "-" + preprocessingParameters.getMaxTime();
        }
        return identifier;
    }

    private String getClusteringIdentifier(MCATDataInterfaceKey key) {
        MCATPreprocessingParameters preprocessingParameters = key.getParameterOfType(MCATPreprocessingParameters.class);
        MCATClusteringParameters clusteringParameters = key.getParameterOfType(MCATClusteringParameters.class);
        String identifier = "anatomyCh-" + preprocessingParameters.getAnatomicChannel() + "_" +
                "signalCh-" + preprocessingParameters.getChannelOfInterest() + "_" +
                "down-" + preprocessingParameters.getDownsamplingFactor() + "_" +
                "grouping-" + clusteringParameters.getClusteringHierarchy().name() + "_" +
                "k-" + clusteringParameters.getkMeansK();
        if (preprocessingParameters.getMinTime() != MCATPreprocessingParameters.MIN_TIME_DEFAULT) {
            identifier += "_" + "startT" + "-" + preprocessingParameters.getMinTime();
        }
        if (preprocessingParameters.getMaxTime() != MCATPreprocessingParameters.MAX_TIME_DEFAULT) {
            identifier += "_" + "endT" + "-" + preprocessingParameters.getMaxTime();
        }
        return identifier;
    }

    public void run(Consumer<Status> onProgress, Supplier<Boolean> isCancelled) {
        // Save graph dot file for debugging
        DOTExporter<MCATAlgorithm, DefaultEdge> exporter = new DOTExporter<>(new IntegerComponentNameProvider<>(), new StringComponentNameProvider<MCATAlgorithm>() {
            @Override
            public String getName(MCATAlgorithm component) {
                if (component instanceof MCATPreprocessingAlgorithm)
                    return component.getName() + " " + ((MCATPreprocessingAlgorithm) component).getPreprocessingParameters().toShortenedString();
                else if (component instanceof MCATClusteringAlgorithm)
                    return component.getName() + " " + ((MCATClusteringAlgorithm) component).getPreprocessingParameters().toShortenedString() + " " + ((MCATClusteringAlgorithm) component).getClusteringParameters().toShortenedString();
                else if (component instanceof MCATPostprocessingAlgorithm)
                    return component.getName() + " " + ((MCATPostprocessingAlgorithm) component).getPreprocessingParameters().toShortenedString() + " " +
                            ((MCATPostprocessingAlgorithm) component).getClusteringParameters().toShortenedString() + " " + ((MCATPostprocessingAlgorithm) component).getPostprocessingParameters().toShortenedString();
                return component.getName();
            }
        }, null);
        try {
            exporter.exportGraph(graph.getGraph(), outputPath.resolve("graph.dot").toFile());
        } catch (ExportException e) {
            e.printStackTrace();
        }
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
        System.out.println("Finished Run!");
    }

    public MCATProject getProject() {
        return project;
    }

    public MCATAlgorithmGraph getGraph() {
        return graph;
    }

    @Override
    public void reportValidity(MCATValidityReport report) {
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
