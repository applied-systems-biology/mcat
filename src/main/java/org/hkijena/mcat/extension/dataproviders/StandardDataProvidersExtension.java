package org.hkijena.mcat.extension.dataproviders;

import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.registries.MCATDataTypeRegistry;
import org.hkijena.mcat.extension.datatypes.*;

public class StandardDataProvidersExtension {
    private StandardDataProvidersExtension() {

    }

    public static void register() {
        registerDataProvider("cluster-abundance-from-file", ClusterAbundanceFromFileProvider.class, ClusterAbundanceData.class);
        registerDataProvider("cluster-centers-from-file", ClusterCentersFromFileProvider.class, ClusterCentersData.class);
        registerDataProvider("derivation-matrix-from-file", DerivativeMatrixFromFileProvider.class, DerivativeMatrixData.class);
        registerDataProvider("imageplus-hyperstack-from-file", HyperstackFromTifDataProvider.class, HyperstackData.class);
        registerDataProvider("roi-from-file", ROIFromFileDataProvider.class, ROIData.class);
    }

    private static void registerDataProvider(String id, Class<? extends MCATDataProvider> providerClass, Class<? extends MCATData> dataClass) {
        MCATDataTypeRegistry.getInstance().registerDataProvider(id, providerClass, dataClass);
    }
}
