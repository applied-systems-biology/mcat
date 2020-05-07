package org.hkijena.mcat.utils.extension.parameters.generators;

import org.scijava.Context;

/**
 * Generates {@link Short}
 */
public class ShortParameterGenerator extends NumberParameterGenerator {

    /**
     * Creates a new instance
     *
     * @param context the SciJava context
     */
    public ShortParameterGenerator(Context context) {
        super(context, Short.class);
    }
}
