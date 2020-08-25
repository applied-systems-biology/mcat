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
package org.hkijena.mcat.extension.parameters.editors;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.ui.components.DocumentChangeListener;
import org.hkijena.mcat.ui.parameters.MCATParameterEditorUI;
import org.scijava.Context;

/**
 * Parameter editor for {@link String}
 */
public class StringParameterEditorUI extends MCATParameterEditorUI {

    private JTextComponent textComponent;
    private boolean skipNextReload = false;
    private boolean isReloading = false;

    /**
     * @param context         SciJava context
     * @param parameterAccess the parameter
     */
    public StringParameterEditorUI(Context context, MCATParameterAccess parameterAccess) {
        super(context, parameterAccess);
        initialize();
        reload();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        StringParameterSettings settings = getParameterAccess().getAnnotationOfType(StringParameterSettings.class);
        Object value = getParameterAccess().get();
        String stringValue = "";
        if (value != null) {
            stringValue = "" + value;
        }
        boolean monospaced = settings != null && settings.monospace();
        if (settings != null && settings.multiline()) {
            JTextArea textArea = new JTextArea(stringValue);
            textArea.setBorder(BorderFactory.createEtchedBorder());
            textComponent = textArea;
            add(textArea, BorderLayout.CENTER);
        } else {
            JTextField textField = new JTextField(stringValue);
            textComponent = textField;
            add(textField, BorderLayout.CENTER);
        }
        if (monospaced)
            textComponent.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        textComponent.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void changed(DocumentEvent documentEvent) {
                if (!isReloading) {
                    skipNextReload = true;
                    getParameterAccess().set(textComponent.getText());
                }
            }
        });
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
        Object value = getParameterAccess().get();
        String stringValue = "";
        if (value != null) {
            stringValue = "" + value;
        }
        textComponent.setText(stringValue);
        isReloading = false;
    }
}
