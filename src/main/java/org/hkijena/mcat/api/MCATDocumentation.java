package org.hkijena.mcat.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used within ACAQ to annotate types and methods with documentation
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MCATDocumentation {
    /**
     * @return The name
     */
    String name() default "";

    /**
     * @return The description
     */
    String description() default "";
}
