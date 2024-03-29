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
package org.hkijena.mcat.ui.components;

import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Text field with a file selection
 */
public class FileSelection extends JPanel {

    private JFileChooser fileChooser = new JFileChooser();
    private JTextField pathEdit;
    private IOMode ioMode;
    private PathMode pathMode;
    private Set<ActionListener> listeners = new HashSet<>();
    private JButton generateRandomButton;

    /**
     * Creates a new file selection that opens a file
     */
    public FileSelection() {
        setPathMode(PathMode.FilesOnly);
        initialize();
        setIoMode(IOMode.Open);
    }

    /**
     * @param ioMode   If a path is opened or saved
     * @param pathMode If the path is a file, directory or anything
     */
    public FileSelection(IOMode ioMode, PathMode pathMode) {
        setPathMode(pathMode);
        initialize();
        setIoMode(ioMode);
    }

    private void initialize() {
        // Setup the GUI
        setLayout(new GridBagLayout());

        pathEdit = new JTextField();
        add(pathEdit, new GridBagConstraints() {
            {
                gridx = 0;
                gridy = 0;
                gridwidth = 1;
                anchor = GridBagConstraints.WEST;
                fill = GridBagConstraints.HORIZONTAL;
                weightx = 1;
                insets = UIUtils.UI_PADDING;
            }
        });

        generateRandomButton = new JButton(UIUtils.getIconFromResources("random.png"));
        generateRandomButton.setToolTipText("Generate random file or folder");
        UIUtils.makeFlat25x25(generateRandomButton);
        generateRandomButton.addActionListener(e -> generateRandom());
        add(generateRandomButton, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.PAGE_START;
                gridx = 1;
                gridy = 0;
            }
        });

        JButton selectButton = new JButton(UIUtils.getIconFromResources("open.png"));
        selectButton.setToolTipText("Select from filesystem");
        UIUtils.makeFlat25x25(selectButton);
        add(selectButton, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.PAGE_START;
                gridx = 2;
                gridy = 0;
            }
        });

        selectButton.addActionListener(e -> showFileChooser());

        pathEdit.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void changed(DocumentEvent documentEvent) {
                postAction();
            }
        });
    }

    /**
     * Opens the file chooser
     */
    public void showFileChooser() {
        if (ioMode == IOMode.Open) {
            if (getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                pathEdit.setText(getFileChooser().getSelectedFile().toString());
            }
        } else {
            if (getFileChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                pathEdit.setText(getFileChooser().getSelectedFile().toString());
            }
        }
    }

    private void generateRandom() {
        try {
            if (pathMode == PathMode.DirectoriesOnly) {
                setPath(Files.createTempDirectory("MCAT"));
            } else {
                setPath(Files.createTempFile("MCAT", null));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getPath() {
        return Paths.get(pathEdit.getText());
    }

    public void setPath(Path path) {
        if (path != null)
            pathEdit.setText(path.toString());
        else
            pathEdit.setText("");
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    private void postAction() {
        for (ActionListener listener : listeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "text-changed"));
        }
    }

    /**
     * Adds a listener for when the path property changes
     *
     * @param listener Listens to when a file is selected
     */
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    /**
     * @param listener Registered listener
     */
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }

    public IOMode getIoMode() {
        return ioMode;
    }

    public void setIoMode(IOMode ioMode) {
        this.ioMode = ioMode;
        generateRandomButton.setVisible(ioMode == IOMode.Save);
    }

    public PathMode getPathMode() {
        return pathMode;
    }

    public void setPathMode(PathMode pathMode) {
        this.pathMode = pathMode;
        switch (pathMode) {
            case FilesOnly:
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                break;
            case DirectoriesOnly:
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                break;
            case FilesAndDirectories:
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                break;
        }
    }

    /**
     * Determines if a path is opened or saved
     */
    public enum IOMode {
        Open,
        Save
    }

    /**
     * Determines the type of selected path
     */
    public enum PathMode {
        FilesOnly,
        DirectoriesOnly,
        FilesAndDirectories
    }
}
