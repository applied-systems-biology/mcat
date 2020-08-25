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
package org.hkijena.mcat.api.parameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.utils.StringUtils;

/**
 * {@link MCATParameterAccess} generated from reflection
 */
public class MCATReflectionParameterAccess implements MCATParameterAccess {

    private String key;
    private String shortKey;
    private int uiOrder;
    private Method getter;
    private Method setter;
    private double priority;
    private MCATDocumentation documentation;
    private MCATParameterVisibility visibility = MCATParameterVisibility.TransitiveVisible;
    private MCATParameterCollection source;

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getName() {
        if (getDocumentation() != null)
            return getDocumentation().name();
        return key;
    }

    @Override
    public String getDescription() {
        if (getDocumentation() != null)
            return getDocumentation().description();
        return null;
    }

    /**
     * @return Documentation of this parameter
     */
    public MCATDocumentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(MCATDocumentation documentation) {
        this.documentation = documentation;
    }

    @Override
    public <T extends Annotation> T getAnnotationOfType(Class<T> klass) {
        T getterAnnotation = getter.getAnnotation(klass);
        if (getterAnnotation != null)
            return getterAnnotation;
        return setter.getAnnotation(klass);
    }

    @Override
    public Class<?> getFieldClass() {
        return getter.getReturnType();
    }

    @Override
    public <T> T get() {
        try {
            return (T) getter.invoke(source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> boolean set(T value) {
        try {
            Object result = setter.invoke(source, value);
            if (result instanceof Boolean) {
                return (boolean) result;
            } else {
                return true;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MCATParameterCollection getSource() {
        return source;
    }

    public void setSource(MCATParameterCollection source) {
        this.source = source;
    }

    @Override
    public MCATParameterVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(MCATParameterVisibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    @Override
    public String getShortKey() {
        return !StringUtils.isNullOrEmpty(shortKey) ? shortKey : getKey();
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }

    @Override
    public int getUIOrder() {
        return uiOrder;
    }

    public void setUIOrder(int uiOrder) {
        this.uiOrder = uiOrder;
    }
}
