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
package org.hkijena.mcat.ui.parameters;


import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.events.ParameterChangedEvent;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.scijava.Context;
import org.scijava.Contextual;

import javax.swing.*;
import java.util.Objects;

/**
 * A UI for a parameter type
 */
public abstract class MCATParameterEditorUI extends JPanel implements Contextual {
    private Context context;
    private MCATParameterAccess parameterAccess;

    /**
     * Creates new instance
     *
     * @param context         SciJava context
     * @param parameterAccess Parameter
     */
    public MCATParameterEditorUI(Context context, MCATParameterAccess parameterAccess) {
        this.context = context;
        this.parameterAccess = parameterAccess;
        parameterAccess.getSource().getEventBus().register(this);
    }

    /**
     * Gets the object that holds the parameter
     *
     * @return object that holds the parameter
     */
    public MCATParameterCollection getParameterHolder() {
        return parameterAccess.getSource();
    }

    /**
     * Gets the parameter accessor
     *
     * @return parameter accessor
     */
    public MCATParameterAccess getParameterAccess() {
        return parameterAccess;
    }

    /**
     * If true, the {@link ParameterPanel} will display a label with the parameter
     * name next to this UI.
     *
     * @return if label should be shown
     */
    public abstract boolean isUILabelEnabled();

    /**
     * Reloads the value from the stored parameter
     */
    public abstract void reload();

    /**
     * Listens for changes in parameters
     *
     * @param event Generated event
     */
    @Subscribe
    public void onParameterChanged(ParameterChangedEvent event) {
        if (Objects.equals(event.getKey(), parameterAccess.getKey())) {
            reload();
        }
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
        this.context.inject(this);
    }
}
