package org.hkijena.mcat.ui.dataproviders;

import org.hkijena.mcat.api.MCATProjectSample;
import org.hkijena.mcat.api.dataproviders.DerivativeMatrixFromFileProvider;
import org.hkijena.mcat.ui.MCATDataProviderUI;
import org.hkijena.mcat.ui.components.FileSelection;

import java.awt.*;

/**
 * UI for {@link DerivativeMatrixFromFileProvider}
 */
public class DerivationMatrixFromFileDataProviderUI extends MCATDataProviderUI<DerivativeMatrixFromFileProvider> {
    public DerivationMatrixFromFileDataProviderUI(MCATProjectSample sample, DerivativeMatrixFromFileProvider dataProvider) {
        super(sample, dataProvider);
        setLayout(new BorderLayout());

        FileSelection selection = new FileSelection(FileSelection.IOMode.Open, FileSelection.PathMode.FilesOnly);
        selection.setPath(dataProvider.getFilePath());
        selection.addActionListener(e -> dataProvider.setFilePath(selection.getPath()));
        add(selection, BorderLayout.CENTER);
    }
}
