package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectDataSet;

/**
 * Triggered when a sample is removed from an {@link org.hkijena.mcat.api.MCATProject}
 */
public class MCATDataSetRemovedEvent {
    private MCATProjectDataSet sample;

    public MCATDataSetRemovedEvent(MCATProjectDataSet sample) {
        this.sample = sample;
    }

    public MCATProjectDataSet getSample() {
        return sample;
    }
}
