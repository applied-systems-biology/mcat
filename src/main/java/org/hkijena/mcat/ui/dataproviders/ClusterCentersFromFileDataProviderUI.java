package org.hkijena.mcat.ui.dataproviders;

import org.hkijena.mcat.api.MCATSample;
import org.hkijena.mcat.api.dataproviders.ClusterCentersFromFileProvider;
import org.hkijena.mcat.api.dataproviders.HyperstackFromTifDataProvider;
import org.hkijena.mcat.ui.MCATDataProviderUI;
import org.hkijena.mcat.ui.components.FileSelection;

import java.awt.*;

public class ClusterCentersFromFileDataProviderUI extends MCATDataProviderUI<ClusterCentersFromFileProvider> {
    public ClusterCentersFromFileDataProviderUI(MCATSample sample, ClusterCentersFromFileProvider dataProvider) {
        super(sample, dataProvider);
        setLayout(new BorderLayout());

        FileSelection selection = new FileSelection(FileSelection.Mode.OPEN);
        selection.setPath(dataProvider.getFilePath());
        selection.addActionListener(e -> dataProvider.setFilePath(selection.getPath()));
        add(selection, BorderLayout.CENTER);
    }
}
