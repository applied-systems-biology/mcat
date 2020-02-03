/*
 * Copyright by Ruman Gerst
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * This code is licensed under BSD 2-Clause
 * See the LICENSE file provided with this code for the full license.
 */

package org.hkijena.mcat.utils;

import java.util.Collection;

public class StringUtils {
    private StringUtils() {

    }

    /**
     * Adds a counter to a string to make the result unique
     * @param input
     * @param existing
     * @return
     */
    public static String makeUniqueString(String input, Collection<String> existing) {
        if(!existing.contains(input))
            return input;
        int index = 1;
        while(existing.contains(input + " " + index)) {
            ++index;
        }
        return input + " " + index;
    }

    /**
     * Capitalizes the first letter in the string
     * @param input
     * @return
     */
    public static String capitalizeFirstLetter(String input) {
        if(input == null || input.length() < 2) {
            return input;
        }
        else {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }
    }
}
