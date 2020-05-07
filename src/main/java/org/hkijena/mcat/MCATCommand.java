package org.hkijena.mcat;

import net.imagej.ImageJ;
import org.hkijena.mcat.api.MCATProject;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;

/**
 * Plugin main entry point
 */
@Plugin(type = Command.class, menuPath = "Plugins>MSOT Cluster Analysis Tool (MCAT)")
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
