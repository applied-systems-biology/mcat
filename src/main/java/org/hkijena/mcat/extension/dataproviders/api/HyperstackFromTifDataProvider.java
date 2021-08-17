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
package org.hkijena.mcat.extension.dataproviders.api;

import ij.IJ;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

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
        System.out.println("Loading image from " + getFilePath());
        return new HyperstackData(IJ.openImage(getFilePath().toString()));
    }

}
