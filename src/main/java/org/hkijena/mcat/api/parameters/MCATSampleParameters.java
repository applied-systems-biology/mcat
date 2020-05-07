package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.utils.api.ACAQDocumentation;
import org.hkijena.mcat.utils.api.events.ParameterChangedEvent;
import org.hkijena.mcat.utils.api.parameters.ACAQParameter;
import org.hkijena.mcat.utils.api.parameters.ACAQParameterCollection;

/**
 * Contains sample parameters
 */
public class MCATSampleParameters implements ACAQParameterCollection {
    private EventBus eventBus = new EventBus();
    private String treatment;

    public MCATSampleParameters() {
    }

    public MCATSampleParameters(MCATSampleParameters other) {
        this.treatment = other.treatment;
    }

    @ACAQDocumentation(name = "Treatment")
    @ACAQParameter("treatment")
    @JsonGetter("treatment")
    public String getTreatment() {
        return treatment;
    }

    @ACAQParameter("treatment")
    @JsonSetter("treatment")
    public void setTreatment(String treatment) {
        this.treatment = treatment;
        eventBus.post(new ParameterChangedEvent(this, "treatment"));
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
