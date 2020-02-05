package org.hkijena.mcat.ui.dataproviders;

import org.hkijena.mcat.api.MCATProjectSample;
import org.hkijena.mcat.api.dataproviders.DerivationMatrixFromFileProvider;
import org.hkijena.mcat.ui.MCATDataProviderUI;
import org.hkijena.mcat.ui.components.FileSelection;

import java.awt.*;

/**
 * UI for {@link DerivationMatrixFromFileProvider}
 */
public class DerivationMatrixFromFileDataProviderUI extends MCATDataProviderUI<DerivationMatrixFromFileProvider> {
    public DerivationMatrixFromFileDataProviderUI(MCATProjectSample sample, DerivationMatrixFromFileProvider dataProvider) {
        super(sample, dataProvider);
        setLayout(new BorderLayout());

        FileSelection selection = new FileSelection(FileSelection.Mode.OPEN);
        selection.setPath(dataProvider.getFilePath());
        selection.addActionListener(e -> dataProvider.setFilePath(selection.getPath()));
        add(selection, BorderLayout.CENTER);
    }
}
