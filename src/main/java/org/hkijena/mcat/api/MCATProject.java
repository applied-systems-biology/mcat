package org.hkijena.mcat.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.dataproviders.FileDataProvider;
import org.hkijena.mcat.api.events.MCATSampleAddedEvent;
import org.hkijena.mcat.api.events.MCATSampleRemovedEvent;
import org.hkijena.mcat.api.events.MCATSampleRenamedEvent;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATParametersTable;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.utils.JsonUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * An MCAT5 project.
 * It contains all information to setup and run an analysis
 */
@JsonSerialize(using = MCATProject.Serializer.class)
@JsonDeserialize(using = MCATProject.Deserializer.class)
public class MCATProject {

    private EventBus eventBus = new EventBus();
    private MCATParametersTable parametersTable = new MCATParametersTable();
    private BiMap<String, MCATProjectSample> samples = HashBiMap.create();

    public MCATProject() {
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BiMap<String, MCATProjectSample> getSamples() {
        return ImmutableBiMap.copyOf(samples);
    }

    public Map<String, List<MCATProjectSample>> getSamplesByTreatment() {
        Map<String, List<MCATProjectSample>> result = new HashMap<>();
        for(MCATProjectSample sample : samples.values()) {
            if(!result.containsKey(sample.getParameters().getTreatment())) {
                result.put(sample.getParameters().getTreatment(), new ArrayList<>());
            }
            result.get(sample.getParameters().getTreatment()).add(sample);
        }
        return result;
    }

    public Map<String, Set<MCATProjectSample>> getSamplesGroupedByTreatment() {
        Map<String, Set<MCATProjectSample>> result = new HashMap<>();
        for(MCATProjectSample sample : samples.values()) {
            if(!result.containsKey(sample.getParameters().getTreatment())) {
                result.put(sample.getParameters().getTreatment(), new HashSet<>());
            }
            result.get(sample.getParameters().getTreatment()).add(sample);
        }
        return result;
    }

    public MCATProjectSample addSample(String sampleName) {
        if(samples.containsKey(sampleName)) {
            return samples.get(sampleName);
        }
        else {
            MCATProjectSample sample = new MCATProjectSample(this);
            samples.put(sampleName, sample);
            eventBus.post(new MCATSampleAddedEvent(sample));
            return sample;
        }
    }

    public boolean removeSample(MCATProjectSample sample) {
        String name = sample.getName();
        if(samples.containsKey(name)) {
            samples.remove(name);
            eventBus.post(new MCATSampleRemovedEvent(sample));
            return true;
        }
        return false;
    }

    public boolean renameSample(MCATProjectSample sample, String name) {
        if(name == null)
            return false;
        name = name.trim();
        if(name.isEmpty() || samples.containsKey(name))
            return false;
        samples.remove(sample.getName());
        samples.put(name, sample);
        eventBus.post(new MCATSampleRenamedEvent(sample));
        return true;
    }

    public Set<String> getKnownTreatments() {
        Set<String> result = new HashSet<>();
        for(MCATProjectSample sample : samples.values()) {
            if(sample.getParameters().getTreatment() != null)
                result.add(sample.getParameters().getTreatment());
        }
        return result;
    }

    public void saveProject(Path fileName) throws IOException {
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(fileName.toFile(), this);
    }

    public MCATParametersTable getParametersTable() {
        return parametersTable;
    }

    public static MCATProject loadProject(Path fileName) throws IOException {
        return JsonUtils.getObjectMapper().readerFor(MCATProject.class).readValue(fileName.toFile());
    }

    public static class Deserializer extends JsonDeserializer<MCATProject> {
        @Override
        public MCATProject deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            MCATProject project = new MCATProject();
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if(node.has("samples"))
                readSamples(project, node.get("samples"));
            if(node.has("algorithm"))
                readAlgorithmParameters(project, node.get("algorithm"));

            JsonNode importedDataNode = node.path("filesystem").path("json-data").path("imported").path("children");
            if(!importedDataNode.isMissingNode()) {
                for(Map.Entry<String, MCATProjectSample> kv : project.getSamples().entrySet()) {
                    if(importedDataNode.has(kv.getKey())) {
                        JsonNode dataSlotsNode = importedDataNode.get(kv.getKey()).path("children");
                        if(!dataSlotsNode.isMissingNode()) {
                            for(Map.Entry<String, JsonNode> slot : ImmutableList.copyOf(dataSlotsNode.fields())) {
                                switch (slot.getKey()) {
                                    case "raw-image":
                                        readDataSlot(kv.getValue().getRawDataInterface().getRawImage(), slot.getValue());
                                        break;
                                    case "tissue-roi":
                                        readDataSlot(kv.getValue().getRawDataInterface().getTissueROI(), slot.getValue());
                                        break;
                                    case "preprocessed-image":
                                        readDataSlot(kv.getValue().getPreprocessedDataInterface().getPreprocessedImage(), slot.getValue());
                                        break;
                                    case "derivative-matrix":
                                        readDataSlot(kv.getValue().getPreprocessedDataInterface().getDerivativeMatrix(), slot.getValue());
                                        break;
                                    case "cluster-image":
                                        readDataSlot(kv.getValue().getClusteredDataInterface().getClusterImages(), slot.getValue());
                                        break;
                                    case "cluster-centers":
                                        readDataSlot(kv.getValue().getClusteredDataInterface().getClusterCenters(), slot.getValue());
                                        break;
                                }
                            }
                        }
                    }
                }
            }

            return project;
        }

        private void readSamples(MCATProject project, JsonNode node) throws IOException {
            for(Map.Entry<String, JsonNode> kv : ImmutableList.copyOf(node.fields())) {
                MCATProjectSample sample = project.addSample(kv.getKey());
                JsonUtils.getObjectMapper().readerForUpdating(sample.getParameters()).readValue(kv.getValue().traverse());
            }
        }

        private void readAlgorithmParameters(MCATProject project, JsonNode node) throws IOException {
            JsonUtils.getObjectMapper().readerForUpdating(project.getPreprocessingParameters()).readValue(node.get("preprocessing").traverse());
            JsonUtils.getObjectMapper().readerForUpdating(project.getClusteringParameters()).readValue(node.get("clustering").traverse());
            JsonUtils.getObjectMapper().readerForUpdating(project.getPostprocessingParameters()).readValue(node.get("postprocessing").traverse());
        }

        private void readDataSlot(MCATDataSlot<?> slot, JsonNode node) {
            JsonNode filePathNode = node.path("metadata").path("file-path");
            if(!filePathNode.isMissingNode()) {
                slot.setCurrentProvider(FileDataProvider.class);
                slot.getProvider(FileDataProvider.class).setFilePath(Paths.get(filePathNode.asText()));
            }
        }
    }

