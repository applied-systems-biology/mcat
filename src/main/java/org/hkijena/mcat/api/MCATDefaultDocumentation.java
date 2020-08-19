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

import java.lang.annotation.Annotation;

/**
 * Default implementation of {@link MCATDocumentation}
 */
public class MCATDefaultDocumentation implements MCATDocumentation {
    private final String name;
    private final String description;

    /**
     * Creates a new instance
     *
     * @param name        The name
     * @param description The description
     */
    public MCATDefaultDocumentation(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return MCATDocumentation.class;
    }
}
