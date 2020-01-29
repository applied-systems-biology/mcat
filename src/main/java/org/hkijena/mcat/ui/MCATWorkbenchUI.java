package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATProject;
import org.hkijena.mcat.ui.components.DocumentTabPane;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class MCATWorkbenchUI extends JFrame {

    private MCATProject project;
    private org.hkijena.mcat.MCATCommand command;
    private DocumentTabPane documentTabPane;

    public MCATWorkbenchUI(org.hkijena.mcat.MCATCommand command, MCATProject project) {
        this.project = project;
        this.command = command;
        initialize();
    }

    private void initialize() {
        getContentPane().setLayout(new BorderLayout(8, 8));
        setTitle("MSOT Clustering Analysis Tool (MCAT)");
        setIconImage(UIUtils.getIconFromResources("module.png").getImage());
        UIUtils.setToAskOnClose(this, "Do you really want to close MCAT?", "Close window");

        documentTabPane = new DocumentTabPane();
        add(documentTabPane, BorderLayout.CENTER);

        initializeToolbar();
    }

    private void initializeToolbar() {
        JToolBar toolBar = new JToolBar();

        // Add "New project" toolbar entry
        JButton newProject = new JButton("New project ...", UIUtils.getIconFromResources("new.png"));
        newProject.addActionListener(e -> newWindow(command, new MCATProject()));
        toolBar.add(newProject);

        // "Open project" entry
        JButton openProject = new JButton("Open project ...", UIUtils.getIconFromResources("open.png"));
        toolBar.add(openProject);

        // "Save project" entry
        JButton saveProject = new JButton("Save project ...", UIUtils.getIconFromResources("save.png"));
        toolBar.add(saveProject);

        toolBar.add(Box.createHorizontalGlue());

        // "Run" entry
        JButton runProject = new JButton("Run", UIUtils.getIconFromResources("run.png"));
        toolBar.add(runProject);

        initializeToolbarHelpMenu(toolBar);

        add(toolBar, BorderLayout.NORTH);
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
        return  documentTabPane;
    }

    public MCATProject getProject() {
        return project;
    }

    public static void newWindow(org.hkijena.mcat.MCATCommand command, MCATProject project) {
        org.hkijena.mcat.ui.MCATWorkbenchUI frame = new org.hkijena.mcat.ui.MCATWorkbenchUI(command, project);
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);
//        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }


}
