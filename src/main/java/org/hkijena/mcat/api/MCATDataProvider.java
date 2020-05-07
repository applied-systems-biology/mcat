package org.hkijena.mcat.api;

import org.hkijena.mcat.utils.api.ACAQValidatable;

/**
 * An interface that generates data
 */
public interface MCATDataProvider extends ACAQValidatable {
    /**
     * Gets the data
     *
     * @return the data
     */
    MCATData get();

    /**
     * Duplicates the provider
     *
     * @return the copy
     */
    MCATDataProvider duplicate();

    /**
     * Returns true if the parameters are valid and data can be loaded
     *
     * @return if the parameters are valid and data can be loaded
     */
    boolean isValid();
}
