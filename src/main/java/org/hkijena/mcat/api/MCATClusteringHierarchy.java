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
