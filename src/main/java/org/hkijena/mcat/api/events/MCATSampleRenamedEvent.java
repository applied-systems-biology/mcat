package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATSample;

public class MCATSampleRenamedEvent {
    private MCATSample sample;

    public MCATSampleRenamedEvent(MCATSample sample) {
        this.sample = sample;
    }

    public MCATSample getSample() {
        return sample;
    }
}
