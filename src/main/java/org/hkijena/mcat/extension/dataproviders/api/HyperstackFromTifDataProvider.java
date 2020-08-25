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
package org.hkijena.mcat.extension.dataproviders.api;

import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

import ij.IJ;

/**
 * Loads a {@link HyperstackData} from a file
 */
@MCATDocumentation(name = "Hyperstack (*.tif)")
public class HyperstackFromTifDataProvider extends FileDataProvider {

    public HyperstackFromTifDataProvider() {
        super();
    }

    public HyperstackFromTifDataProvider(HyperstackFromTifDataProvider other) {
        super(other);
    }

    @Override
    public HyperstackData get() {
        return new HyperstackData(IJ.openImage(getFilePath().toString()));
    }

}
