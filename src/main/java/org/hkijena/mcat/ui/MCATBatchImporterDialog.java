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
package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATBatchImporter;
import org.hkijena.mcat.ui.components.DocumentChangeListener;
import org.hkijena.mcat.ui.components.FileSelection;
import org.hkijena.mcat.ui.components.FormPanel;
import org.hkijena.mcat.ui.components.MarkdownDocument;
import org.hkijena.mcat.utils.StringUtils;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * UI for {@link MCATBatchImporter}
 */
public class MCATBatchImporterDialog extends JDialog {
    private MCATWorkbenchUI workbenchUI;
    private MCATBatchImporter batchImporter;

    public MCATBatchImporterDialog(MCATWorkbenchUI workbenchUI) {
        this.workbenchUI = workbenchUI;
        this.batchImporter = new MCATBatchImporter(workbenchUI.getProject());
        initialize();
    }

    private void initialize() {
        setSize(1200, 800);
        getContentPane().setLayout(new BorderLayout(8, 8));
        setTitle("Batch import samples");
        setIconImage(UIUtils.getIconFromResources("mcat.png").getImage());

        initializeSettings();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        buttonPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);

        JButton addButton = new JButton("Import");
        addButton.addActionListener(e -> runImport());
        buttonPanel.add(addButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addToggleForPattern(FormPanel formPanel, String name,
                                     Supplier<Boolean> toggleGetter, Consumer<Boolean> toggleSetter,
                                     Supplier<String> patternGetter, Consumer<String> patternSetter,
                                     MarkdownDocument document) {
        JCheckBox toggle = formPanel.addToForm(new JCheckBox("Import " + name, toggleGetter.get()),
                document);
        toggle.addActionListener(e -> toggleSetter.accept(toggle.isSelected()));
        JTextField textField = formPanel.addToForm(new JTextField(patternGetter.get()),
                new JLabel(StringUtils.capitalizeFirstLetter(name) + " pattern"),
                document);
        textField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textField.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void changed(DocumentEvent documentEvent) {
                patternSetter.accept(textField.getText());
            }
        });
    }

    private void initializeSettings() {
        FormPanel formPanel = new FormPanel(MarkdownDocument.fromPluginResource("documentation/batch_importer_default.md"),
                FormPanel.WITH_DOCUMENTATION | FormPanel.WITH_SCROLLING);

        FileSelection fileSelection = formPanel.addToForm(new FileSelection(FileSelection.IOMode.Open, FileSelection.PathMode.DirectoriesOnly),
                new JLabel("Input folder"),
                null);
        fileSelection.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileSelection.addActionListener(e -> batchImporter.setInputFolder(fileSelection.getPath()));

        JCheckBox treatmentFolders = formPanel.addToForm(new JCheckBox("Subfolders are treatments", batchImporter.isSubfoldersAreTreatments()),
                null);
        treatmentFolders.addActionListener(e -> batchImporter.setSubfoldersAreTreatments(treatmentFolders.isSelected()));
        JCheckBox collapseTreatmentInName = formPanel.addToForm(new JCheckBox("Include treatment in name", batchImporter.isIncludeTreatmentInName()),
                null);
        collapseTreatmentInName.addActionListener(e -> batchImporter.setIncludeTreatmentInName(collapseTreatmentInName.isSelected()));

        addToggleForPattern(formPanel,
                "raw images",
                batchImporter::isImportRawImages,
                batchImporter::setImportRawImages,
                batchImporter::getRawImagesPattern,
                batchImporter::setRawImagesPattern,
                null);
        addToggleForPattern(formPanel,
                "ROI",
                batchImporter::isImportROI,
                batchImporter::setImportROI,
                batchImporter::getRoiPattern,
                batchImporter::setRoiPattern,
                null);
//        addToggleForPattern(formPanel,
//                "preprocessed images",
//                batchImporter::isImportPreprocessedImages,
//                batchImporter::setImportPreprocessedImages,
//                batchImporter::getPreprocessedImagesPattern,
//                batchImporter::setPreprocessedImagesPattern,
//                null);
//        addToggleForPattern(formPanel,
//                "derivative matrix",
//                batchImporter::isImportDerivativeMatrix,
//                batchImporter::setImportDerivativeMatrix,
//                batchImporter::getDerivativeMatrixPattern,
//                batchImporter::setDerivativeMatrixPattern,
//                null);
//        addToggleForPattern(formPanel,
//                "clusters",
//                batchImporter::isImportClusters,
//                batchImporter::setImportClusters,
//                batchImporter::getClustersPattern,
//                batchImporter::setClustersPattern,
//                null);

        formPanel.addVerticalGlue();
        add(formPanel, BorderLayout.CENTER);
    }

    private void runImport() {
        try {
            batchImporter.run();
            setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
