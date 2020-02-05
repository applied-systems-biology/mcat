package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATProjectSample;

import javax.swing.*;

/**
 * Base class for any {@link MCATDataProvider} UI
 * @param <T>
 */
public abstract class MCATDataProviderUI <T extends MCATDataProvider<?>> extends JPanel {
    private MCATProjectSample sample;
    private T dataProvider;

    protected MCATDataProviderUI(MCATProjectSample sample, T dataProvider) {
        this.sample = sample;
        this.dataProvider = dataProvider;
    }

    public MCATProjectSample getSample() {
        return sample;
    }

    public T getDataProvider() {
        return dataProvider;
    }
}
