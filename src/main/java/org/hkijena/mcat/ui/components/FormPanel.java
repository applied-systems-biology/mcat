package org.hkijena.mcat.ui.components;

import org.scijava.util.Colors;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hkijena.mcat.utils.UIUtils.UI_PADDING;

public class FormPanel extends JPanel {

    private int numRows = 0;
    private String currentGroup;
    private Map<String, List<Component>> componentGroups = new HashMap<>();

    public FormPanel() {
        setLayout(new GridBagLayout());
    }

    public <T extends Component> T addToForm(T component) {
        add(component, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.WEST;
                gridx = 0;
                gridwidth = 2;
                gridy = numRows;
                insets = UI_PADDING;
                fill = GridBagConstraints.HORIZONTAL;
                weightx = 1;
            }
        });
        ++numRows;
        getComponentListForCurrentGroup().add(component);
        return component;
    }

    public <T extends Component> T addToForm(T component, JLabel description) {
        add(component, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.NORTHWEST;
                gridx = 1;
                gridwidth = 1;
                gridy = numRows;
                insets = UI_PADDING;
                fill = GridBagConstraints.HORIZONTAL;
                weightx = 1;
            }
        });
        add(description, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.NORTHWEST;
                gridx = 0;
                gridwidth = 1;
                gridy = numRows;
                insets = UI_PADDING;
            }
        });
        ++numRows;
        getComponentListForCurrentGroup().add(component);
        return component;
    }

    private List<Component> getComponentListForCurrentGroup() {
        List<Component> result = componentGroups.getOrDefault(currentGroup, null);
        if(result == null) {
            result = new ArrayList<>();
            componentGroups.put(currentGroup, result);
        }
        return result;
    }

    public void addVerticalGlue() {
        add(new JPanel(), new GridBagConstraints() {
            {
                anchor = GridBagConstraints.NORTHWEST;
                gridx = 0;
                gridy = numRows;
                fill = GridBagConstraints.HORIZONTAL | GridBagConstraints.VERTICAL;
                weightx = 1;
                weighty = 1;
            }
        });
        ++numRows;
    }

    public void addSeparator() {
        add(new JPanel() {
            {
                setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            }
        }, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.NORTHWEST;
                gridx = 0;
                gridy = numRows;
                fill = GridBagConstraints.HORIZONTAL;
                weightx = 1;
                gridwidth = 2;
            }
        });
        ++numRows;
    }

    public void setGroupVisiblity(String group, boolean visible) {
        for(Component component : componentGroups.get(group)) {
            component.setVisible(visible);
        }
    }

    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }
}
