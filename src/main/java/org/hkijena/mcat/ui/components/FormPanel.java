package org.hkijena.mcat.ui.components;

import org.scijava.util.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hkijena.mcat.utils.UIUtils.UI_PADDING;

public class FormPanel extends JPanel {

    private int numRows = 0;
    private String currentGroup;
    private Map<String, List<Component>> componentGroups = new HashMap<>();
    private JPanel forms = new JPanel();
    private MarkdownReader parameterHelp;

    public FormPanel() {
        setLayout(new BorderLayout());
        forms.setLayout(new GridBagLayout());

        JPanel helpPanel = new JPanel(new BorderLayout());
        parameterHelp = new MarkdownReader(false);
        helpPanel.add(parameterHelp, BorderLayout.CENTER);

        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(forms), helpPanel) {
            {
                setDividerSize(3);
                setResizeWeight(0.33);
            }
        });
    }

    private void documentComponent(Component component, String documentationPath) {
        if(documentationPath != null) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    parameterHelp.loadFromResource(documentationPath);
                }
            });
            component.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    super.focusGained(e);
                    parameterHelp.loadFromResource(documentationPath);
                }
            });
        }
    }

    public <T extends Component> T addToForm(T component, String documentationPath) {
        forms.add(component, new GridBagConstraints() {
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
        documentComponent(component, documentationPath);
        return component;
    }

    public <T extends Component> T addToForm(T component, JLabel description, String documentationPath) {
        forms.add(component, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.WEST;
                gridx = 1;
                gridwidth = 1;
                gridy = numRows;
                insets = UI_PADDING;
                fill = GridBagConstraints.HORIZONTAL;
                weightx = 1;
            }
        });
        forms.add(description, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.WEST;
                gridx = 0;
                gridwidth = 1;
                gridy = numRows;
                insets = UI_PADDING;
            }
        });
        ++numRows;
        getComponentListForCurrentGroup().add(component);
        documentComponent(component, documentationPath);
        documentComponent(description, documentationPath);
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
        forms.add(new JPanel(), new GridBagConstraints() {
            {
                anchor = GridBagConstraints.WEST;
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
        forms.add(new JPanel() {
            {
                setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            }
        }, new GridBagConstraints() {
            {
                anchor = GridBagConstraints.WEST;
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
