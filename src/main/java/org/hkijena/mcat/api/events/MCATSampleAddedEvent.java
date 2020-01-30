package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATSample;

public class MCATSampleAddedEvent {
    private MCATSample sample;

    public MCATSampleAddedEvent(MCATSample sample) {
        this.sample = sample;
    }

    public MCATSample getSample() {
        return sample;
    }
}
