package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATSample;

/**
 * Triggered when a sample is removed from an {@link org.hkijena.mcat.api.MCATProject}
 */
public class MCATSampleRemovedEvent {
    private MCATSample sample;

    public MCATSampleRemovedEvent(MCATSample sample) {
        this.sample = sample;
    }

    public MCATSample getSample() {
        return sample;
    }
}
