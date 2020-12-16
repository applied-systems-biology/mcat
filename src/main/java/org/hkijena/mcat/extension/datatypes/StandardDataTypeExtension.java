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
package org.hkijena.mcat.extension.datatypes;

import org.hkijena.mcat.api.MCATData;
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
        registerDataType("auc", AUCData.class);
        registerDataType("auc-plot", AUCPlotData.class);
        registerDataType("time-derivative-plot", TimeDerivativePlotData.class);
    }

    private static void registerDataType(String id, Class<? extends MCATData> klass) {
        MCATDataTypeRegistry.getInstance().registerDataType(id, klass);
    }
}
