package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectDataSet;

/**
 * Triggered when a sample is added to an {@link org.hkijena.mcat.api.MCATProject}
 */
public class DataSetAddedEvent {
    private MCATProjectDataSet sample;

    public DataSetAddedEvent(MCATProjectDataSet sample) {
        this.sample = sample;
    }

    public MCATProjectDataSet getSample() {
        return sample;
    }
}
