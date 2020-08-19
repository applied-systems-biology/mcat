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
 * An interface about a type that reports of the validity of its internal state
 */
public interface MCATValidatable {

    /**
     * Generates a validity report
     *
     * @param report the report to be added to
     */
    void reportValidity(MCATValidityReport report);

}
