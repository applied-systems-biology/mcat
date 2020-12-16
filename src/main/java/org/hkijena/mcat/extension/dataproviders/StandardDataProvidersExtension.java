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
package org.hkijena.mcat.extension.dataproviders;

import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.registries.MCATDataTypeRegistry;
import org.hkijena.mcat.extension.dataproviders.api.ClusterAbundanceFromFileProvider;
import org.hkijena.mcat.extension.dataproviders.api.ClusterCentersFromFileProvider;
import org.hkijena.mcat.extension.dataproviders.api.DerivativeMatrixFromFileProvider;
import org.hkijena.mcat.extension.dataproviders.api.HyperstackFromTifDataProvider;
import org.hkijena.mcat.extension.dataproviders.api.ROIFromFileDataProvider;
import org.hkijena.mcat.extension.dataproviders.ui.ClusterCentersFromFileDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.DerivationMatrixFromFileDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.HyperstackFromTifDataProviderUI;
import org.hkijena.mcat.extension.dataproviders.ui.ROIFromFileDataProviderUI;
import org.hkijena.mcat.extension.datatypes.ClusterAbundanceData;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;
import org.hkijena.mcat.extension.datatypes.DerivativeMatrixData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;
import org.hkijena.mcat.extension.datatypes.ROIData;
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
