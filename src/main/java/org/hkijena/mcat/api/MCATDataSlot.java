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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hkijena.mcat.api.registries.MCATDataTypeRegistry;
import org.hkijena.mcat.utils.JsonUtils;
import org.hkijena.mcat.utils.StringUtils;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A slot holds data and also have the capability to load data from an MCATDataProvider if data is requested, but not set
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public class MCATDataSlot {

    private String name;
    private Class<? extends MCATData> acceptedDataType;
    private MCATData data;
    private MCATDataProvider dataProvider;
    private Path fileName;

    /**
     * The path where the slot stores its data
     */
    private Path storageFilePath;

    public MCATDataSlot(String name, Class<? extends MCATData> acceptedDataType) {
        this.name = name;
        this.acceptedDataType = acceptedDataType;
    }

    public MCATDataSlot(MCATDataSlot other) {
        this.name = other.name;
        this.acceptedDataType = other.acceptedDataType;
        if (other.dataProvider != null) {
            this.dataProvider = other.dataProvider.duplicate();
        }
    }

    public Class<? extends MCATData> getAcceptedDataType() {
        return acceptedDataType;
    }

    /**
     * Gets the data stored within this slot.
     * If no data is loaded, but an {@link MCATDataProvider} is set, the data is automatically set to the {@link MCATDataProvider} result.
     *
     * @param klass target class
     * @param <T>   target class
     * @return data already stored within this slot or result of getCurrentProvider().get()
     */
    public <T extends MCATData> T getData(Class<T> klass) {
        // Automatically load data if available
        if (data == null && dataProvider != null) {
            if (!dataProvider.isValid()) {
                throw new RuntimeException("Data provider is invalid!");
            }
            data = dataProvider.get();
        }
        return (T) data;
    }

    public void setData(MCATData data) {
        this.data = data;
    }

    /**
     * Sets the data to the result of getCurrentProvider().get()
     */
    public void resetFromCurrentProvider() {
        this.data = getCurrentProvider().get();
    }

    /**
     * Returns the currently selected data provider.
     * Please do not run getCurrentProvider().get() to get data from this slot. getData() automatically calls
     * the provider's method
     *
     * @return the currently selected data provider
     */
    public MCATDataProvider getCurrentProvider() {
        return dataProvider;
    }

    /**
     * Sets the data provider to the specified type.
     * Set to null to disable the data provider.
     */
    public void setCurrentProvider(MCATDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasDataOrIsProvidedData() {
        return hasData() || (getCurrentProvider() != null && getCurrentProvider().isValid());
    }

    public Path getStorageFilePath() {
        return storageFilePath;
    }

    public void setStorageFilePath(Path storageFilePath) {
        this.storageFilePath = storageFilePath;
    }

    /**
     * Stores the data to the storageFilePath
     */
    public void flush() {
        if (storageFilePath == null || fileName == null) {
            System.err.println("Skipping to flush() data slot " + name + " containing " + acceptedDataType + ": No storage location or file name defined!");
            return;
        }
        data.saveTo(storageFilePath, getFileName());
    }

    public String getName() {
        return name;
    }

    /**
     * Loads the provider from a JSON node
     *
     * @param jsonNode
     */
    public void fromJson(JsonNode jsonNode) {
        String providerId = jsonNode.get("current-provider-type-id").textValue();
        if (!StringUtils.isNullOrEmpty(providerId)) {
            Class<? extends MCATDataProvider> providerClass = MCATDataTypeRegistry.getInstance().getRegisteredDataProviders().get(providerId);
            try {
                Object provider = JsonUtils.getObjectMapper().readerFor(providerClass).readValue(jsonNode.get("current-provider"));
                setCurrentProvider((MCATDataProvider) provider);
            } catch (Exception e2) {
                e2.printStackTrace();
                try {
                    setCurrentProvider(providerClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Path getFileName() {
        return fileName;
    }

    public void setFileName(Path fileName) {
        this.fileName = fileName;
    }

    public static class Serializer extends JsonSerializer<MCATDataSlot> {
        @Override
        public void serialize(MCATDataSlot value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            if (value.getCurrentProvider() != null) {
                String providerId = MCATDataTypeRegistry.getInstance().getProviderId(value.getCurrentProvider().getClass());
                gen.writeStringField("current-provider-type-id", providerId);
                gen.writeObjectField("current-provider", value.getCurrentProvider());
            } else {
                gen.writeStringField("current-provider-type-id", "");
            }
            gen.writeEndObject();
        }
    }
}
