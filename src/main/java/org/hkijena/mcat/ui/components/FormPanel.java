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


import static org.hkijena.mcat.utils.UIUtils.UI_PADDING;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.hkijena.mcat.utils.UIUtils;

/**
 * Organizes UI in a form layout with integrated help functionality, and grouping with conditional visibility
 */
public class FormPanel extends JPanel {

    /**
     * Flag that indicates no modifications, meaning (1) No documentation, and (2) no scrolling
     */
    public static final int NONE = 0;

    /**
     * Flag that indicates that a documentation panel is shown. The documentation panel is
     * attached on the right-hand side. Use DOCUMENTATION_BELOW to move it below the contents.
     * The documentation panel is shown even if the documentation provided in the constructor is null.
     */
    public static final int WITH_DOCUMENTATION = 1;

    /**
     * Flag that makes the content be wrapped in a {@link JScrollPane}
     */
    public static final int WITH_SCROLLING = 2;

    /**
     * Flag that indicates that documentation should be shown below if enabled.
     * This does not enable documentation! Use WITH_DOCUMENTATION for this.
     */
    public static final int DOCUMENTATION_BELOW = 4;

    private int numRows = 0;
    private JPanel forms = new JPanel();
    private MarkdownReader parameterHelp;

    /**
     * Creates a new instance
     *
     * @param document optional documentation
     * @param flags    flags for this component
     */
    public FormPanel(MarkdownDocument document, int flags) {
        setLayout(new BorderLayout());
        forms.setLayout(new GridBagLayout());

        JPanel helpPanel = new JPanel(new BorderLayout());
        parameterHelp = new MarkdownReader(false);
        parameterHelp.setDocument(document);
        helpPanel.add(parameterHelp, BorderLayout.CENTER);

        Component content;
        if ((flags & WITH_SCROLLING) == WITH_SCROLLING) {
            content = new JScrollPane(forms);
            ((JScrollPane)content).getVerticalScrollBar().setUnitIncrement(25);
        }
        else
            content = forms;

        if ((flags & WITH_DOCUMENTATION) == WITH_DOCUMENTATION) {
            boolean documentationBelow = (flags & DOCUMENTATION_BELOW) == DOCUMENTATION_BELOW;
            JSplitPane splitPane = new JSplitPane(documentationBelow ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT, content, helpPanel);
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
        } else {
            add(content, BorderLayout.CENTER);
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();

    }

    private void documentComponent(Component component, MarkdownDocument componentDocument) {

        if (componentDocument != null) {
            Toolkit.getDefaultToolkit().addAWTEventListener(new ComponentDocumentationHandler(this, component, componentDocument),
                    AWTEvent.MOUSE_EVENT_MASK);
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    parameterHelp.setTemporaryDocument(componentDocument);
                }
            });
            component.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    super.focusGained(e);
                    parameterHelp.setTemporaryDocument(componentDocument);
                }
            });
        }
    }

    /**
     * Adds a component to the form
     *
     * @param component     The component
     * @param documentation Optional documentation for this component. Can be null.
     * @param <T>           Component type
     * @return The component
     */
    public <T extends Component> T addToForm(T component, MarkdownDocument documentation) {
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
        documentComponent(component, documentation);
        return component;
    }

    /**
     * Adds a component to the form
     *
     * @param component     The component
     * @param description   A description component displayed on the left hand side
     * @param documentation Optional documentation for this component. Can be null.
     * @param <T>           Component type
     * @return The component
     */
    public <T extends Component> T addToForm(T component, Component description, MarkdownDocument documentation) {
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
        documentComponent(component, documentation);
        documentComponent(description, documentation);
        return component;
    }

    /**
     * Adds a component. Its size is two columns.
     *
     * @param component     The component
     * @param documentation Optional documentation. Can be null.
     * @param <T>           Component type
     * @return The component
     */
    public <T extends Component> T addWideToForm(T component, MarkdownDocument documentation) {
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
        documentComponent(component, documentation);
        return component;
    }

    /**
     * Adds a group header
     *
     * @param text Group text
     * @param icon Group icon
     * @return the panel that allows adding more components to it
     */
    public GroupHeaderPanel addGroupHeader(String text, Icon icon) {
        GroupHeaderPanel panel = new GroupHeaderPanel(text, icon);
        addWideToForm(panel, null);
        return panel;
    }

    /**
     * Adds a component that acts as Box.verticalGlue()
     */
    public void addVerticalGlue() {
        forms.add(new JPanel(), new GridBagConstraints() {
            {
                anchor = GridBagConstraints.WEST;
                gridx = 0;
                gridy = numRows;
                fill = GridBagConstraints.VERTICAL;
                weightx = 0;
                weighty = 1;
            }
        });
        ++numRows;
    }

    /**
     * Removes all components
     */
    public void clear() {
        forms.removeAll();
        numRows = 0;
        revalidate();
        repaint();
    }

    public MarkdownReader getParameterHelp() {
        return parameterHelp;
    }

    /**
     * Panel that contains a group header
     */
    public static class GroupHeaderPanel extends JPanel {
        private final JLabel titleLabel;
        private JTextArea descriptionArea;
        private int columnCount = 0;

        /**
         * @param text the text
         * @param icon the icon
         */
        public GroupHeaderPanel(String text, Icon icon) {
            setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
            setLayout(new GridBagLayout());

            titleLabel = new JLabel(text, icon, JLabel.LEFT);
            add(titleLabel, new GridBagConstraints() {
                {
                    gridx = 0;
                    gridy = 0;
                    anchor = GridBagConstraints.WEST;
                    insets = UI_PADDING;
                }
            });
            ++columnCount;

            add(new JSeparator(), new GridBagConstraints() {
                {
                    gridx = 1;
                    gridy = 0;
                    fill = GridBagConstraints.HORIZONTAL;
                    weightx = 1;
                    anchor = GridBagConstraints.WEST;
                    insets = UI_PADDING;
                }
            });
            ++columnCount;

            descriptionArea = UIUtils.makeReadonlyTextArea("");
            descriptionArea.setOpaque(false);
            descriptionArea.setBorder(null);
            descriptionArea.setVisible(false);
            add(descriptionArea, new GridBagConstraints() {
                {
                    gridx = 0;
                    gridy = 1;
                    gridwidth = 2;
                    fill = GridBagConstraints.HORIZONTAL;
                    anchor = GridBagConstraints.WEST;
                    insets = UI_PADDING;
                }
            });
        }

        public JLabel getTitleLabel() {
            return titleLabel;
        }

        /**
         * Adds an additional component on the right hand side
         *
         * @param component the component
         */
        public void addColumn(Component component) {
            add(component, new GridBagConstraints() {
                {
                    gridx = columnCount;
                    gridy = 0;
                    anchor = GridBagConstraints.WEST;
                    insets = UI_PADDING;
                }
            });
            ++columnCount;
        }

        public JTextArea getDescriptionArea() {
            return descriptionArea;
        }
    }

    /**
     * Mouse handler to target specific components
     */
    private static class ComponentDocumentationHandler implements AWTEventListener {

        private final WeakReference<FormPanel> formPanel;
        private final WeakReference<Component> target;
        private final MarkdownDocument componentDocument;

        private ComponentDocumentationHandler(FormPanel formPanel, Component component, MarkdownDocument componentDocument) {
            this.formPanel = new WeakReference<>(formPanel);
            this.target = new WeakReference<>(component);
            this.componentDocument = componentDocument;
        }

        @Override
        public void eventDispatched(AWTEvent event) {
            Component component = target.get();
            FormPanel panel = formPanel.get();
            if (component == null || panel == null) {
                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                return;
            }
            if (!component.isDisplayable()) {
                return;
            }
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                if (((MouseEvent) event).getComponent() == component || SwingUtilities.isDescendingFrom(mouseEvent.getComponent(), component)) {
                    Point componentLocation = component.getLocationOnScreen();
                    boolean isInComponent = mouseEvent.getXOnScreen() >= componentLocation.x &&
                            mouseEvent.getYOnScreen() >= componentLocation.y &&
                            mouseEvent.getXOnScreen() < (component.getWidth() + componentLocation.x) &&
                            mouseEvent.getYOnScreen() < (component.getHeight() + componentLocation.y);
                    if (isInComponent && panel.parameterHelp.getTemporaryDocument() != componentDocument) {
                        panel.parameterHelp.setTemporaryDocument(componentDocument);
                    }
                }
            }
        }
    }
}
