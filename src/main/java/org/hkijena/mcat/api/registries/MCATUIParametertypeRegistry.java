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
package org.hkijena.mcat.api.registries;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hkijena.mcat.api.MCATDefaultDocumentation;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.extension.parameters.StandardParameterEditorsExtension;
import org.hkijena.mcat.ui.parameters.MCATParameterEditorUI;
import org.hkijena.mcat.ui.parameters.MCATParameterGeneratorUI;
import org.scijava.Context;

/**
 * Registry for parameter types
 */
public class MCATUIParametertypeRegistry {

    private static MCATUIParametertypeRegistry instance;

    private Map<Class<?>, MCATDocumentation> parameterDocumentations = new HashMap<>();
    private Map<Class<?>, Class<? extends MCATParameterEditorUI>> parameterTypes = new HashMap<>();

    private Map<Class<?>, Set<Class<? extends MCATParameterGeneratorUI>>> parameterGenerators = new HashMap<>();
    private Map<Class<? extends MCATParameterGeneratorUI>, MCATDocumentation> parameterGeneratorDocumentations = new HashMap<>();

    /**
     * New instance
     */
    private MCATUIParametertypeRegistry() {

    }

    /**
     * Registers a new parameter type
     *
     * @param parameterType parameter type
     * @param uiClass       corresponding editor UI
     */
    public void registerParameterEditor(Class<?> parameterType, Class<? extends MCATParameterEditorUI> uiClass) {
        parameterTypes.put(parameterType, uiClass);
    }

    /**
     * Registers documentation for a parameter type
     *
     * @param parameterType parameter type
     * @param documentation the documentation
     */
    public void registerDocumentation(Class<?> parameterType, MCATDocumentation documentation) {
        parameterDocumentations.put(parameterType, documentation);
    }

    /**
     * Gets documentation for a parameter type
     *
     * @param parameterType parameter type
     * @return documentation. Can be null.
     */
    public MCATDocumentation getDocumentationFor(Class<?> parameterType) {
        return parameterDocumentations.getOrDefault(parameterType, null);
    }

    /**
     * Creates editor for the parameter
     *
     * @param context         SciJava context
     * @param parameterAccess the parameter
     * @return Parameter editor UI
     */
    public MCATParameterEditorUI createEditorFor(Context context, MCATParameterAccess parameterAccess) {
        Class<? extends MCATParameterEditorUI> uiClass = parameterTypes.getOrDefault(parameterAccess.getFieldClass(), null);
        if (uiClass == null) {
            // Search a matching one
            for (Map.Entry<Class<?>, Class<? extends MCATParameterEditorUI>> entry : parameterTypes.entrySet()) {
                if (entry.getKey().isAssignableFrom(parameterAccess.getFieldClass())) {
                    uiClass = entry.getValue();
                    break;
                }
            }
        }
        if (uiClass == null) {
            throw new NullPointerException("Could not find parameter editor for parameter class '" + parameterAccess.getFieldClass() + "'");
        }
        try {
            return uiClass.getConstructor(Context.class, MCATParameterAccess.class).newInstance(context, parameterAccess);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns true if there is an editor for the parameter
     *
     * @param parameterType the parameter type
     * @return if there is an editor for the parameter
     */
    public boolean hasEditorFor(Class<?> parameterType) {
        return parameterTypes.containsKey(parameterType);
    }

    /**
     * Registers a UI that can generate parameters
     *
     * @param parameterClass Parameter class
     * @param uiClass        The generator UI class
     * @param name           Generator name
     * @param description    Description for the generator
     */
    public void registerGenerator(Class<?> parameterClass, Class<? extends MCATParameterGeneratorUI> uiClass, String name, String description) {
        Set<Class<? extends MCATParameterGeneratorUI>> generators = parameterGenerators.getOrDefault(parameterClass, null);
        if (generators == null) {
            generators = new HashSet<>();
            parameterGenerators.put(parameterClass, generators);
        }
        generators.add(uiClass);
        parameterGeneratorDocumentations.put(uiClass, new MCATDefaultDocumentation(name, description));
    }

    /**
     * Returns all generators for the parameter class
     *
     * @param parameterClass the parameter class
     * @return Set of generators
     */
    public Set<Class<? extends MCATParameterGeneratorUI>> getGeneratorsFor(Class<?> parameterClass) {
        return parameterGenerators.getOrDefault(parameterClass, Collections.emptySet());
    }

    /**
     * Returns documentation for the generator
     *
     * @param generatorClass the generator
     * @return documentation
     */
    public MCATDocumentation getGeneratorDocumentationFor(Class<? extends MCATParameterGeneratorUI> generatorClass) {
        return parameterGeneratorDocumentations.get(generatorClass);
    }

    public static MCATUIParametertypeRegistry getInstance() {
        if (instance == null) {
            instance = new MCATUIParametertypeRegistry();
            StandardParameterEditorsExtension.register();
        }

        return instance;
    }
}
