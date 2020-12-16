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
package org.hkijena.mcat.ui;

import javax.swing.JPanel;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATProjectDataSet;

/**
 * Base class for any {@link MCATDataProvider} UI
 */
public abstract class MCATDataProviderUI extends JPanel {
    private MCATProjectDataSet sample;
    private MCATDataProvider dataProvider;

    protected MCATDataProviderUI(MCATProjectDataSet sample, MCATDataProvider dataProvider) {
        this.sample = sample;
        this.dataProvider = dataProvider;
    }

    public MCATProjectDataSet getSample() {
        return sample;
    }

    public MCATDataProvider getDataProvider() {
        return dataProvider;
    }
}
