package org.hkijena.mcat.api;

import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.events.MCATParameterChangedEvent;

/**
 * Base class for MCAT sample data classes
 */
public abstract class MCATParameters {
    private EventBus eventBus = new EventBus();

    public EventBus getEventBus() {
        return eventBus;
    }

    protected void postChangedEvent(String name) {
        eventBus.post(new MCATParameterChangedEvent(this, name));
    }
}
