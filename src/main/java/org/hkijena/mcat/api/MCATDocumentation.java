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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used within MCAT to annotate types and methods with documentation
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MCATDocumentation {
    /**
     * @return The name
     */
    String name() default "";

    /**
     * Either a string that is used as description or a link to a Markdown resource that contains the description.
     * To load a description from Markdown, the string must begin with res://, following an absolute resource path
     *
     * @return The description
     */
    String description() default "";
}
