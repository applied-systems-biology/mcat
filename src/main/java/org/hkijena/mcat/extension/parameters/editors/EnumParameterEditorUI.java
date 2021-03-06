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
package org.hkijena.mcat.extension.parameters.editors;

import java.awt.BorderLayout;

import javax.swing.JComboBox;

import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.ui.parameters.MCATParameterEditorUI;
import org.scijava.Context;

/**
 * A parameter editor UI that works for all enumerations
 */
public class EnumParameterEditorUI extends MCATParameterEditorUI {

    private boolean skipNextReload = false;
    private boolean isReloading = false;
    private JComboBox<Object> comboBox;

    /**
     * @param context         SciJava context
     * @param parameterAccess the parameter
     */
    public EnumParameterEditorUI(Context context, MCATParameterAccess parameterAccess) {
        super(context, parameterAccess);
        initialize();
        reload();
    }

    @Override
    public boolean isUILabelEnabled() {
        return true;
    }

    @Override
    public void reload() {
        if (skipNextReload) {
            skipNextReload = false;
            return;
        }
        isReloading = true;
        comboBox.setSelectedItem(getParameterAccess().get());
        isReloading = false;
    }

    private void initialize() {
        setLayout(new BorderLayout());
        Object[] values = getParameterAccess().getFieldClass().getEnumConstants();
        comboBox = new JComboBox<>(values);
        comboBox.setSelectedItem(getParameterAccess().get());
        comboBox.addActionListener(e -> {
            if (!isReloading) {
                skipNextReload = true;
                if (!getParameterAccess().set(comboBox.getSelectedItem())) {
                    skipNextReload = false;
                    reload();
                }
            }
        });
        add(comboBox, BorderLayout.CENTER);
    }
}
