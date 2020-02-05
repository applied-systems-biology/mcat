package org.hkijena.mcat.ui.registries;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATProjectSample;
import org.hkijena.mcat.api.dataproviders.ClusterCentersFromFileProvider;
import org.hkijena.mcat.api.dataproviders.DerivationMatrixFromFileProvider;
import org.hkijena.mcat.api.dataproviders.HyperstackFromTifDataProvider;
import org.hkijena.mcat.api.dataproviders.ROIFromFileDataProvider;
import org.hkijena.mcat.ui.MCATDataProviderUI;
import org.hkijena.mcat.ui.dataproviders.ClusterCentersFromFileDataProviderUI;
import org.hkijena.mcat.ui.dataproviders.DerivationMatrixFromFileDataProviderUI;
import org.hkijena.mcat.ui.dataproviders.HyperstackFromTifDataProviderUI;
import org.hkijena.mcat.ui.dataproviders.ROIFromFileDataProviderUI;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains definitions that link a {@link MCATDataProvider} to its respective UI class
 */
public class MCATDataProviderUIRegistry {

    private static MCATDataProviderUIRegistry instance;
    private Map<Class<? extends MCATDataProvider<?>>, Class<? extends MCATDataProviderUI<?>>> registry = new HashMap<>();

    private MCATDataProviderUIRegistry() {
        // Register here
        registry.put(HyperstackFromTifDataProvider.class, HyperstackFromTifDataProviderUI.class);
        registry.put(ROIFromFileDataProvider.class, ROIFromFileDataProviderUI.class);
        registry.put(DerivationMatrixFromFileProvider.class, DerivationMatrixFromFileDataProviderUI.class);
        registry.put(ClusterCentersFromFileProvider.class, ClusterCentersFromFileDataProviderUI.class);
    }

    public <T extends MCATDataProviderUI<?>> T getUIFor(MCATProjectSample sample, MCATDataProvider<?> provider) {
        Class<? extends MCATDataProviderUI<?>> uiClass = registry.get(provider.getClass());
        try {
            return (T)uiClass.getConstructor(MCATProjectSample.class, provider.getClass()).newInstance(sample, provider);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static MCATDataProviderUIRegistry getInstance() {
        if(instance == null)
            instance = new MCATDataProviderUIRegistry();
        return instance;
    }
}
