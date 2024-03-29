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
package org.hkijena.mcat.ui.parameters;

import org.hkijena.mcat.api.MCATValidatable;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.utils.UIUtils;
import org.scijava.Context;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Supplier;

/**
 * UI that generates a set of parameter values
 */
public abstract class MCATParameterGeneratorUI extends JPanel implements Supplier<List<Object>>, MCATValidatable {
    private Context context;

    /**
     * Creates a new instance
     *
     * @param context the SciJava context
     */
    public MCATParameterGeneratorUI(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Shows a dialog that allows the user to setup the generator
     *
     * @param parent  The parent component
     * @param context The SciJava context
     * @param uiClass The generator UI class
     * @return the generated values or null if the user cancelled
     */
    public static List<Object> showDialog(Component parent, Context context, Class<? extends MCATParameterGeneratorUI> uiClass) {
        Dialog dialog = new Dialog(SwingUtilities.getWindowAncestor(parent), context, uiClass);
        dialog.setTitle("");
        dialog.setModal(true);
        dialog.pack();
        dialog.setSize(new Dimension(500, 400));
        dialog.setLocationRelativeTo(parent);
        UIUtils.addEscapeListener(dialog);
        dialog.setVisible(true);

        if (dialog.cancelled)
            return null;
        else
            return dialog.getGeneratedValue();
    }

    /**
     * Dialog around an {@link MCATParameterGeneratorUI}
     */
    private static class Dialog extends JDialog {

        private boolean cancelled = true;
        private MCATParameterGeneratorUI generatorUI;

        public Dialog(Window windowAncestor, Context context, Class<? extends MCATParameterGeneratorUI> uiClass) {
            super(windowAncestor);
            try {
                this.generatorUI = uiClass.getConstructor(Context.class).newInstance(context);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            initialize();
        }

        private void initialize() {
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(generatorUI, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalGlue());

            JButton cancelButton = new JButton("Cancel", UIUtils.getIconFromResources("remove.png"));
            cancelButton.addActionListener(e -> {
                this.cancelled = true;
                this.setVisible(false);
            });
            buttonPanel.add(cancelButton);

            JButton confirmButton = new JButton("Generate", UIUtils.getIconFromResources("run.png"));
            confirmButton.addActionListener(e -> {
                MCATValidityReport report = new MCATValidityReport();
                generatorUI.reportValidity(report);
                if (!report.isValid()) {
//                    UIUtils.openValidityReportDialog(this, report, true);
                    JOptionPane.showMessageDialog(this, "The parameters are invalid!", "Generate", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                this.cancelled = false;
                this.setVisible(false);
            });
            buttonPanel.add(confirmButton);

            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        }

        public List<Object> getGeneratedValue() {
            return generatorUI.get();
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }
}
