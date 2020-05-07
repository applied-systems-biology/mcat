package org.hkijena.mcat.extension.datatypes;

import ij.gui.Roi;
import ij.io.RoiEncoder;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.utils.api.ACAQDocumentation;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Contains a {@link Roi}
 */
@ACAQDocumentation(name = "ROI")
public class ROIData implements MCATData {

    private Roi roi;

    public ROIData(Roi roi) {
        this.setRoi(roi);
    }

    @Override
    public void saveTo(Path folder, String name, String identifier) {
        RoiEncoder re = new RoiEncoder(folder.resolve(identifier + name + ".roi").toString());
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
}
