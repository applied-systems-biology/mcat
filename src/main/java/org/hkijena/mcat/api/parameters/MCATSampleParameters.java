package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hkijena.mcat.api.MCATParameters;

/**
 * Contains sample parameters
 */
public class MCATSampleParameters extends MCATParameters {
    private String treatment;

    @JsonGetter("treatment")
    public String getTreatment() {
        return treatment;
    }

    @JsonSetter("treatment")
    public void setTreatment(String treatment) {
        this.treatment = treatment;
        postChangedEvent("treatment");
    }
}
