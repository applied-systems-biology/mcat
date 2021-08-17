package org.hkijena.mcat.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.eventbus.EventBus;
import ij.IJ;
import ij.Prefs;
import org.hkijena.mcat.api.parameters.MCATParameter;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.utils.JsonUtils;
import org.hkijena.mcat.utils.PythonEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MCATSettings implements MCATParameterCollection {
    private static MCATSettings INSTANCE;
    private final EventBus eventBus = new EventBus();
    private PythonEnvironment cellposeEnvironment = new PythonEnvironment();

    @MCATDocumentation(name = "Cellpose Python environment", description = "Use following settings to customize how MCAT runs Cellpose.")
    @MCATParameter("cellpose-environment")
    @JsonGetter("cellpose-environment")
    public PythonEnvironment getCellposeEnvironment() {
        return cellposeEnvironment;
    }

    @JsonSetter("cellpose-environment")
    public void setCellposeEnvironment(PythonEnvironment cellposeEnvironment) {
        this.cellposeEnvironment = cellposeEnvironment;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Gets the raw property files Json node
     *
     * @return the node. Never null.
     */
    public static JsonNode getRawNode() {
        Path propertyFile = getPropertyFile();
        if (Files.exists(propertyFile)) {
            try {
                return JsonUtils.getObjectMapper().readTree(propertyFile.toFile());
            } catch (IOException e) {
                return MissingNode.getInstance();
            }
        }
        return MissingNode.getInstance();
    }

    /**
     * @return The location of the file where the settings are stored
     */
    public static Path getPropertyFile() {
        Path imageJDir = Paths.get(Prefs.getImageJDir());
        if (!Files.isDirectory(imageJDir)) {
            try {
                Files.createDirectories(imageJDir);
            } catch (IOException e) {
                IJ.handleException(e);
            }
        }
        return imageJDir.resolve("mcat.properties.json");
    }

    public static void  reloadProperties() {
        if(Files.isRegularFile(getPropertyFile())) {
            try {
                INSTANCE = JsonUtils.getObjectMapper().readValue(getPropertyFile().toFile(), MCATSettings.class);
            }
            catch (Exception e) {
                INSTANCE = new MCATSettings();
            }
        }
        else {
            INSTANCE = new MCATSettings();
        }
    }

    public static void saveProperties() {
        try {
            JsonUtils.getObjectMapper().writeValue(getPropertyFile().toFile(), getInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MCATSettings getInstance() {
        if(INSTANCE == null) {
            if(Files.isRegularFile(getPropertyFile())) {
                try {
                    INSTANCE = JsonUtils.getObjectMapper().readValue(getPropertyFile().toFile(), MCATSettings.class);
                }
                catch (Exception e) {
                    INSTANCE = new MCATSettings();
                }
            }
            else {
                INSTANCE = new MCATSettings();
            }
        }
        return INSTANCE;
    }
}
