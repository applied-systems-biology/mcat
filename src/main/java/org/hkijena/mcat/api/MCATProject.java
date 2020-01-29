package org.hkijena.mcat.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.eventbus.EventBus;

/**
 * An ACAQ5 project.
 * It contains all information to setup and run an analysis
 */
public class MCATProject {

    private EventBus eventBus = new EventBus();
    private BiMap<String, MCATSample> samples = HashBiMap.create();

    public MCATProject() {
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BiMap<String, MCATSample> getSamples() {
        return ImmutableBiMap.copyOf(samples);
    }
}
