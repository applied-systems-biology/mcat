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
package org.hkijena.mcat.api.events;

import org.hkijena.mcat.api.MCATProjectDataSet;


/**
 * Triggered when a sample in an {@link org.hkijena.mcat.api.MCATProject} is renamed
 */
public class DataSetRenamedEvent {
    private MCATProjectDataSet sample;

    public DataSetRenamedEvent(MCATProjectDataSet sample) {
        this.sample = sample;
    }

    public MCATProjectDataSet getSample() {
        return sample;
    }
}
