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
 * Contains clustering hierarchy types
 */
public enum MCATClusteringHierarchy {
    AllInOne,
    PerTreatment,
    PerSubject;


    @Override
    public String toString() {
        switch (this) {
            case AllInOne:
                return "All in one";
            case PerTreatment:
                return "Per treatment";
            case PerSubject:
                return "Per subject";
            default:
                throw new RuntimeException();
        }
    }
}
