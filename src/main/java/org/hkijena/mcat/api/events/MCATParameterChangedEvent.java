package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATParameters;

public class MCATParameterChangedEvent {
    private MCATParameters source;
    private String name;

    public MCATParameterChangedEvent(MCATParameters source, String name) {
        this.source = source;
        this.name = name;
    }

    public MCATParameters getSource() {
        return source;
    }

    public String getName() {
        return name;
    }
}
