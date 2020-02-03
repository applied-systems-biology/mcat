package org.hkijena.mcat.api.parameters;

import org.hkijena.mcat.api.MCATParameters;

/**
 * Contains sample parameters
 */
public class MCATSampleParameters extends MCATParameters {
    private String treatment;

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
        postChangedEvent("treatment");
    }
}
