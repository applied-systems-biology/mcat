package org.hkijena.mcat.utils.api.parameters;

import java.util.*;
import java.util.stream.Collectors;

/**
 * If a class inherits from this interface, reflection-based parameter discovery is replaced by
 * the getCustomParameters() method result.
 */
public interface ACAQCustomParameterCollection extends ACAQParameterCollection {
    /**
     * Returns all parameters
     *
     * @return Map from parameter ID to its access. The ID is not necessarily equal to {@link ACAQParameterAccess}.getKey()
     */
    Map<String, ACAQParameterAccess> getParameters();

    /**
     * Generates a parameter string from parameters.
     * @param parameters The parameters
     * @param separator String that separates entries (e.g. ',')
     * @param equals String that separates key and values (e.g. '=')
     * @return Parameter string
     */
    static String parametersToString(Collection<ACAQParameterAccess> parameters, String separator, String equals) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (ACAQParameterAccess access : parameters.stream().sorted(Comparator.comparing(ACAQParameterAccess::getShortKey)).collect(Collectors.toList())) {
            if(first)
                stringBuilder.append(separator);
            first = false;
            stringBuilder.append(access.getShortKey()).append(equals).append(("" + (Object)access.get()));
        }
        return stringBuilder.toString();
    }
}
