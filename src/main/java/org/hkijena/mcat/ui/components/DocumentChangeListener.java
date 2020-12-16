/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 * 
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 * 
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.ui.components;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Merges {@link DocumentListener} events into one "changed" event
 */
public abstract class DocumentChangeListener implements DocumentListener {

    public abstract void changed(DocumentEvent documentEvent);

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        changed(documentEvent);
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        changed(documentEvent);
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        changed(documentEvent);
    }
}
