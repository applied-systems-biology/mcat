package org.hkijena.mcat.ui;

import org.hkijena.mcat.MCATCommand;
import org.hkijena.mcat.api.MCATProject;
import org.hkijena.mcat.ui.components.DocumentTabPane;
import org.hkijena.mcat.ui.parameters.MCATParametersTableUI;
import org.hkijena.mcat.utils.UIUtils;
import org.scijava.Context;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Main MCAT window
 */
public class MCATWorkbenchUI extends JFrame {

    private MCATProject project;
    private MCATCommand command;
    private DocumentTabPane documentTabPane;

    public MCATWorkbenchUI(MCATCommand command, MCATProject project) {
        this.project = project;
        this.command = command;
        initialize();
    }

    private void initialize() {
        getContentPane().setLayout(new BorderLayout(8, 8));
        setTitle("MSOT Clustering Analysis Tool (MCAT)");
        setIconImage(UIUtils.getIconFromResources("mcat.png").getImage());
        UIUtils.setToAskOnClose(this, "Do you really want to close MCAT?", "Close window");

        documentTabPane = new DocumentTabPane();
        documentTabPane.addTab("Data",
                UIUtils.getIconFromResources("sample.png"),
                new MCATDataUI(this),
                DocumentTabPane.CloseMode.withoutCloseButton,
                false);
        documentTabPane.addTab("Parameters",
                UIUtils.getIconFromResources("wrench.png"),
                new MCATParametersTableUI(this),
                DocumentTabPane.CloseMode.withoutCloseButton,
                false);
//        documentTabPane.addTab( "Preprocessing",
//                UIUtils.getIconFromResources("wrench.png"),
//                new MCATPreprocessingUI(this),
//                DocumentTabPane.CloseMode.withoutCloseButton,
//                false);
//        documentTabPane.addTab( "Clustering",
//                UIUtils.getIconFromResources("clustering.png"),
//                new MCATClusteringUI(this),
//                DocumentTabPane.CloseMode.withoutCloseButton,
//                false);
//        documentTabPane.addTab( "Postprocessing",
//                UIUtils.getIconFromResources("statistics.png"),
//                new MCATPostprocessingUI(this),
//                DocumentTabPane.CloseMode.withoutCloseButton,
//                false);

        add(documentTabPane, BorderLayout.CENTER);

        initializeToolbar();
    }

    private void initializeToolbar() {
        JToolBar toolBar = new JToolBar();

        // Add "New project" toolbar entry
        JButton newProjectButton = new JButton("New project ...", UIUtils.getIconFromResources("new.png"));
        newProjectButton.addActionListener(e -> newWindow(command, new MCATProject()));
        toolBar.add(newProjectButton);

        // "Open project" entry
        JButton openProjectButton = new JButton("Open project ...", UIUtils.getIconFromResources("open.png"));
        openProjectButton.addActionListener(e -> openProject());
        toolBar.add(openProjectButton);

        // "Save project" entry
        JButton saveProjectButton = new JButton("Save project ...", UIUtils.getIconFromResources("save.png"));
        saveProjectButton.addActionListener(e -> saveProject());
        toolBar.add(saveProjectButton);

        toolBar.add(Box.createHorizontalGlue());

        // "Run" entry
        JButton runProject = new JButton("Run", UIUtils.getIconFromResources("run.png"));
        runProject.addActionListener(e -> openRunUI());
        toolBar.add(runProject);

        initializeToolbarHelpMenu(toolBar);

        add(toolBar, BorderLayout.NORTH);
    }

    private void openProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Open project (*.json");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                newWindow(command, MCATProject.loadProject(fileChooser.getSelectedFile().toPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Save project (*.json");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                getProject().saveProject(fileChooser.getSelectedFile().toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void openRunUI() {
        MCATRunUI ui = new MCATRunUI(this);
        documentTabPane.addTab("Run", UIUtils.getIconFromResources("run.png"), ui,
                DocumentTabPane.CloseMode.withAskOnCloseButton, false);
        documentTabPane.switchToLastTab();
    }

    private void initializeToolbarHelpMenu(JToolBar toolBar) {
        JButton helpButton = new JButton(UIUtils.getIconFromResources("help.png"));
        JPopupMenu menu = UIUtils.addPopupMenuToComponent(helpButton);

        JMenuItem quickHelp = new JMenuItem("Quick introduction", UIUtils.getIconFromResources("quickload.png"));
        quickHelp.addActionListener(e -> documentTabPane.selectSingletonTab("INTRODUCTION"));
        menu.add(quickHelp);

        toolBar.add(helpButton);
    }

    public DocumentTabPane getDocumentTabPane() {
        return documentTabPane;
    }

    public MCATProject getProject() {
        return project;
    }

    public Context getContext() {
        return command.getContext();
    }

    public static void newWindow(MCATCommand command, MCATProject project) {
        MCATWorkbenchUI frame = new MCATWorkbenchUI(command, project);
        frame.pack();
        frame.setSize(1024, 768);
        frame.setVisible(true);
//        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }


}
