package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectSample;


/**
 * Triggered when a sample in an {@link org.hkijena.mcat.api.MCATProject} is renamed
 */
public class MCATSampleRenamedEvent {
    private MCATProjectSample sample;

    public MCATSampleRenamedEvent(MCATProjectSample sample) {
        this.sample = sample;
    }

    public MCATProjectSample getSample() {
        return sample;
    }
}
