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
