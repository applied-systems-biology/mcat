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

public enum MCATPostprocessingMethod {
    MaxDecrease,
    MaxIncrease,
    NetDecrease,
    NetIncrease;


    @Override
    public String toString() {
        switch (this) {
            case MaxDecrease:
                return "maximum decrease";
            case MaxIncrease:
                return "maximum increase";
            case NetDecrease:
                return "net decrease";
            case NetIncrease:
                return "net increase";
            default:
                throw new RuntimeException();
        }
    }
}
