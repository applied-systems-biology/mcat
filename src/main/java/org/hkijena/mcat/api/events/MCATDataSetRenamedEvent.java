package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectDataSet;


/**
 * Triggered when a sample in an {@link org.hkijena.mcat.api.MCATProject} is renamed
 */
public class MCATDataSetRenamedEvent {
    private MCATProjectDataSet sample;

    public MCATDataSetRenamedEvent(MCATProjectDataSet sample) {
        this.sample = sample;
    }

    public MCATProjectDataSet getSample() {
        return sample;
    }
}
