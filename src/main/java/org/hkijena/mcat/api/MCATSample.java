package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datainterfaces.MCATClusteredDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;
import org.hkijena.mcat.ui.components.MonochromeColorIcon;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Manages one sample/subject
 */
public class MCATSample implements Comparable<MCATSample> {

    private MCATProject project;

    private MCATSampleParameters parameters = new MCATSampleParameters();

    private MCATRawDataInterface rawDataInterface = new MCATRawDataInterface();

    private MCATPreprocessedDataInterface preprocessedDataInterface = new MCATPreprocessedDataInterface();

    private MCATClusteredDataInterface clusteredDataInterface = new MCATClusteredDataInterface();

    private MCATPostprocessedDataInterface postprocessedDataInterface = new MCATPostprocessedDataInterface();

    public MCATSample(MCATProject project) {
        this.project = project;
    }

    public MCATProject getProject() {
        return project;
    }

    public MCATSampleParameters getParameters() {
        return parameters;
    }

    public String getName() {
        return  getProject().getSamples().inverse().get(this);
    }

    public Color getTreatmentColor() {
       return UIUtils.stringToColor(getParameters().getTreatment(), 0.8f, 0.8f);
    }

    public Icon getIcon() {
        return new MonochromeColorIcon(UIUtils.getIconFromResources("sample-template.png"), getTreatmentColor());
    }

    @Override
    public int compareTo(MCATSample other) {
        return getName().compareTo(other.getName());
    }

    public MCATRawDataInterface getRawDataInterface() {
        return rawDataInterface;
    }

    public MCATPreprocessedDataInterface getPreprocessedDataInterface() {
        return preprocessedDataInterface;
    }

    public MCATClusteredDataInterface getClusteredDataInterface() {
        return clusteredDataInterface;
    }

    public MCATPostprocessedDataInterface getPostprocessedDataInterface() {
        return postprocessedDataInterface;
    }
}
