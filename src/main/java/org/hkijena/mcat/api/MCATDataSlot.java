package org.hkijena.mcat.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Base class for a data slot.
 * A slot holds data and also have the capability to load data from an MCATDataProvider if data is requested, but not set
 * @param <T> data type held within this slow
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public abstract class MCATDataSlot<T extends MCATData> {

    private String name;
    private Class<T> acceptedDataType;
    private T data;
    private List<MCATDataProvider<T>> availableProviders = new ArrayList<>();
    private MCATDataProvider<T> dataProvider;

    /**
     * The path where the slot stores its data
     */
    private Path storageFilePath;

    public MCATDataSlot(String name, Class<T> acceptedDataType, MCATDataProvider<T>... dataProviders) {
        this.name = name;
        this.acceptedDataType = acceptedDataType;
        availableProviders.addAll(Arrays.asList(dataProviders));
    }

    public MCATDataSlot(MCATDataSlot<T> other) {
        this.name = other.name;
        this.acceptedDataType = other.acceptedDataType;
        for(MCATDataProvider<T> provider : other.availableProviders) {
            MCATDataProvider<T> copy = MCATDataProvider.duplicate(provider);
            this.availableProviders.add(copy);
            if(other.dataProvider == provider) {
                this.dataProvider = copy;
            }
        }
    }

    public Class<T> getAcceptedDataType() {
        return acceptedDataType;
    }

    public T getData() {
        // Automatically load data if available
        if(data == null && dataProvider != null)
            data = dataProvider.get();
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * Gets the matching data provider
     * @param klass
     * @param <U>
     * @return
     */
    public <U extends MCATDataProvider<T>> U getProvider(Class<? extends U> klass) {
        return (U)availableProviders.stream().filter(k -> klass.isAssignableFrom(k.getClass())).findFirst().orElse(null);
    }

    public MCATDataProvider<T> getCurrentProvider() {
        return dataProvider;
    }

    /**
     * Sets the data provider to the specified type.
     * Set to null to disable the data provider.
     * @param klass
     * @param <U>
     */
    public <U extends MCATDataProvider<T>> void setCurrentProvider(Class<? extends U> klass) {
        if(klass != null)
            dataProvider = getProvider(klass);
        else
            dataProvider = null;
    }

    /**
     * Ensures that a data provider is assigned
     */
    public void ensureDataProvider() {
        if(dataProvider == null)
            dataProvider = availableProviders.get(0);
    }

    public List<MCATDataProvider<T>> getAvailableProviders() {
        return Collections.unmodifiableList(availableProviders);
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasDataOrIsProvidedData() {
        return hasData() || (getCurrentProvider() != null && getCurrentProvider().providesData());
    }

    public Path getStorageFilePath() {
        return storageFilePath;
    }

    public void setStorageFilePath(Path storageFilePath) {
        this.storageFilePath = storageFilePath;
    }

    /**
     * Stores the data to the
     */
    public void flush(String identifier) {
        data.saveTo(storageFilePath, getName(), identifier);
    }

    public String getName() {
        return name;
    }

    public static class Serializer extends JsonSerializer<MCATDataSlot<?>> {
        @Override
        public void serialize(MCATDataSlot<?> dataSlot, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        }
    }
}
