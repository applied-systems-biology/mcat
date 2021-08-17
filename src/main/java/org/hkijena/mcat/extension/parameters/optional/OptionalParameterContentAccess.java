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

package org.hkijena.mcat.extension.parameters.optional;

import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATParameterVisibility;

import java.lang.annotation.Annotation;


public class OptionalParameterContentAccess<T> implements MCATParameterAccess {
    private MCATParameterAccess parent;
    private OptionalParameter<T> optionalParameter;

    /**
     * Creates a new instance
     *
     * @param parent            the parent access
     * @param optionalParameter the parameter
     */
    public OptionalParameterContentAccess(MCATParameterAccess parent, OptionalParameter<T> optionalParameter) {
        this.parent = parent;
        this.optionalParameter = optionalParameter;
    }

    public OptionalParameter<T> getOptionalParameter() {
        return optionalParameter;
    }

    @Override
    public String getKey() {
        return "content";
    }

    @Override
    public String getName() {
        return "Content";
    }

    @Override
    public String getDescription() {
        return "Parameter content";
    }

    @Override
    public MCATParameterVisibility getVisibility() {
        return null;
    }

    @Override
    public <U extends Annotation> U getAnnotationOfType(Class<U> klass) {
        return parent.getAnnotationOfType(klass);
    }

    @Override
    public Class<?> getFieldClass() {
        return optionalParameter.getContentClass();
    }

    @Override
    public <T> T get() {
        return (T) optionalParameter.getContent();
    }


    @Override
    public <U> boolean set(U value) {
        optionalParameter.setContent((T) value);
        return true;
    }

    @Override
    public MCATParameterCollection getSource() {
        return parent.getSource();
    }

    @Override
    public double getPriority() {
        return 0;
    }

    @Override
    public String getShortKey() {
        return null;
    }

    @Override
    public int getUIOrder() {
        return 0;
    }

    public MCATParameterAccess getParent() {
        return parent;
    }
}
