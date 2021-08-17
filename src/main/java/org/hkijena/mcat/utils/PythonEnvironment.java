package org.hkijena.mcat.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameter that describes a Python environment
 */
public class PythonEnvironment {
    private PythonEnvironmentType type = PythonEnvironmentType.System;
    private Path executablePath = Paths.get("");
    private Map<String, String> environmentVariables = new HashMap<>();
    private String condaEnvironment = "base";

    public PythonEnvironment() {

    }

    public PythonEnvironment(PythonEnvironmentType type, Path executablePath) {
        this.type = type;
        this.executablePath = executablePath;
    }

    public PythonEnvironment(PythonEnvironment other) {
        this.type = other.type;
        this.executablePath = other.executablePath;
        this.environmentVariables = new HashMap<>(other.environmentVariables);
        this.condaEnvironment = other.condaEnvironment;
    }

    public String getCondaEnvironment() {
        return condaEnvironment;
    }

    public void setCondaEnvironment(String condaEnvironment) {
        this.condaEnvironment = condaEnvironment;
    }

    public PythonEnvironmentType getType() {
        return type;
    }

    public void setType(PythonEnvironmentType type) {
        this.type = type;
    }

    public Path getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(Path executablePath) {
        this.executablePath = executablePath;
    }

    public Path getAbsoluteExecutablePath() {
        return PathUtils.relativeToImageJToAbsolute(getExecutablePath());
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }
}
