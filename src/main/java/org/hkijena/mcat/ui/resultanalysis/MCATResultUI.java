package org.hkijena.mcat.ui.resultanalysis;

import org.hkijena.mcat.api.MCATResult;
import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.MCATWorkbenchUIPanel;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

public class MCATResultUI extends MCATWorkbenchUIPanel {
    private JSplitPane splitPane;
    private MCATResult result;

    public MCATResultUI(MCATWorkbenchUI workbenchUI, MCATResult result) {
        super(workbenchUI);
        this.result = result;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        MCATResultSlotManagerUI sampleManagerUI = new MCATResultSlotManagerUI(this);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sampleManagerUI, new JPanel());
        splitPane.setDividerSize(3);
        splitPane.setResizeWeight(0.33);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                splitPane.setDividerLocation(0.33);
            }
        });
        add(splitPane, BorderLayout.CENTER);

        sampleManagerUI.getSampleTree().addTreeSelectionListener(e -> {
            Object pathComponent = e.getPath().getLastPathComponent();
            if (pathComponent != null) {
                MCATResultTreeNode treeNode = (MCATResultTreeNode) pathComponent;
                splitPane.setRightComponent(new MCATResultSlotListUI(getWorkbenchUI(), result.getOutputFolder(), treeNode.getSlotEntries()));
            }
        });

        initializeToolbar();
    }

    private void initializeToolbar() {
        JToolBar toolBar = new JToolBar();
        JButton openFolderButton = new JButton("Open output folder", UIUtils.getIconFromResources("open.png"));
        openFolderButton.addActionListener(e -> openOutputFolder());
        toolBar.add(openFolderButton);
        add(toolBar, BorderLayout.NORTH);
    }

    private void openOutputFolder() {
        try {
            Desktop.getDesktop().open(result.getOutputFolder().toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MCATResult getResult() {
        return result;
    }
}
