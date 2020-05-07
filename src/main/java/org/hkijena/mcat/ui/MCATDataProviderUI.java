package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATProjectDataSet;

import javax.swing.*;

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
