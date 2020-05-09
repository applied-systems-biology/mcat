package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.events.ParameterChangedEvent;

/**
 * Contains sample parameters
 */
public class MCATSampleParameters implements MCATParameterCollection {
    private EventBus eventBus = new EventBus();
    private String treatment;

    public MCATSampleParameters() {
    }

    public MCATSampleParameters(MCATSampleParameters other) {
        this.treatment = other.treatment;
    }

    @MCATDocumentation(name = "Treatment")
    @MCATParameter("treatment")
    @JsonGetter("treatment")
    public String getTreatment() {
        return treatment;
    }

    @MCATParameter("treatment")
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
