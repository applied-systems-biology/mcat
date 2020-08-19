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
package org.hkijena.mcat.api;

/**
 * An interface that generates data
 */
public interface MCATDataProvider extends MCATValidatable {
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
