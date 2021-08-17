package org.hkijena.mcat.utils;

/**
 * The supported Python environment types
 */
public enum PythonEnvironmentType {
    System,
    VirtualEnvironment,
    Conda;


    @Override
    public String toString() {
        if (this == VirtualEnvironment)
            return "Virtual environment";
        else
            return this.name();
    }
}
