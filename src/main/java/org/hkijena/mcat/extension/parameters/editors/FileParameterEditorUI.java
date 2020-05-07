package org.hkijena.mcat.extension.parameters.editors;

import org.hkijena.mcat.ui.components.FileSelection;
import org.hkijena.mcat.utils.api.parameters.ACAQParameterAccess;
import org.hkijena.mcat.utils.ui.parameters.ACAQParameterEditorUI;
import org.scijava.Context;

import java.awt.*;
import java.io.File;

/**
 * Editor for a {@link File} parameter
 */
public class FileParameterEditorUI extends ACAQParameterEditorUI {

    private boolean skipNextReload = false;
    private boolean isReloading = false;
    private FileSelection fileSelection;

    /**
     * @param context         SciJava context
     * @param parameterAccess the parameter
     */
    public FileParameterEditorUI(Context context, ACAQParameterAccess parameterAccess) {
        super(context, parameterAccess);
        initialize();
//        getWorkbenchUI().getProject().getEventBus().register(this);
    }

    @Override
    public boolean isUILabelEnabled() {
        return true;
    }

    @Override
    public void reload() {
        if (skipNextReload) {
            skipNextReload = false;
            return;
        }
        isReloading = true;
        fileSelection.setPath(((File) getParameterAccess().get()).toPath());
        isReloading = false;
    }

    private void initialize() {
        setLayout(new BorderLayout());
        fileSelection = new FileSelection(FileSelection.IOMode.Open, FileSelection.PathMode.FilesOnly);
        FilePathParameterSettings settings = getParameterAccess().getAnnotationOfType(FilePathParameterSettings.class);
        if (settings != null) {
            fileSelection.setIoMode(settings.ioMode());
            fileSelection.setPathMode(settings.pathMode());
        }

        fileSelection.setPath(((File) getParameterAccess().get()).toPath());
        add(fileSelection, BorderLayout.CENTER);
        fileSelection.addActionListener(e -> {
            if (!isReloading) {
                skipNextReload = true;
                if (!getParameterAccess().set(fileSelection.getPath().toFile())) {
                    skipNextReload = false;
                    reload();
                }
            }
        });
    }

//    @Subscribe
//    public void onWorkDirectoryChanged(WorkDirectoryChangedEvent event) {
//        if (event.getWorkDirectory() != null)
//            fileSelection.getFileChooser().setCurrentDirectory(event.getWorkDirectory().toFile());
//    }
}
