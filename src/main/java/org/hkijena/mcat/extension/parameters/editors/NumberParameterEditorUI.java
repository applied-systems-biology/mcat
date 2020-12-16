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
import java.awt.Dimension;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.ui.parameters.MCATParameterEditorUI;
import org.scijava.Context;

/**
 * Editor for a {@link Double} parameter
 */
public class NumberParameterEditorUI extends MCATParameterEditorUI {
    private JSpinner spinner;
    private boolean skipNextReload = false;
    private boolean isReloading = false;

    /**
     * @param context         SciJava context
     * @param parameterAccess the parameter
     */
    public NumberParameterEditorUI(Context context, MCATParameterAccess parameterAccess) {
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
        spinner.setValue(getCurrentValue());
        isReloading = false;
    }

    private Number getCurrentValue() {
        Object value = getParameterAccess().get();
        if (getParameterAccess().getFieldClass() == byte.class || getParameterAccess().getFieldClass() == Byte.class) {
            return value != null ? (Byte) value : (byte) 0;
        } else if (getParameterAccess().getFieldClass() == short.class || getParameterAccess().getFieldClass() == Short.class) {
            return value != null ? (Short) value : (short) 0;
        } else if (getParameterAccess().getFieldClass() == int.class || getParameterAccess().getFieldClass() == Integer.class) {
            return value != null ? (Integer) value : 0;
        } else if (getParameterAccess().getFieldClass() == long.class || getParameterAccess().getFieldClass() == Long.class) {
            return value != null ? (Long) value : 0L;
        } else if (getParameterAccess().getFieldClass() == float.class || getParameterAccess().getFieldClass() == Float.class) {
            return value != null ? (Float) value : 0f;
        } else if (getParameterAccess().getFieldClass() == double.class || getParameterAccess().getFieldClass() == Double.class) {
            return value != null ? (Double) value : 0d;
        } else {
            throw new IllegalArgumentException("Unsupported numeric type: " + value);
        }
    }

    private Comparable<?> getMinimumValue() {
        NumberParameterSettings settings = getParameterAccess().getAnnotationOfType(NumberParameterSettings.class);
        if (settings != null) {
            return settings.min();
        }

        if (getParameterAccess().getFieldClass() == byte.class || getParameterAccess().getFieldClass() == Byte.class) {
            return Byte.MIN_VALUE;
        } else if (getParameterAccess().getFieldClass() == short.class || getParameterAccess().getFieldClass() == Short.class) {
            return Short.MIN_VALUE;
        } else if (getParameterAccess().getFieldClass() == int.class || getParameterAccess().getFieldClass() == Integer.class) {
            return Integer.MIN_VALUE;
        } else if (getParameterAccess().getFieldClass() == long.class || getParameterAccess().getFieldClass() == Long.class) {
            return Long.MIN_VALUE;
        } else if (getParameterAccess().getFieldClass() == float.class || getParameterAccess().getFieldClass() == Float.class) {
            return Float.NEGATIVE_INFINITY;
        } else if (getParameterAccess().getFieldClass() == double.class || getParameterAccess().getFieldClass() == Double.class) {
            return Double.NEGATIVE_INFINITY;
        } else {
            throw new IllegalArgumentException("Unsupported numeric type: " + getParameterAccess().getFieldClass());
        }
    }

