package org.hkijena.mcat.extension.datatypes;

import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.registries.MCATDataTypeRegistry;

public class StandardDataTypeExtension {
    private StandardDataTypeExtension() {

    }

    public static void register() {
        registerDataType("cluster-abundance", ClusterAbundanceData.class);
        registerDataType("cluster-centers", ClusterCentersData.class);
        registerDataType("derivative-matrix", DerivativeMatrixData.class);
        registerDataType("imageplus-hyperstack", HyperstackData.class);
        registerDataType("roi", ROIData.class);
    }

    private static void registerDataType(String id, Class<? extends MCATData> klass) {
        MCATDataTypeRegistry.getInstance().registerDataType(id, klass);
    }
}
