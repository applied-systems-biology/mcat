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

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * If a class inherits from this interface, reflection-based parameter discovery is replaced by
 * the getCustomParameters() method result.
 */
public interface MCATCustomParameterCollection extends MCATParameterCollection {
    /**
     * Returns all parameters
     *
     * @return Map from parameter ID to its access. The ID is not necessarily equal to {@link MCATParameterAccess}.getKey()
     */
    Map<String, MCATParameterAccess> getParameters();

    /**
     * Generates a parameter string from parameters.
     *
     * @param parameters The parameters
     * @param separator  String that separates entries (e.g. ',')
     * @param equals     String that separates key and values (e.g. '=')
     * @return Parameter string
     */
    static String parameterCollectionsToString(Collection<MCATParameterCollection> parameters, String separator, String equals) {
        return parametersToString((new MCATTraversedParameterCollection(parameters.toArray(new MCATParameterCollection[0])).getParameters().values()), separator, equals);
    }

    /**
     * Generates a parameter string from parameters.
     *
     * @param parameters The parameters
     * @param separator  String that separates entries (e.g. ',')
     * @param equals     String that separates key and values (e.g. '=')
     * @return Parameter string
     */
    static String parametersToString(Collection<MCATParameterAccess> parameters, String separator, String equals) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (MCATParameterAccess access : parameters.stream().sorted(Comparator.comparing(MCATParameterAccess::getShortKey)).collect(Collectors.toList())) {
            if (!first)
                stringBuilder.append(separator);
            first = false;
            stringBuilder.append(access.getShortKey()).append(equals).append(("" + (Object) access.get()));
        }
        return stringBuilder.toString();
    }
}
