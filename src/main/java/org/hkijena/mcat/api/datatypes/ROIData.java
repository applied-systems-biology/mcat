package org.hkijena.mcat.api.datatypes;

import ij.gui.Roi;
import ij.io.RoiEncoder;
import org.hkijena.mcat.api.MCATData;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Contains a {@link Roi}
 */
public class ROIData extends MCATData {

    private Roi roi;

    public ROIData(Roi roi) {
        this.setRoi(roi);
    }

    @Override
    public void saveTo(Path folder, String name, String identifier) {
        RoiEncoder re = new RoiEncoder(folder.resolve(name + identifier + ".roi").toString());
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
