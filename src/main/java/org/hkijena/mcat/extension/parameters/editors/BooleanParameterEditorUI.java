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

import javax.swing.JCheckBox;

import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.ui.parameters.MCATParameterEditorUI;
import org.scijava.Context;

/**
 * Parameter editor for boolean data
 */
public class BooleanParameterEditorUI extends MCATParameterEditorUI {

    private JCheckBox checkBox;
    private boolean skipNextReload = false;
    private boolean isReloading = false;

    /**
     * @param context         SciJava context
     * @param parameterAccess the parameter
     */
    public BooleanParameterEditorUI(Context context, MCATParameterAccess parameterAccess) {
        super(context, parameterAccess);
        initialize();
    }

    @Override
    public boolean isUILabelEnabled() {
        return false;
    }

    @Override
    public void reload() {
        if (skipNextReload) {
            skipNextReload = false;
            return;
        }
        isReloading = true;
        Object value = getParameterAccess().get();
        boolean booleanValue = false;
        if (value != null)
            booleanValue = (boolean) value;
        checkBox.setSelected(booleanValue);
        isReloading = false;
    }

    private void initialize() {
        setLayout(new BorderLayout());
        Object value = getParameterAccess().get();
        boolean booleanValue = false;
        if (value != null)
            booleanValue = (boolean) value;
        checkBox = new JCheckBox(getParameterAccess().getName());
        checkBox.setSelected(booleanValue);
        add(checkBox, BorderLayout.CENTER);
        checkBox.addActionListener(e -> {
            if (!isReloading) {
                skipNextReload = true;
                getParameterAccess().set(checkBox.isSelected());
            }
        });
    }
}
