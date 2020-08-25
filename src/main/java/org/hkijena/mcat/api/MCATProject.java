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
package org.hkijena.mcat.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hkijena.mcat.api.events.DataSetAddedEvent;
import org.hkijena.mcat.api.events.DataSetRemovedEvent;
import org.hkijena.mcat.api.events.DataSetRenamedEvent;
import org.hkijena.mcat.api.parameters.MCATParametersTable;
import org.hkijena.mcat.utils.JsonUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

/**
 * An MCAT5 project.
 * It contains all information to setup and run an analysis
 */
@JsonSerialize(using = MCATProject.Serializer.class)
@JsonDeserialize(using = MCATProject.Deserializer.class)
public class MCATProject {

    private EventBus eventBus = new EventBus();
    private MCATParametersTable parametersTable = new MCATParametersTable();
    private BiMap<String, MCATProjectDataSet> dataSets = HashBiMap.create();

    public MCATProject() {
        parametersTable.addRow();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BiMap<String, MCATProjectDataSet> getDataSets() {
        return ImmutableBiMap.copyOf(dataSets);
    }

    public Map<String, List<MCATProjectDataSet>> getSamplesByTreatment() {
        Map<String, List<MCATProjectDataSet>> result = new HashMap<>();
        for (MCATProjectDataSet sample : dataSets.values()) {
            if (!result.containsKey(sample.getParameters().getTreatment())) {
                result.put(sample.getParameters().getTreatment(), new ArrayList<>());
            }
            result.get(sample.getParameters().getTreatment()).add(sample);
        }
        return result;
    }

    public Map<String, Set<MCATProjectDataSet>> getSamplesGroupedByTreatment() {
        Map<String, Set<MCATProjectDataSet>> result = new HashMap<>();
        for (MCATProjectDataSet sample : dataSets.values()) {
            if (!result.containsKey(sample.getParameters().getTreatment())) {
                result.put(sample.getParameters().getTreatment(), new HashSet<>());
            }
            result.get(sample.getParameters().getTreatment()).add(sample);
        }
        return result;
    }

    public MCATProjectDataSet addSample(String sampleName) {
        if (dataSets.containsKey(sampleName)) {
            return dataSets.get(sampleName);
        } else {
            MCATProjectDataSet sample = new MCATProjectDataSet(this);
            dataSets.put(sampleName, sample);
            eventBus.post(new DataSetAddedEvent(sample));
            return sample;
        }
    }

    public boolean removeSample(MCATProjectDataSet sample) {
        String name = sample.getName();
        if (dataSets.containsKey(name)) {
            dataSets.remove(name);
            eventBus.post(new DataSetRemovedEvent(sample));
            return true;
        }
        return false;
    }

    public boolean renameSample(MCATProjectDataSet sample, String name) {
        if (name == null)
            return false;
        name = name.trim();
        if (name.isEmpty() || dataSets.containsKey(name))
            return false;
        dataSets.remove(sample.getName());
        dataSets.put(name, sample);
        eventBus.post(new DataSetRenamedEvent(sample));
        return true;
    }

    public Set<String> getKnownTreatments() {
        Set<String> result = new HashSet<>();
        for (MCATProjectDataSet sample : dataSets.values()) {
            if (sample.getParameters().getTreatment() != null)
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
            JsonNode node = jsonParser.readValueAsTree();
            project.parametersTable = JsonUtils.getObjectMapper().readerFor(MCATParametersTable.class).readValue(node.get("parameters"));
            for (Map.Entry<String, JsonNode> dataSetEntry : ImmutableList.copyOf(node.get("data-sets").fields())) {
                MCATProjectDataSet dataSet = JsonUtils.getObjectMapper().readerFor(MCATProjectDataSet.class).readValue(dataSetEntry.getValue());
                dataSet.setProject(project);
                project.dataSets.put(dataSetEntry.getKey(), dataSet);
            }
            return project;
        }
    }

    public static class Serializer extends JsonSerializer<MCATProject> {
        @Override
        public void serialize(MCATProject project, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("parameters", project.parametersTable);
            jsonGenerator.writeFieldName("data-sets");
            jsonGenerator.writeStartObject();
            for (Map.Entry<String, MCATProjectDataSet> entry : project.dataSets.entrySet()) {
                jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
            }
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        }
    }
}
