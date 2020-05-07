package org.hkijena.mcat.api.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.extension.dataproviders.StandardDataProvidersExtension;
import org.hkijena.mcat.extension.datatypes.StandardDataTypeExtension;

import java.util.*;

/**
 * Manages {@link org.hkijena.mcat.api.MCATDataSlot} and {@link org.hkijena.mcat.api.MCATDataProvider} types
 */
public class MCATDataTypeRegistry {
    private static MCATDataTypeRegistry instance;
    private BiMap<String, Class<? extends MCATData>> registeredDataTypes = HashBiMap.create();
    private BiMap<String, Class<? extends MCATDataProvider>> registeredDataProviders = HashBiMap.create();
    private Map<Class<? extends MCATData>, Set<Class<? extends MCATDataProvider>>> providersForData = new HashMap<>();

    private MCATDataTypeRegistry() {

    }

    /**
     * Registers a data type
     *
     * @param id    unique ID
     * @param klass data class
     */
    public void registerDataType(String id, Class<? extends MCATData> klass) {
        registeredDataTypes.put(id, klass);
    }

    /**
     * Registers a data provider
     *
     * @param id            unique id
     * @param providerClass provider class
     * @param dataClass     data that is generated
     */
    public void registerDataProvider(String id, Class<? extends MCATDataProvider> providerClass, Class<? extends MCATData> dataClass) {
        registeredDataProviders.put(id, providerClass);
        Set<Class<? extends MCATDataProvider>> forData = providersForData.getOrDefault(dataClass, null);
        if (forData == null) {
            forData = new HashSet<>();
            providersForData.put(dataClass, forData);
        }
        forData.add(providerClass);
    }

    /**
     * Returns providers for the specified data class
     *
     * @param dataClass the data class
     * @return providers for the specified data class. Always return a non-null list.
     */
    public Set<Class<? extends MCATDataProvider>> getProvidersFor(Class<? extends MCATData> dataClass) {
        return providersForData.getOrDefault(dataClass, Collections.emptySet());
    }

    public static MCATDataTypeRegistry getInstance() {
        if (instance == null) {
            instance = new MCATDataTypeRegistry();
            StandardDataTypeExtension.register();
            StandardDataProvidersExtension.register();
        }
        return instance;
    }
}
