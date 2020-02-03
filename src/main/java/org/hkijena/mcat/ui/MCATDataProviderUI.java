package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATSample;

import javax.swing.*;

/**
 * Base class for any {@link MCATDataProvider} UI
 * @param <T>
 */
public abstract class MCATDataProviderUI <T extends MCATDataProvider<?>> extends JPanel {
    private MCATSample sample;
    private T dataProvider;

    protected MCATDataProviderUI(MCATSample sample, T dataProvider) {
        this.sample = sample;
        this.dataProvider = dataProvider;
    }

    public MCATSample getSample() {
        return sample;
    }

    public T getDataProvider() {
        return dataProvider;
    }
}
