package org.hkijena.mcat.extension.dataproviders.ui;

import org.hkijena.mcat.api.MCATProjectSample;
import org.hkijena.mcat.extension.dataproviders.api.HyperstackFromTifDataProvider;
import org.hkijena.mcat.ui.MCATDataProviderUI;
import org.hkijena.mcat.ui.components.FileSelection;

import java.awt.*;

/**
 * UI for {@link HyperstackFromTifDataProvider}
 */
public class HyperstackFromTifDataProviderUI extends MCATDataProviderUI {
    public HyperstackFromTifDataProviderUI(MCATProjectSample sample, HyperstackFromTifDataProvider dataProvider) {
        super(sample, dataProvider);
        setLayout(new BorderLayout());

        FileSelection selection = new FileSelection(FileSelection.IOMode.Open, FileSelection.PathMode.FilesOnly);
        selection.setPath(dataProvider.getFilePath());
        selection.addActionListener(e -> dataProvider.setFilePath(selection.getPath()));
        add(selection, BorderLayout.CENTER);
    }
}
