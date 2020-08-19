/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
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
