package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATParameters;

/**
 * Event triggered when a {@link MCATParameters} property is changed
 */
public class MCATPropertyChangedEvent {
    private MCATParameters source;
    private String name;

    public MCATPropertyChangedEvent(MCATParameters source, String name) {
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