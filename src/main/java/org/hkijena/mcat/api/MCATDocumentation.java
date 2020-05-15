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
