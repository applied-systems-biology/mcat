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
package org.hkijena.mcat.api.datainterfaces;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.HyperstackData;
import org.hkijena.mcat.extension.datatypes.ROIData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Organizes raw data
 */
@JsonSerialize(using = MCATPreprocessingInput.Serializer.class)
@JsonDeserialize(using = MCATPreprocessingInput.Deserializer.class)
public class MCATPreprocessingInput implements MCATDataInterface {

    private MCATDataSlot rawImage = new MCATDataSlot("raw-image", HyperstackData.class);
    private MCATDataSlot tissueROI = new MCATDataSlot("tissue-roi", ROIData.class);

    public MCATPreprocessingInput() {

    }

    public MCATPreprocessingInput(MCATPreprocessingInput other) {
        this.rawImage = new MCATDataSlot(other.rawImage);
        this.tissueROI = new MCATDataSlot(other.tissueROI);
    }

    public MCATDataSlot getRawImage() {
        return rawImage;
    }

    public MCATDataSlot getTissueROI() {
        return tissueROI;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(rawImage.getName(), rawImage);
        result.put(tissueROI.getName(), tissueROI);
        return result;
    }

    public static class Serializer extends JsonSerializer<MCATPreprocessingInput> {
        @Override
        public void serialize(MCATPreprocessingInput value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeObjectField("raw-image", value.rawImage);
            gen.writeObjectField("tissue-roi", value.tissueROI);
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends JsonDeserializer<MCATPreprocessingInput> {
        @Override
        public MCATPreprocessingInput deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            MCATPreprocessingInput preprocessingInput = new MCATPreprocessingInput();
            JsonNode node = p.readValueAsTree();
            preprocessingInput.getRawImage().fromJson(node.get("raw-image"));
            preprocessingInput.getTissueROI().fromJson(node.get("tissue-roi"));
            return preprocessingInput;
        }
    }
}
