package org.hkijena.mcat.ui.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.ui.components.FormPanel;
import org.hkijena.mcat.ui.registries.MCATResultDataSlotUIRegistry;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MCATResultSlotListUI extends JPanel {

    public MCATResultSlotListUI(List<MCATResultDataInterfaces.SlotEntry> slotEntries) {
        setLayout(new BorderLayout());
        FormPanel formPanel = new FormPanel(null, FormPanel.WITH_SCROLLING);

        Map<Path, List<MCATResultDataInterfaces.SlotEntry>> byParentStoragePath = slotEntries.stream().collect(Collectors.groupingBy(e -> e.getStoragePath().getParent()));
        for (Path parentStoragePath : byParentStoragePath.keySet().stream().sorted().collect(Collectors.toList())) {
            FormPanel.GroupHeaderPanel groupHeader = formPanel.addGroupHeader(parentStoragePath.toString(), UIUtils.getIconFromResources("sample.png"));
            JButton openFolderButton = new JButton(UIUtils.getIconFromResources("open.png"));
            openFolderButton.setToolTipText("Open folder");
            UIUtils.makeFlat25x25(openFolderButton);
            openFolderButton.addActionListener(e -> openFolder(parentStoragePath));
            groupHeader.addColumn(openFolderButton);

            for (MCATResultDataInterfaces.SlotEntry slotEntry : byParentStoragePath.get(parentStoragePath).stream().sorted(Comparator.comparing(MCATResultDataInterfaces.SlotEntry::getName)).collect(Collectors.toList())) {
                Component ui = MCATResultDataSlotUIRegistry.getInstance().getUIFor(slotEntry);
                JLabel label = new JLabel(slotEntry.getName(), UIUtils.getIconFromResources("database.png"), JLabel.LEFT);
                formPanel.addToForm(ui, label, null);
            }
        }

        formPanel.addVerticalGlue();

        add(formPanel, BorderLayout.CENTER);
    }

    private void openFolder(Path parentStoragePath) {
        try {
            Desktop.getDesktop().open(parentStoragePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
