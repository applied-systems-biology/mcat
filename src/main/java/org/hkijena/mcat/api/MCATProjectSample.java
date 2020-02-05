package org.hkijena.mcat.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hkijena.mcat.api.datainterfaces.MCATClusteredDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;
import org.hkijena.mcat.ui.components.MonochromeColorIcon;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Manages one sample/subject
 */
public class MCATProjectSample implements Comparable<MCATProjectSample> {

    private MCATProject project;

    private MCATSampleParameters parameters = new MCATSampleParameters();

    private MCATRawDataInterface rawDataInterface = new MCATRawDataInterface();

    private MCATPreprocessedDataInterface preprocessedDataInterface = new MCATPreprocessedDataInterface();

    private MCATClusteredDataInterface clusteredDataInterface = new MCATClusteredDataInterface();

    private MCATPostprocessedDataInterface postprocessedDataInterface = new MCATPostprocessedDataInterface();

    public MCATProjectSample(MCATProject project) {
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
    public int compareTo(MCATProjectSample other) {
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

    public static class Serializer extends JsonSerializer<MCATProjectSample> {
        @Override
        public void serialize(MCATProjectSample sample, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeObject(sample.getParameters());
        }
    }

    public static class Deserializer extends JsonDeserializer<MCATProjectSample> {
        @Override
        public MCATProjectSample deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return null;
        }
    }
}
