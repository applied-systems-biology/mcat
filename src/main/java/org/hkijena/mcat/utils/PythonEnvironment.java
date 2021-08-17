package org.hkijena.mcat.utils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameter;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameter that describes a Python environment
 */
public class PythonEnvironment implements MCATParameterCollection {
    private final EventBus eventBus = new EventBus();
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

    @MCATDocumentation(name = "Conda environment", description = "Only if Conda: The Conda environment")
    @MCATParameter("conda-environment")
    @JsonGetter("conda-environment")
    public String getCondaEnvironment() {
        return condaEnvironment;
    }

    @MCATParameter("conda-environment")
    @JsonSetter("conda-environment")
    public void setCondaEnvironment(String condaEnvironment) {
        this.condaEnvironment = condaEnvironment;
    }

    @MCATDocumentation(name = "Environment type", description = "The type of Python environment")
    @MCATParameter("type")
    @JsonGetter("type")
    public PythonEnvironmentType getType() {
        return type;
    }

    @MCATParameter("type")
    @JsonSetter("type")
    public void setType(PythonEnvironmentType type) {
        this.type = type;
    }

    @MCATDocumentation(name = "Executable path", description = "The path to the Python executable. For Conda, this should be the Conda executable. Otherwise, this should be the Python executable.")
    @MCATParameter("executable-path")
    @JsonGetter("executable-path")
    public Path getExecutablePath() {
        return executablePath;
    }

    @MCATParameter("executable-path")
    @JsonSetter("executable-path")
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

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    public boolean isValid() {
        return Files.exists(getAbsoluteExecutablePath()) && Files.isRegularFile(getAbsoluteExecutablePath());
    }
}
