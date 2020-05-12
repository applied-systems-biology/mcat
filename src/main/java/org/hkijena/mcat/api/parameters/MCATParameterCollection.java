package org.hkijena.mcat.api.parameters;

import com.google.common.eventbus.EventBus;

/**
 * Interfaced for a parameterized object
 */
public interface MCATParameterCollection {
    /**
     * Gets the event bus that posts events about the parameters
     *
     * @return The event bus triggering {@link org.hkijena.mcat.api.events.ParameterChangedEvent} and {@link org.hkijena.mcat.api.events.ParameterStructureChangedEvent}
     */
    EventBus getEventBus();
}
