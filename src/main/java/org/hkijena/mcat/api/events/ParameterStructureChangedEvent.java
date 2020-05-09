package org.hkijena.mcat.api.events;


import org.hkijena.mcat.api.parameters.MCATParameterCollection;

/**
 * Triggered by an {@link MCATParameterCollection} if the list of available parameters is changed
 */
public class ParameterStructureChangedEvent {
    private MCATParameterCollection source;

    /**
     * @param source event source
     */
    public ParameterStructureChangedEvent(MCATParameterCollection source) {
        this.source = source;
    }

    public MCATParameterCollection getSource() {
        return source;
    }
}
