package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATSample;

public class MCATSampleRemovedEvent {
    private MCATSample sample;

    public MCATSampleRemovedEvent(MCATSample sample) {
        this.sample = sample;
    }

    public MCATSample getSample() {
        return sample;
    }
}
