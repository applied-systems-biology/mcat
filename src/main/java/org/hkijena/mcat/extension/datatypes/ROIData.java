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

import java.io.IOException;
import java.nio.file.Path;

import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDocumentation;

import ij.gui.Roi;
import ij.io.RoiEncoder;

/**
 * Contains a {@link Roi}
 */
@MCATDocumentation(name = "ROI")
public class ROIData implements MCATData {

    private Roi roi;
    private String name;

    public ROIData(Roi roi, String name) {
        this.name = name;
        this.setRoi(roi);
    }

    @Override
    public void saveTo(Path folder, Path fileName) {
        RoiEncoder re = new RoiEncoder(folder.resolve(fileName).toString());
        try {
            re.write(getRoi());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Roi getRoi() {
        return roi;
    }

    public void setRoi(Roi roi) {
        this.roi = roi;
    }

    public String getName() {
        return name;
    }
}
