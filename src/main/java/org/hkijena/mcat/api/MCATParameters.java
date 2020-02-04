package org.hkijena.mcat.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.events.MCATPropertyChangedEvent;

/**
 * Base class for MCAT sample data classes
 */
public abstract class MCATParameters {
    private EventBus eventBus = new EventBus();

    public EventBus getEventBus() {
        return eventBus;
    }

    protected void postChangedEvent(String name) {
        eventBus.post(new MCATPropertyChangedEvent(this, name));
    }
}
