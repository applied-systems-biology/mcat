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
package org.hkijena.mcat.extension.parameters;


import org.hkijena.mcat.api.MCATClusteringHierarchy;
import org.hkijena.mcat.api.MCATDefaultDocumentation;
import org.hkijena.mcat.api.registries.MCATUIParametertypeRegistry;
import org.hkijena.mcat.extension.parameters.editors.*;
import org.hkijena.mcat.extension.parameters.generators.*;
import org.hkijena.mcat.ui.parameters.MCATParameterEditorUI;
import org.hkijena.mcat.ui.parameters.MCATParameterGeneratorUI;

import java.io.File;
import java.nio.file.Path;

/**
 * Provides some standard parameters
 */
public class StandardParameterEditorsExtension {

    private StandardParameterEditorsExtension() {

    }

    private static void registerParameterType(Class<?> parameterClass, Class<? extends MCATParameterEditorUI> uiClass, String name, String description) {
        MCATUIParametertypeRegistry.getInstance().registerParameterEditor(parameterClass, uiClass);
        MCATUIParametertypeRegistry.getInstance().registerDocumentation(parameterClass, new MCATDefaultDocumentation(name, description));
    }

    private static void registerParameterGenerator(Class<?> parameterClass, Class<? extends MCATParameterGeneratorUI> uiClass, String name, String description) {
        MCATUIParametertypeRegistry.getInstance().registerGenerator(parameterClass, uiClass, name, description);
    }

    public static void register() {

        // Register boolean
        registerParameterType(boolean.class, BooleanParameterEditorUI.class, "Boolean value", "A boolean value (true/false)");
        registerParameterType(Boolean.class, BooleanParameterEditorUI.class, "Boolean value", "A boolean value (true/false)");

        // Register numbers
        registerParameterType(byte.class, NumberParameterEditorUI.class, "8-bit integer number", "An 8-bit integer number ranging from " + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE);
        registerParameterType(short.class, NumberParameterEditorUI.class, "16-bit integer number", "A 16-bit integer number ranging from " + Short.MIN_VALUE + " to " + Short.MAX_VALUE);
        registerParameterType(int.class, NumberParameterEditorUI.class, "Integer number", "A 32-bit integer number ranging from " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE);
        registerParameterType(long.class, NumberParameterEditorUI.class, "64-bit integer number", "A 64-bit integer number ranging from " + Long.MIN_VALUE + " to " + Long.MAX_VALUE);
        registerParameterType(float.class, NumberParameterEditorUI.class, "Floating point number (single)", "A floating point number with single precision");
        registerParameterType(double.class, NumberParameterEditorUI.class, "Floating point number (double)", "A floating point number with double precision");
        registerParameterType(Byte.class, NumberParameterEditorUI.class, "8-bit integer number", "A 8-bit integral number ranging from " + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE);
        registerParameterType(Short.class, NumberParameterEditorUI.class, "16-bit integer number", "A 16-bit integral number ranging from " + Short.MIN_VALUE + " to " + Short.MAX_VALUE);
        registerParameterType(Integer.class, NumberParameterEditorUI.class, "Integer number", "An integer number ranging from " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE);
        registerParameterType(Long.class, NumberParameterEditorUI.class, "64-bit integer number", "A 64-bit integer number ranging from " + Long.MIN_VALUE + " to " + Long.MAX_VALUE);
        registerParameterType(Float.class, NumberParameterEditorUI.class, "Floating point number (single)", "A floating point number with single precision");
        registerParameterType(Double.class, NumberParameterEditorUI.class, "Floating point number (double)", "A floating point number with double precision");

        // Register other common Java classes
        registerParameterType(Enum.class, EnumParameterEditorUI.class, null, "A selection of different values");
        registerParameterType(String.class, StringParameterEditorUI.class, "String", "A text value");
        registerParameterType(Path.class, FilePathParameterEditorUI.class, "Filesystem path", "A file path");
        registerParameterType(File.class, FileParameterEditorUI.class, "Filesystem path (legacy)", "A file path (legacy)");

        // Register MCAT parameters
        registerParameterType(MCATClusteringHierarchy.class, EnumParameterEditorUI.class, "Clustering hierarchy", "Determines how data is organized for clustering");

        // Register generators
        registerParameterGenerator(byte.class, ByteParameterGenerator.class, "Generate 8-bit integral number sequence", "Generates 8-bit integer numbers");
        registerParameterGenerator(short.class, ShortParameterGenerator.class, "Generate 16-bit integral number sequence", "Generates 16-bit integer numbers");
        registerParameterGenerator(int.class, IntegerParameterGenerator.class, "Generate 32-bit integral number sequence", "Generates 32-bit integer numbers");
        registerParameterGenerator(long.class, LongParameterGenerator.class, "Generate 64-bit integral number sequence", "Generates 64-bit integer numbers");
        registerParameterGenerator(float.class, FloatParameterGenerator.class, "Generate single precision floating point number sequence", "Generates 32-bit floating point numbers");
        registerParameterGenerator(double.class, DoubleParameterGenerator.class, "Generate double precision floating point number sequence", "Generates 64-bit floating point numbers");
        registerParameterGenerator(Byte.class, ByteParameterGenerator.class, "Generate 8-bit integral number sequence", "Generates 8-bit integer numbers");
        registerParameterGenerator(Short.class, ShortParameterGenerator.class, "Generate 16-bit integral number sequence", "Generates 16-bit integer numbers");
        registerParameterGenerator(Integer.class, IntegerParameterGenerator.class, "Generate 32-bit integral number sequence", "Generates 32-bit integer numbers");
        registerParameterGenerator(Long.class, LongParameterGenerator.class, "Generate 64-bit integral number sequence", "Generates 64-bit integer numbers");
        registerParameterGenerator(Float.class, FloatParameterGenerator.class, "Generate single precision floating point number sequence", "Generates 32-bit floating point numbers");
        registerParameterGenerator(Double.class, DoubleParameterGenerator.class, "Generate double precision floating point number sequence", "Generates 64-bit floating point numbers");
    }
}
