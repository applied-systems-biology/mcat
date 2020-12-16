/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 * 
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 * 
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.extension.dataproviders.ui;

import java.awt.BorderLayout;

import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.extension.dataproviders.api.ROIFromFileDataProvider;
import org.hkijena.mcat.ui.MCATDataProviderUI;
import org.hkijena.mcat.ui.components.FileSelection;

/**
 * UI for {@link ROIFromFileDataProvider}
 */
public class ROIFromFileDataProviderUI extends MCATDataProviderUI {
    public ROIFromFileDataProviderUI(MCATProjectDataSet sample, ROIFromFileDataProvider dataProvider) {
        super(sample, dataProvider);
        setLayout(new BorderLayout());

        FileSelection selection = new FileSelection(FileSelection.IOMode.Open, FileSelection.PathMode.FilesOnly);
        selection.setPath(dataProvider.getFilePath());
        selection.addActionListener(e -> dataProvider.setFilePath(selection.getPath()));
        add(selection, BorderLayout.CENTER);
    }
}
