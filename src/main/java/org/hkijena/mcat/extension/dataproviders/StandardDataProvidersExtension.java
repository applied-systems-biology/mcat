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
package org.hkijena.mcat.extension.dataproviders;

import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.registries.MCATDataTypeRegistry;
import org.hkijena.mcat.extension.dataproviders.api.*;
import org.hkijena.mcat.extension.dataproviders.ui.ClusterCentersFromFileDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.DerivationMatrixFromFileDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.HyperstackFromTifDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.ROIFromFileDataProviderUI;
import org.hkijena.mcat.extension.datatypes.*;
import org.hkijena.mcat.ui.MCATDataProviderUI;
import org.hkijena.mcat.ui.registries.MCATDataProviderUIRegistry;

public class StandardDataProvidersExtension {
    private StandardDataProvidersExtension() {

    }

    public static void register() {
        registerDataProvider("cluster-abundance-from-file",
                ClusterAbundanceFromFileProvider.class,
                ClusterCentersFromFileDataProviderUI.class,
                ClusterAbundanceData.class);
        registerDataProvider("cluster-centers-from-file",
                ClusterCentersFromFileProvider.class,
                ClusterCentersFromFileDataProviderUI.class,
                ClusterCentersData.class);
        registerDataProvider("derivation-matrix-from-file",
                DerivativeMatrixFromFileProvider.class,
                DerivationMatrixFromFileDataProviderUI.class,
                DerivativeMatrixData.class);
        registerDataProvider("imageplus-hyperstack-from-file",
                HyperstackFromTifDataProvider.class,
                HyperstackFromTifDataProviderUI.class,
                HyperstackData.class);
        registerDataProvider("roi-from-file",
                ROIFromFileDataProvider.class,
                ROIFromFileDataProviderUI.class,
                ROIData.class);
    }

    private static void registerDataProvider(String id, Class<? extends MCATDataProvider> providerClass, Class<? extends MCATDataProviderUI> providerUIClass, Class<? extends MCATData> dataClass) {
        MCATDataTypeRegistry.getInstance().registerDataProvider(id, providerClass, dataClass);
        MCATDataProviderUIRegistry.getInstance().register(providerClass, providerUIClass);
    }
}