    private Comparable<?> getMaximumValue() {
        NumberParameterSettings settings = getParameterAccess().getAnnotationOfType(NumberParameterSettings.class);
        if (settings != null) {
            return settings.max();
        }

        if (getParameterAccess().getFieldClass() == byte.class || getParameterAccess().getFieldClass() == Byte.class) {
            return Byte.MAX_VALUE;
        } else if (getParameterAccess().getFieldClass() == short.class || getParameterAccess().getFieldClass() == Short.class) {
            return Short.MAX_VALUE;
        } else if (getParameterAccess().getFieldClass() == int.class || getParameterAccess().getFieldClass() == Integer.class) {
            return Integer.MAX_VALUE;
        } else if (getParameterAccess().getFieldClass() == long.class || getParameterAccess().getFieldClass() == Long.class) {
            return Long.MAX_VALUE;
        } else if (getParameterAccess().getFieldClass() == float.class || getParameterAccess().getFieldClass() == Float.class) {
            return Float.POSITIVE_INFINITY;
        } else if (getParameterAccess().getFieldClass() == double.class || getParameterAccess().getFieldClass() == Double.class) {
            return Double.POSITIVE_INFINITY;
        } else {
            throw new IllegalArgumentException("Unsupported numeric type: " + getParameterAccess().getFieldClass());
        }
    }

    private double getStep() {
        NumberParameterSettings settings = getParameterAccess().getAnnotationOfType(NumberParameterSettings.class);
        if (settings != null) {
            return settings.step();
        }
        return 1;
    }

    private boolean setCurrentValue(Number number) {
        if (getParameterAccess().getFieldClass() == byte.class || getParameterAccess().getFieldClass() == Byte.class) {
            return getParameterAccess().set(number.byteValue());
        } else if (getParameterAccess().getFieldClass() == short.class || getParameterAccess().getFieldClass() == Short.class) {
            return getParameterAccess().set(number.shortValue());
        } else if (getParameterAccess().getFieldClass() == int.class || getParameterAccess().getFieldClass() == Integer.class) {
            return getParameterAccess().set(number.intValue());
        } else if (getParameterAccess().getFieldClass() == long.class || getParameterAccess().getFieldClass() == Long.class) {
            return getParameterAccess().set(number.longValue());
        } else if (getParameterAccess().getFieldClass() == float.class || getParameterAccess().getFieldClass() == Float.class) {
            return getParameterAccess().set(number.floatValue());
        } else if (getParameterAccess().getFieldClass() == double.class || getParameterAccess().getFieldClass() == Double.class) {
            return getParameterAccess().set(number.doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported numeric type: " + getParameterAccess().getFieldClass());
        }
    }

    private void initialize() {
        setLayout(new BorderLayout());
        SpinnerNumberModel model = new SpinnerNumberModel(getCurrentValue(), getMinimumValue(), getMaximumValue(), getStep());
        spinner = new JSpinner(model);
        spinner.addChangeListener(e -> {
            if (!isReloading) {
                skipNextReload = true;
                if (!setCurrentValue(model.getNumber())) {
                    skipNextReload = false;
                    reload();
                }
            }
        });
        ((JSpinner.NumberEditor)spinner.getEditor()).getTextField().setFormatterFactory(new DefaultFormatterFactory(new NumberEditorFormatter(model, NumberFormat.getInstance())));
        spinner.setPreferredSize(new Dimension(100, spinner.getPreferredSize().height));
        add(spinner, BorderLayout.CENTER);
    }

    private static class NumberEditorFormatter extends NumberFormatter {
        private final SpinnerNumberModel model;

        NumberEditorFormatter(SpinnerNumberModel model, NumberFormat format) {
            super(format);
            this.model = model;
            setValueClass(model.getValue().getClass());
        }

        public void setMinimum(Comparable min) {
            model.setMinimum(min);
        }

        public Comparable getMinimum() {
            return  model.getMinimum();
        }

        public void setMaximum(Comparable max) {
            model.setMaximum(max);
        }

        public Comparable getMaximum() {
            return model.getMaximum();
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if(value instanceof Integer && (int)value == Integer.MAX_VALUE) {
                return "Max";
            }
            else {
                return super.valueToString(value);
            }
        }

        @Override
        public Object stringToValue(String text) throws ParseException {
            if(Objects.equals(text, "Max")) {
                return Integer.MAX_VALUE;
            }
            else {
                return super.stringToValue(text);
            }
        }
    }
}
