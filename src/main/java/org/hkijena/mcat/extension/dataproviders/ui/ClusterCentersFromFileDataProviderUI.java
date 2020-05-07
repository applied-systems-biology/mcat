package org.hkijena.mcat.extension.dataproviders.ui;

import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.extension.dataproviders.api.ClusterCentersFromFileProvider;
import org.hkijena.mcat.ui.MCATDataProviderUI;
import org.hkijena.mcat.ui.components.FileSelection;

import java.awt.*;

/**
 * UI for {@link ClusterCentersFromFileProvider}
 */
public class ClusterCentersFromFileDataProviderUI extends MCATDataProviderUI {
    public ClusterCentersFromFileDataProviderUI(MCATProjectDataSet sample, ClusterCentersFromFileProvider dataProvider) {
        super(sample, dataProvider);
        setLayout(new BorderLayout());

        FileSelection selection = new FileSelection(FileSelection.IOMode.Open, FileSelection.PathMode.FilesOnly);
        selection.setPath(dataProvider.getFilePath());
        selection.addActionListener(e -> dataProvider.setFilePath(selection.getPath()));
        add(selection, BorderLayout.CENTER);
    }
}
