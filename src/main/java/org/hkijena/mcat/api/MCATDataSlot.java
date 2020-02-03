package org.hkijena.mcat.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for a data slot.
 * A slot holds data and also have the capability to load data from an MCATDataProvider if data is requested, but not set
 * @param <T> data type held within this slow
 */
public abstract class MCATDataSlot<T extends MCATData> {

    private Class<T> acceptedDataType;
    private T data;
    private Map<Class<? extends MCATDataProvider<T>> ,MCATDataProvider<T>> availableProviders = new HashMap<>();
    private MCATDataProvider<T> dataProvider;

    public MCATDataSlot(Class<T> acceptedDataType, MCATDataProvider<T>... dataProviders) {
        this.acceptedDataType = acceptedDataType;
        for(MCATDataProvider<T> provider : dataProviders) {
            availableProviders.put((Class<? extends MCATDataProvider<T>>) provider.getClass(), provider);
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
        return (U)availableProviders.get(klass);
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
            dataProvider = availableProviders.values().iterator().next();
    }

    public Map<Class<? extends MCATDataProvider<T>>, MCATDataProvider<T>> getAvailableProviders() {
        return Collections.unmodifiableMap(availableProviders);
    }
}
