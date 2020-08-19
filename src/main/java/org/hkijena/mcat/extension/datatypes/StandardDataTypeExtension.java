/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
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
