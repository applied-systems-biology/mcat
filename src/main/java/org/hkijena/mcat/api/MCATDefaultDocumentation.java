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
