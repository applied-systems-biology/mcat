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
        
        if(!isValid())
        	return null;
        
        return new ROIData(opener.openRoi(getFilePath().toString()), getFilePath().getFileName().toString());
    }
}
