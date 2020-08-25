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
