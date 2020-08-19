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

/**
 * Triggered when a parameter holder's parameters are changed
 */
public class ParameterChangedEvent {
    private Object source;
    private String key;

    /**
     * @param source event source
     * @param key    parameter key
     */
    public ParameterChangedEvent(Object source, String key) {
        this.source = source;
        this.key = key;
    }

    public Object getSource() {
        return source;
    }

    public String getKey() {
        return key;
    }
}
