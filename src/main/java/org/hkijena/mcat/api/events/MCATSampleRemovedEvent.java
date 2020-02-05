package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectSample;

/**
 * Triggered when a sample is removed from an {@link org.hkijena.mcat.api.MCATProject}
 */
public class MCATSampleRemovedEvent {
    private MCATProjectSample sample;

    public MCATSampleRemovedEvent(MCATProjectSample sample) {
        this.sample = sample;
    }

    public MCATProjectSample getSample() {
        return sample;
    }
}
