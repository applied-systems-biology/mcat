package org.hkijena.mcat.api;

import java.nio.file.Path;

/**
 * A slot holds data and also have the capability to load data from an MCATDataProvider if data is requested, but not set
 */
public class MCATDataSlot {

    private String name;
    private Class<? extends MCATData> acceptedDataType;
    private MCATData data;
    private MCATDataProvider dataProvider;

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
     * If not data is loaded, but an {@link MCATDataProvider} is set, the data is automatically set to the {@link MCATDataProvider} result.
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
     * Stores the data to the
     */
    public void flush(String identifier) {
        System.out.println("Saving data " + data + " to " + storageFilePath + " with identifier " + identifier);
        data.saveTo(storageFilePath, getName(), identifier);
    }

    public String getName() {
        return name;
    }
}
