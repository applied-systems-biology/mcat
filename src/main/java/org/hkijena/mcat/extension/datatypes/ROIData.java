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

import ij.gui.Roi;
import ij.io.RoiEncoder;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDocumentation;

import java.io.IOException;
import java.nio.file.Path;

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
