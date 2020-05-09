package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectDataSet;


/**
 * Triggered when a sample in an {@link org.hkijena.mcat.api.MCATProject} is renamed
 */
public class DataSetRenamedEvent {
    private MCATProjectDataSet sample;

    public DataSetRenamedEvent(MCATProjectDataSet sample) {
        this.sample = sample;
    }

    public MCATProjectDataSet getSample() {
        return sample;
    }
}
