package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectDataSet;

/**
 * Triggered when a sample is added to an {@link org.hkijena.mcat.api.MCATProject}
 */
public class MCATDataSetAddedEvent {
    private MCATProjectDataSet sample;

    public MCATDataSetAddedEvent(MCATProjectDataSet sample) {
        this.sample = sample;
    }

    public MCATProjectDataSet getSample() {
        return sample;
    }
}
