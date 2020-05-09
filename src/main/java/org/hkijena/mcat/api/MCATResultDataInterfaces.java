package org.hkijena.mcat.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hkijena.mcat.utils.JsonUtils;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATTraversedParameterCollection;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains information where data was saved in the results folder
 */
public class MCATResultDataInterfaces {

    private List<DataInterfaceEntry> entries = new ArrayList<>();

    public MCATResultDataInterfaces() {

    }

    @JsonGetter("entries")
    public List<DataInterfaceEntry> getEntries() {
        return entries;
    }

    @JsonSetter("entries")
    public void setEntries(List<DataInterfaceEntry> entries) {
        this.entries = entries;
    }

    public static class DataInterfaceEntry {

        private String name;
        private List<String> dataSets = new ArrayList<>();
        private List<JsonNode> parameters = new ArrayList<>();
        private String parameterString;
        private List<SlotEntry> slots = new ArrayList<>();

        public DataInterfaceEntry() {

        }

        public DataInterfaceEntry(MCATDataInterfaceKey key) {
            this.name = key.getDataInterfaceName();
            this.dataSets.addAll(key.getDataSetNames());

            for (MCATParameterCollection parameter : key.getParameters()) {
                ObjectNode node = JsonUtils.getObjectMapper().getNodeFactory().objectNode();
                MCATTraversedParameterCollection traversedParameterCollection = new MCATTraversedParameterCollection(parameter);
                for (Map.Entry<String, MCATParameterAccess> entry : traversedParameterCollection.getParameters().entrySet()) {
                   node.set(entry.getKey(), JsonUtils.getObjectMapper().convertValue(entry.getValue().get(), JsonNode.class));
                }
                parameters.add(node);
            }
        }

        @JsonGetter("name")
        public String getName() {
            return name;
        }

        @JsonSetter("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonGetter("data-sets")
        public List<String> getDataSets() {
            return dataSets;
        }

        @JsonSetter("data-sets")
        public void setDataSets(List<String> dataSets) {
            this.dataSets = dataSets;
        }

        @JsonGetter("parameters")
        public List<JsonNode> getParameters() {
            return parameters;
        }

        @JsonSetter("parameters")
        public void setParameters(List<JsonNode> parameters) {
            this.parameters = parameters;
        }

        @JsonGetter("slots")
        public List<SlotEntry> getSlots() {
            return slots;
        }

        @JsonSetter("slots")
        public void setSlots(List<SlotEntry> slots) {
            this.slots = slots;
        }

        @JsonGetter("parameter-string")
        public String getParameterString() {
            return parameterString;
        }

        @JsonSetter("parameter-string")
        public void setParameterString(String parameterString) {
            this.parameterString = parameterString;
        }
    }

    public static class SlotEntry {

        private String name;
        private Path storagePath;

        public SlotEntry() {

        }

        public SlotEntry(String name, Path storagePath) {
            this.name = name;
            this.storagePath = storagePath;
        }

        @JsonGetter("name")
        public String getName() {
            return name;
        }

        @JsonSetter("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonGetter("storage-path")
        public Path getStoragePath() {
            return storagePath;
        }

        @JsonSetter("storage-path")
        public void setStoragePath(Path storagePath) {
            this.storagePath = storagePath;
        }
    }
}
