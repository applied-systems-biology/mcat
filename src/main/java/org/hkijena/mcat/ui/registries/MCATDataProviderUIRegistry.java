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
package org.hkijena.mcat.ui.registries;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.api.registries.MCATDataTypeRegistry;
import org.hkijena.mcat.extension.dataproviders.api.ClusterCentersFromFileProvider;
import org.hkijena.mcat.extension.dataproviders.api.DerivativeMatrixFromFileProvider;
import org.hkijena.mcat.extension.dataproviders.api.HyperstackFromTifDataProvider;
import org.hkijena.mcat.extension.dataproviders.api.ROIFromFileDataProvider;
import org.hkijena.mcat.extension.dataproviders.ui.ClusterCentersFromFileDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.DerivationMatrixFromFileDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.HyperstackFromTifDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.ROIFromFileDataProviderUI;
import org.hkijena.mcat.ui.MCATDataProviderUI;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains definitions that link a {@link MCATDataProvider} to its respective UI class
 */
public class MCATDataProviderUIRegistry {

    private static MCATDataProviderUIRegistry instance;
    private Map<Class<? extends MCATDataProvider>, Class<? extends MCATDataProviderUI>> registry = new HashMap<>();

    private MCATDataProviderUIRegistry() {
        // Register here
        registry.put(HyperstackFromTifDataProvider.class, HyperstackFromTifDataProviderUI.class);
        registry.put(ROIFromFileDataProvider.class, ROIFromFileDataProviderUI.class);
        registry.put(DerivativeMatrixFromFileProvider.class, DerivationMatrixFromFileDataProviderUI.class);
        registry.put(ClusterCentersFromFileProvider.class, ClusterCentersFromFileDataProviderUI.class);
    }

    public <T extends MCATDataProviderUI> T getUIFor(MCATProjectDataSet sample, MCATDataProvider provider) {
        Class<? extends MCATDataProviderUI> uiClass = registry.get(provider.getClass());
        try {
            return (T) uiClass.getConstructor(MCATProjectDataSet.class, provider.getClass()).newInstance(sample, provider);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Class<? extends MCATDataProvider> providerClass, Class<? extends MCATDataProviderUI> providerUIClass) {
        registry.put(providerClass, providerUIClass);
    }

    public static MCATDataProviderUIRegistry getInstance() {
        if (instance == null) {
            instance = new MCATDataProviderUIRegistry();
            MCATDataTypeRegistry.getInstance();
        }
        return instance;
    }
}
