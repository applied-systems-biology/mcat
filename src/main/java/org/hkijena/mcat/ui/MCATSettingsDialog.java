package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATSettings;
import org.hkijena.mcat.api.cellpose.CellPoseEnvInstaller;
import org.hkijena.mcat.api.cellpose.CellPoseGPUEnvInstaller;
import org.hkijena.mcat.api.parameters.MCATMutableParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATParameterVisibility;
import org.hkijena.mcat.ui.components.DocumentTabPane;
import org.hkijena.mcat.ui.components.MarkdownDocument;
import org.hkijena.mcat.ui.parameters.ParameterPanel;
import org.hkijena.mcat.utils.PythonEnvironment;
import org.hkijena.mcat.utils.UIUtils;
import org.scijava.Context;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.annotation.Annotation;

public class MCATSettingsDialog extends JDialog {
    private final Context context;
    private final MCATWorkbenchUI workbenchUI;

    public MCATSettingsDialog(Context context, MCATWorkbenchUI parent) {
        super(SwingUtilities.getWindowAncestor(parent));
        this.context = context;
        workbenchUI = parent;
        initialize();
        setTitle("Settings - MCAT");
        setSize(800, 600);
        setLocationRelativeTo(parent);
        revalidate();
        repaint();
    }

    private void initialize() {
        JPanel content = new JPanel(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton installCellposeGPUButton = new JButton("Install Cellpose (GPU)", UIUtils.getIconFromResources("cellpose.png"));
        installCellposeGPUButton.addActionListener(e -> installCellposeGPU());
        toolBar.add(installCellposeGPUButton);

        JButton installCellposeCPUButton = new JButton("Install Cellpose (CPU)", UIUtils.getIconFromResources("cellpose.png"));
        installCellposeCPUButton.addActionListener(e -> installCellposeCPU());
        toolBar.add(installCellposeCPUButton);

        content.add(toolBar, BorderLayout.NORTH);

        ParameterPanel parameterPanel = new ParameterPanel(context, MCATSettings.getInstance(), new MarkdownDocument(""), ParameterPanel.WITH_SCROLLING | ParameterPanel.WITH_DOCUMENTATION);
        content.add(parameterPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel", UIUtils.getIconFromResources("remove.png"));
        cancelButton.addActionListener(e -> {
            MCATSettings.reloadProperties();
            setVisible(false);
        });
        buttonPanel.add(cancelButton);

        JButton okButton = new JButton("Save", UIUtils.getIconFromResources("checkmark.png"));
        okButton.addActionListener(e -> {
            MCATSettings.saveProperties();
            setVisible(false);
        });
        buttonPanel.add(okButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MCATSettings.reloadProperties();
            }
        });

        content.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(content);
    }



    private void installCellposeCPU() {
        MCATParameterAccess access = new MCATParameterAccess() {
            @Override
            public String getKey() {
                return "";
            }

            @Override
            public String getShortKey() {
                return null;
            }

            @Override
            public int getUIOrder() {
                return 0;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public MCATParameterVisibility getVisibility() {
                return null;
            }

            @Override
            public <T extends Annotation> T getAnnotationOfType(Class<T> klass) {
                return null;
            }

            @Override
            public Class<?> getFieldClass() {
                return PythonEnvironment.class;
            }

            @Override
            public <T> T get() {
                return (T) MCATSettings.getInstance().getCellposeEnvironment();
            }

            @Override
            public <T> boolean set(T value) {
                MCATSettings.getInstance().setCellposeEnvironment((PythonEnvironment) value);
                MCATSettings.saveProperties();
                return true;
            }

            @Override
            public MCATParameterCollection getSource() {
                return null;
            }

            @Override
            public double getPriority() {
                return 0;
            }
        };
        CellPoseEnvInstaller envInstaller = new CellPoseEnvInstaller(workbenchUI, workbenchUI.getContext(), access);
        workbenchUI.getDocumentTabPane().addTab("Install Cellpose (CPU)",
                UIUtils.getIconFromResources("cellpose.png"),
                new MCATRunnableUI(workbenchUI, envInstaller),
                DocumentTabPane.CloseMode.withAskOnCloseButton,
                false);
        workbenchUI.getDocumentTabPane().switchToLastTab();
        setVisible(false);
    }

    private void installCellposeGPU() {
        MCATParameterAccess access = new MCATParameterAccess() {
            @Override
            public String getKey() {
                return "";
            }

            @Override
            public String getShortKey() {
                return null;
            }

            @Override
            public int getUIOrder() {
                return 0;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public MCATParameterVisibility getVisibility() {
                return null;
            }

            @Override
            public <T extends Annotation> T getAnnotationOfType(Class<T> klass) {
                return null;
            }

            @Override
            public Class<?> getFieldClass() {
                return PythonEnvironment.class;
            }

            @Override
            public <T> T get() {
                return (T) MCATSettings.getInstance().getCellposeEnvironment();
            }

            @Override
            public <T> boolean set(T value) {
                MCATSettings.getInstance().setCellposeEnvironment((PythonEnvironment) value);
                MCATSettings.saveProperties();
                return true;
            }

            @Override
            public MCATParameterCollection getSource() {
                return null;
            }

            @Override
            public double getPriority() {
                return 0;
            }
        };
        CellPoseGPUEnvInstaller envInstaller = new CellPoseGPUEnvInstaller(workbenchUI, workbenchUI.getContext(), access);
        workbenchUI.getDocumentTabPane().addTab("Install Cellpose (GPU)",
                UIUtils.getIconFromResources("cellpose.png"),
                new MCATRunnableUI(workbenchUI, envInstaller),
                DocumentTabPane.CloseMode.withAskOnCloseButton,
                false);
        workbenchUI.getDocumentTabPane().switchToLastTab();
        setVisible(false);
    }
}
