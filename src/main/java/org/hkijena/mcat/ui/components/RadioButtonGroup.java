/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat.ui.components;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A group of multiple radio button options
 *
 * @param <T>
 */
public class RadioButtonGroup<T> extends JPanel {
    private List<T> options;
    private Map<T, JRadioButton> buttons = new HashMap<>();
    private ButtonGroup buttonGroup = new ButtonGroup();

    public RadioButtonGroup(List<T> options, T selected) {
        this.options = options;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (T option : options) {
            JRadioButton radioButton = new JRadioButton(option.toString(), option.equals(selected));
            add(radioButton);
            buttons.put(option, radioButton);
            buttonGroup.add(radioButton);
        }
    }

    public RadioButtonGroup(List<T> options) {
        this(options, options.get(0));
    }

    public T getSelected() {
        for (Map.Entry<T, JRadioButton> kv : buttons.entrySet()) {
            if (kv.getValue().isSelected())
                return kv.getKey();
        }
        return null;
    }

    public Map<T, JRadioButton> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }
}
