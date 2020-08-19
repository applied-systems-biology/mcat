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

import ij.io.Opener;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.extension.datatypes.ROIData;

/**
 * Loads a {@link ROIData} from a file
 */
@MCATDocumentation(name = "ROI (*.roi, *.zip)")
public class ROIFromFileDataProvider extends FileDataProvider {

    public ROIFromFileDataProvider() {
        super();
    }

    public ROIFromFileDataProvider(ROIFromFileDataProvider other) {
        super(other);
    }

    @Override
    public ROIData get() {
        Opener opener = new Opener();
        return new ROIData(opener.openRoi(getFilePath().toString()), getFilePath().getFileName().toString());
    }
}
