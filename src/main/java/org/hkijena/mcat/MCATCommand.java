/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.hkijena.mcat.api.MCATProject;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImageJ;

/**
 * Plugin main entry point
 */
@Plugin(type = Command.class, menuPath = "Plugins>MSOT Cluster Analysis Toolkit (Mcat)")
public class MCATCommand implements Command {

    @Parameter
    private Context context;

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
            ToolTipManager.sharedInstance().setInitialDelay(1000);
            org.hkijena.mcat.ui.MCATWorkbenchUI.newWindow(this, new MCATProject());
        });
    }

    public Context getContext() {
        return context;
    }

    public static void main(final String... args) {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(org.hkijena.mcat.MCATCommand.class, true);
    }
}
