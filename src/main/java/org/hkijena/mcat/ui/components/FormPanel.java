package org.hkijena.mcat.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    public FormPanel(String defaultHelpDocumentPath) {
        setLayout(new BorderLayout());
        forms.setLayout(new GridBagLayout());

        JPanel helpPanel = new JPanel(new BorderLayout());
        parameterHelp = new MarkdownReader(false);
        parameterHelp.loadDefaultDocument(defaultHelpDocumentPath);
        helpPanel.add(parameterHelp, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(forms), helpPanel);
        splitPane.setDividerSize(3);
        splitPane.setResizeWeight(0.33);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                splitPane.setDividerLocation(0.66);
            }
        });
        add(splitPane, BorderLayout.CENTER);
    }

    public FormPanel() {
        this(null);
    }

    private void documentComponent(Component component, String documentationPath) {
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
        getComponentListForCurrentGroup().add(description);
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

    public void addGroupToggle(AbstractButton toggle, String group) {
        toggle.addActionListener(e -> setGroupVisiblity(group, toggle.isSelected()));
        setGroupVisiblity(group, toggle.isSelected());
    }

    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }
}
