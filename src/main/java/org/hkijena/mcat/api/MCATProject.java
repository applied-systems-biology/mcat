package org.hkijena.mcat.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.dataproviders.FileDataProvider;
import org.hkijena.mcat.api.events.MCATSampleAddedEvent;
import org.hkijena.mcat.api.events.MCATSampleRemovedEvent;
import org.hkijena.mcat.api.events.MCATSampleRenamedEvent;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * An MCAT5 project.
 * It contains all information to setup and run an analysis
 */
@JsonSerialize(using = MCATProject.Serializer.class)
public class MCATProject {

    private EventBus eventBus = new EventBus();
    private MCATPreprocessingParameters preprocessingParameters = new MCATPreprocessingParameters();
    private MCATClusteringParameters clusteringParameters = new MCATClusteringParameters();
    private MCATPostprocessingParameters postprocessingParameters = new MCATPostprocessingParameters();
    private BiMap<String, MCATSample> samples = HashBiMap.create();

    public MCATProject() {
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BiMap<String, MCATSample> getSamples() {
        return ImmutableBiMap.copyOf(samples);
    }

    public Map<String, Set<MCATSample>> getSamplesGroupedByTreatment() {
        Map<String, Set<MCATSample>> result = new HashMap<>();
        for(MCATSample sample : samples.values()) {
            if(!result.containsKey(sample.getParameters().getTreatment())) {
                result.put(sample.getParameters().getTreatment(), new HashSet<>());
            }
            result.get(sample.getParameters().getTreatment()).add(sample);
        }
        return result;
    }

    public MCATSample addSample(String sampleName) {
        if(samples.containsKey(sampleName)) {
            return samples.get(sampleName);
        }
        else {
            MCATSample sample = new MCATSample(this);
            samples.put(sampleName, sample);
            eventBus.post(new MCATSampleAddedEvent(sample));
            return sample;
        }
    }

    public boolean removeSample(MCATSample sample) {
        String name = sample.getName();
        if(samples.containsKey(name)) {
            samples.remove(name);
            eventBus.post(new MCATSampleRemovedEvent(sample));
            return true;
        }
        return false;
    }

    public boolean renameSample(MCATSample sample, String name) {
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
        for(MCATSample sample : samples.values()) {
            if(sample.getParameters().getTreatment() != null)
                result.add(sample.getParameters().getTreatment());
        }
        return result;
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }

    public void saveProject(Path fileName) throws IOException {
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(fileName.toFile(), this);
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
                for(Map.Entry<String, MCATSample> kv : project.getSamples().entrySet()) {
                    jsonGenerator.writeFieldName(kv.getKey());
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeFieldName("children");
                    jsonGenerator.writeStartObject();

                    if(mode.equals("imported")) {
                        serializeDataSlot(kv.getValue().getRawDataInterface().getRawImage(), "raw-image", jsonGenerator);
                        serializeDataSlot(kv.getValue().getRawDataInterface().getTissueROI(), "tissue-roi", jsonGenerator);
                        serializeDataSlot(kv.getValue().getPreprocessedDataInterface().getPreprocessedImage(), "preprocessed-image", jsonGenerator);
                        serializeDataSlot(kv.getValue().getPreprocessedDataInterface().getDerivationMatrix(), "derivation-matrix", jsonGenerator);
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
            for(Map.Entry<String, MCATSample> kv : project.getSamples().entrySet()) {
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
