package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATSample;


/**
 * Triggered when a sample in an {@link org.hkijena.mcat.api.MCATProject} is renamed
 */
public class MCATSampleRenamedEvent {
    private MCATSample sample;

    public MCATSampleRenamedEvent(MCATSample sample) {
        this.sample = sample;
    }

    public MCATSample getSample() {
        return sample;
    }
}
