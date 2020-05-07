package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATProjectSample;

import javax.swing.*;

/**
 * Base class for any {@link MCATDataProvider} UI
 */
public abstract class MCATDataProviderUI extends JPanel {
    private MCATProjectSample sample;
    private MCATDataProvider dataProvider;

    protected MCATDataProviderUI(MCATProjectSample sample, MCATDataProvider dataProvider) {
        this.sample = sample;
        this.dataProvider = dataProvider;
    }

    public MCATProjectSample getSample() {
        return sample;
    }

    public MCATDataProvider getDataProvider() {
        return dataProvider;
    }
}
