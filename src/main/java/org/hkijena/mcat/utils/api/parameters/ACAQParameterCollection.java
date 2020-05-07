package org.hkijena.mcat.utils.api.parameters;

import com.google.common.eventbus.EventBus;

/**
 * Interfaced for a parameterized object
 */
public interface ACAQParameterCollection {
    /**
     * Gets the event bus that posts events about the parameters
     *
     * @return The event bus triggering {@link events.ParameterChangedEvent} and {@link events.ParameterStructureChangedEvent}
     */
    EventBus getEventBus();
}
