package org.hkijena.mcat.api;

import org.apache.commons.lang.reflect.ConstructorUtils;
import org.hkijena.mcat.utils.api.ACAQDocumentation;
import org.hkijena.mcat.utils.api.ACAQValidatable;

import java.beans.Expression;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * An interface that generates data
 */
public interface MCATDataProvider extends ACAQValidatable {
    /**
     * Gets the data
     * @return the data
     */
    MCATData get();

    /**
     * Duplicates the provider
     * @return the copy
     */
    MCATDataProvider duplicate();

    /**
     * Returns true if the parameters are valid and data can be loaded
     * @return if the parameters are valid and data can be loaded
     */
    boolean isValid();
}
