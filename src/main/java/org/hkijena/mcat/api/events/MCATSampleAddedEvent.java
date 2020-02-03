package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATSample;

/**
 * Triggered when a sample is added to an {@link org.hkijena.mcat.api.MCATProject}
 */
public class MCATSampleAddedEvent {
    private MCATSample sample;

    public MCATSampleAddedEvent(MCATSample sample) {
        this.sample = sample;
    }

    public MCATSample getSample() {
        return sample;
    }
}
