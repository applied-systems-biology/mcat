/*
 * Copyright by Zoltán Cseresnyés, Ruman Gerst
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 */

package org.hkijena.mcat.extension.parameters.editors;

import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.registries.MCATUIParametertypeRegistry;
import org.hkijena.mcat.extension.parameters.optional.OptionalParameter;
import org.hkijena.mcat.extension.parameters.optional.OptionalParameterContentAccess;
import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.parameters.MCATParameterEditorUI;
import org.hkijena.mcat.utils.UIUtils;
import org.scijava.Context;

import javax.swing.*;
import java.awt.BorderLayout;


public class OptionalParameterEditorUI extends MCATParameterEditorUI {
    /**
     * Creates new instance
     *
     * @param context       context
     * @param parameterAccess Parameter
     */
    public OptionalParameterEditorUI(Context context, MCATParameterAccess parameterAccess) {
        super(context, parameterAccess);
        initialize();
        reload();
    }

    private void initialize() {
        setLayout(new BorderLayout());
    }

    @Override
    public boolean isUILabelEnabled() {
        return true;
    }

    @Override
    public void reload() {
        OptionalParameter<?> parameter = getParameterAccess().get();
        removeAll();

        // Create toggle button
        JToggleButton toggle = new JToggleButton("Enabled", UIUtils.getIconFromResources("check-square.png"));
        UIUtils.makeFlat(toggle);
        toggle.setToolTipText("If enabled, the parameter is not ignored.");
        toggle.setSelected(parameter.isEnabled());
        toggle.setIcon(toggle.isSelected() ? UIUtils.getIconFromResources("check-square.png") :
                UIUtils.getIconFromResources("empty-square.png"));
        toggle.addActionListener(e -> {
            parameter.setEnabled(toggle.isSelected());
            getParameterAccess().set(parameter);
        });
        add(toggle, BorderLayout.WEST);

        OptionalParameterContentAccess<?> access = new OptionalParameterContentAccess(getParameterAccess(), parameter);
        MCATParameterEditorUI ui = MCATUIParametertypeRegistry.getInstance().createEditorFor(getContext(), access);
        add(ui, BorderLayout.CENTER);

        revalidate();
        repaint();

    }
}
