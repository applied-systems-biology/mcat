package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectSample;

/**
 * Triggered when a sample is added to an {@link org.hkijena.mcat.api.MCATProject}
 */
public class MCATSampleAddedEvent {
    private MCATProjectSample sample;

    public MCATSampleAddedEvent(MCATProjectSample sample) {
        this.sample = sample;
    }

    public MCATProjectSample getSample() {
        return sample;
    }
}
