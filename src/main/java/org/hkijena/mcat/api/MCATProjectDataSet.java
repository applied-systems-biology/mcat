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
package org.hkijena.mcat.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessingInput;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;
import org.hkijena.mcat.ui.components.MonochromeColorIcon;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Manages one sample/subject
 */
public class MCATProjectDataSet implements Comparable<MCATProjectDataSet> {

    private MCATProject project;

    private MCATSampleParameters parameters = new MCATSampleParameters();

    private MCATPreprocessingInput rawDataInterface = new MCATPreprocessingInput();

    public MCATProjectDataSet() {

    }

    public MCATProjectDataSet(MCATProject project) {
        this.project = project;
    }

    public MCATProject getProject() {
        return project;
    }

    public void setProject(MCATProject project) {
        this.project = project;
    }

    @JsonGetter("parameters")
    public MCATSampleParameters getParameters() {
        return parameters;
    }

    @JsonSetter("parameters")
    public void setParameters(MCATSampleParameters parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return getProject().getDataSets().inverse().get(this);
    }

    public Color getTreatmentColor() {
        return UIUtils.stringToColor(getParameters().getTreatment(), 0.8f, 0.8f);
    }

    public Icon getIcon() {
        return new MonochromeColorIcon(UIUtils.getIconFromResources("sample-template.png"), getTreatmentColor());
    }

    @Override
    public int compareTo(MCATProjectDataSet other) {
        return getName().compareTo(other.getName());
    }

    @JsonGetter("data")
    public MCATPreprocessingInput getRawDataInterface() {
        return rawDataInterface;
    }

    @JsonSetter("data")
    public void setRawDataInterface(MCATPreprocessingInput rawDataInterface) {
        this.rawDataInterface = rawDataInterface;
    }
}
