package org.hkijena.mcat.extension.parameters.generators;

import org.scijava.Context;

/**
 * Generates {@link Long}
 */
public class LongParameterGenerator extends NumberParameterGenerator {

    /**
     * Creates a new instance
     *
     * @param context the SciJava context
     */
    public LongParameterGenerator(Context context) {
        super(context, Long.class);
    }
}
