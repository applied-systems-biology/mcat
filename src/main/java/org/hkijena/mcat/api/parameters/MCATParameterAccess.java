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
package org.hkijena.mcat.api.parameters;

import java.lang.annotation.Annotation;

import org.scijava.Priority;

/**
 * Interface around accessing a parameter
 */
public interface MCATParameterAccess {

    /**
     * Returns the unique ID of this parameter
     *
     * @return Unique parameter key
     */
    String getKey();

    /**
     * Returns a short form of the ID used, for example to generate a parameter string.
     * Might return getKey() if none was provided
     *
     * @return a short form of the ID used, for example to generate a parameter string
     */
    String getShortKey();

    /**
     * Controls how the parameter is ordered within the user interface
     *
     * @return a low number indicates that this parameter is put first, while a high number indicates that this parameter is put last
     */
    int getUIOrder();

    /**
     * Returns the parameter name that is displayed to the user
     *
     * @return Parameter name
     */
    String getName();

    /**
     * Returns a description
     *
     * @return Parameter description
     */
    String getDescription();

    /**
     * Returns if the parameter should be visible to users or only stored to JSON
     *
     * @return Parameter visibility
     */
    MCATParameterVisibility getVisibility();

    /**
     * Finds an annotation for this parameter
     *
     * @param klass Annotation class
     * @param <T>   Annotation type
     * @return Annotation or null if not found
     */
    <T extends Annotation> T getAnnotationOfType(Class<T> klass);

    /**
     * Returns the parameter data type
     *
     * @return Parameter class
     */
    Class<?> getFieldClass();

    /**
     * Gets the parameter value
     *
     * @param <T> Parameter data type
     * @return Parameter value
     */
    <T> T get();

    /**
     * Sets the parameter value
     *
     * @param value Parameter value
     * @param <T>   Parameter data type
     * @return If setting the value was successful
     */
    <T> boolean set(T value);

    /**
     * Gets the object that holds the parameter
     *
     * @return the object that holds the parameter
     */
    MCATParameterCollection getSource();

    /**
     * Returns the priority for (de)serializing this parameter.
     * Please use the priority constants provided by {@link Priority}
     *
     * @return the priority
     */
    double getPriority();

    /**
     * Compares the priority
     *
     * @param lhs access
     * @param rhs access
     * @return the order
     */
    static int comparePriority(MCATParameterAccess lhs, MCATParameterAccess rhs) {
        return -Double.compare(lhs.getPriority(), rhs.getPriority());
    }
}
