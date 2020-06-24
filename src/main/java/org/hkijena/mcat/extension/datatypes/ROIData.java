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
