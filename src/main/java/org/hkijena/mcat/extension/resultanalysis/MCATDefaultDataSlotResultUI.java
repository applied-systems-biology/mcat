package org.hkijena.mcat.extension.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.resultanalysis.MCATResultDataSlotUI;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MCATDefaultDataSlotResultUI extends MCATResultDataSlotUI {

    private List<SlotAction> registeredSlotActions = new ArrayList<>();
    private MCATWorkbenchUI workbenchUI;

    public MCATDefaultDataSlotResultUI(MCATWorkbenchUI workbenchUI, Path outputPath, MCATResultDataInterfaces.SlotEntry slot) {
        super(outputPath, slot);
        this.workbenchUI = workbenchUI;
        registerActions();
        initialize();
    }

    public Path findFirstFileWithExtension(String extension) {
        if (getStoragePath() != null && Files.isDirectory(getStoragePath())) {
            try {
                return Files.list(getStoragePath()).filter(p -> Files.isRegularFile(p) && p.toString().endsWith(extension)).findFirst().orElse(null);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private void initialize() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());

//        if (!registeredSlotActions.isEmpty()) {
//            SlotAction mainSlotAction = registeredSlotActions.get(registeredSlotActions.size() - 1);
//            JButton mainActionButton = new JButton(mainSlotAction.getName(), mainSlotAction.getIcon());
//            mainActionButton.setToolTipText(mainSlotAction.getDescription());
//            mainActionButton.addActionListener(e -> mainSlotAction.action.accept(this));
//            add(mainActionButton);
//
//            if (registeredSlotActions.size() > 1) {
//                JButton menuButton = new JButton("...");
//                menuButton.setMaximumSize(new Dimension(1, (int) mainActionButton.getPreferredSize().getHeight()));
//                menuButton.setToolTipText("More actions ...");
//                JPopupMenu menu = UIUtils.addPopupMenuToComponent(menuButton);
//                for (int i = registeredSlotActions.size() - 2; i >= 0; --i) {
//                    SlotAction otherSlotAction = registeredSlotActions.get(i);
//                    JMenuItem item = new JMenuItem(otherSlotAction.getName(), otherSlotAction.getIcon());
//                    item.setToolTipText(otherSlotAction.getDescription());
//                    item.addActionListener(e -> otherSlotAction.getAction().accept(this));
//                    menu.add(item);
//                }
//                add(menuButton);
//            }
//        }

        for (int i = registeredSlotActions.size() - 1; i >= 0; --i) {
            SlotAction slotAction = registeredSlotActions.get(i);
            JButton mainActionButton = new JButton(slotAction.getName(), slotAction.getIcon());
            mainActionButton.setToolTipText(slotAction.getDescription());
            mainActionButton.addActionListener(e -> slotAction.action.accept(this));
            add(mainActionButton);
        }

    }

    /**
     * Override this method to add actions
     * The last added action is displayed as full button
     */
    protected void registerActions() {
        if (getSlot().getStoragePath() != null) {
            registerAction("Open folder",
                    "Opens the folder that contains the data files.",
                    UIUtils.getIconFromResources("open.png"),
                    s -> openFolder());
        }
    }

    /**
     * Registers an action for the data slot
     *
     * @param name        The name of the action
     * @param description A description of the action
     * @param icon        An icon
     * @param action      A method called when the action is activated
     */
    protected void registerAction(String name, String description, Icon icon, Consumer<MCATResultDataSlotUI> action) {
        registeredSlotActions.add(new SlotAction(name, description, icon, action));
    }


    /**
     * @return absolute storage path
     */
    public Path getStoragePath() {
        return getOutputPath().resolve(getSlot().getStoragePath());
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(getStoragePath().toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MCATWorkbenchUI getWorkbenchUI() {
        return workbenchUI;
    }

    /**
     * Encapsulates a slot action
     */
    public static class SlotAction {
        private String name;
        private String description;
        private Icon icon;
        private Consumer<MCATResultDataSlotUI> action;

        private SlotAction(String name, String description, Icon icon, Consumer<MCATResultDataSlotUI> action) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.action = action;
        }

        public String getName() {
            return name;
        }

        public Icon getIcon() {
            return icon;
        }

        public Consumer<MCATResultDataSlotUI> getAction() {
            return action;
        }

        public String getDescription() {
            return description;
        }
    }
}