    public static class Serializer extends JsonSerializer<MCATProject> {
        @Override
        public void serialize(MCATProject project, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeStartObject();
            serializeFileSystem(project, jsonGenerator);
            serializeAlgorithmParameters(project, jsonGenerator);
            serializeSampleParameters(project, jsonGenerator);
            jsonGenerator.writeEndObject();
        }

        private void serializeDataSlot(MCATDataSlot<?> slot, String name, JsonGenerator jsonGenerator) throws IOException {
            if(!slot.hasData() && slot.getCurrentProvider() instanceof FileDataProvider) {
                FileDataProvider<?> provider = (FileDataProvider<?>)slot.getCurrentProvider();
                if(provider.getFilePath() != null) {
                    jsonGenerator.writeFieldName(name);
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeFieldName("metadata");
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("file-path", provider.getFilePath().toString());
                    jsonGenerator.writeEndObject();
                    jsonGenerator.writeEndObject();
                }
            }
        }

        private void serializeFileSystem(MCATProject project, JsonGenerator jsonGenerator) throws IOException {
            jsonGenerator.writeFieldName("filesystem");
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("source", "json");
            jsonGenerator.writeFieldName("json-data");
            jsonGenerator.writeStartObject();

            for(String mode : Arrays.asList("imported", "exported")) {
                jsonGenerator.writeFieldName(mode);
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName("children");
                jsonGenerator.writeStartObject();
                for(Map.Entry<String, MCATProjectSample> kv : project.getSamples().entrySet()) {
                    jsonGenerator.writeFieldName(kv.getKey());
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeFieldName("children");
                    jsonGenerator.writeStartObject();

                    if(mode.equals("imported")) {
                        serializeDataSlot(kv.getValue().getRawDataInterface().getRawImage(), "raw-image", jsonGenerator);
                        serializeDataSlot(kv.getValue().getRawDataInterface().getTissueROI(), "tissue-roi", jsonGenerator);
                        serializeDataSlot(kv.getValue().getPreprocessedDataInterface().getPreprocessedImage(), "preprocessed-image", jsonGenerator);
                        serializeDataSlot(kv.getValue().getPreprocessedDataInterface().getDerivativeMatrix(), "derivative-matrix", jsonGenerator);
                        serializeDataSlot(kv.getValue().getClusteredDataInterface().getClusterImages(), "cluster-image", jsonGenerator);
                        serializeDataSlot(kv.getValue().getClusteredDataInterface().getClusterCenters(), "cluster-centers", jsonGenerator);
                    }

                    jsonGenerator.writeEndObject();
                    jsonGenerator.writeEndObject();
                }
                jsonGenerator.writeEndObject();
                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        }

        private void serializeSampleParameters(MCATProject project, JsonGenerator jsonGenerator) throws IOException {
            jsonGenerator.writeFieldName("samples");
            jsonGenerator.writeStartObject();
            for(Map.Entry<String, MCATProjectSample> kv : project.getSamples().entrySet()) {
                jsonGenerator.writeObjectField(kv.getKey(), kv.getValue().getParameters());
            }
            jsonGenerator.writeEndObject();
        }

        private void serializeAlgorithmParameters(MCATProject project, JsonGenerator jsonGenerator) throws IOException {
            jsonGenerator.writeFieldName("algorithm");
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("preprocessing", project.getPreprocessingParameters());
            jsonGenerator.writeObjectField("clustering", project.getClusteringParameters());
            jsonGenerator.writeObjectField("postprocessing", project.getPostprocessingParameters());
            jsonGenerator.writeEndObject();
        }
    }
}
