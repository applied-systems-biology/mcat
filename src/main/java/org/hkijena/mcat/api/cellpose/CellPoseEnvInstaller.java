package org.hkijena.mcat.api.cellpose;

import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameter;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.utils.PathUtils;
import org.hkijena.mcat.utils.WebUtils;
import org.scijava.Context;

import java.awt.Component;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CellPoseEnvInstaller extends BasicMinicondaEnvPythonInstaller {
   
    public CellPoseEnvInstaller(Component parent, Context context, MCATParameterAccess parameterAccess) {
        super(parent, context, parameterAccess);
        setConfiguration(new Configuration());
        getConfiguration().setInstallationPath(Paths.get("mcat").resolve("cellpose-cpu"));
        getConfiguration().setName("Cellpose (CPU)");
    }

    @Override
    public String getTaskLabel() {
        return "Install Cellpose";
    }

    @Override
    protected void postprocessInstall() {
        super.postprocessInstall();

        // We need to create the environment according to the CellPose tutorial https://github.com/MouseLand/cellpose
        Path environmentDefinitionPath = downloadEnvironment();

        // Apply the environment
        runConda("env", "update", "--file", environmentDefinitionPath.toAbsolutePath().toString());

        // Upgrade cellpose (pip)
        runConda("run", "--no-capture-output", "pip", "install", "cellpose", "--upgrade");
        runConda("run", "--no-capture-output", "pip", "install", "cellpose[gui]");

        // Download models
        if (((Configuration) getConfiguration()).isDownloadModels()) {
            runConda("run", "--no-capture-output", "python", "-u", "-c", "from cellpose import models; models.download_model_weights()");
        }
    }

    private Path downloadEnvironment() {
        Path path = PathUtils.generateTempFile("environment", ".yml");
        try {
            WebUtils.download(new URL("https://raw.githubusercontent.com/MouseLand/cellpose/master/environment.yml"),
                    path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Renaming 'cellpose' to 'base' due to bug in conda run and conda env");
        try {
            String contents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            contents = contents.replace("name: cellpose", "name: base");
            Files.write(path, contents.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    public static class Configuration extends BasicMinicondaEnvPythonInstaller.Configuration {
        private boolean downloadModels = true;

        @MCATDocumentation(name = "Download models", description = "If enabled, models will also be downloaded. " +
                "Otherwise, Cellpose might download the models during its first run.")
        @MCATParameter("download-models")
        public boolean isDownloadModels() {
            return downloadModels;
        }

        @MCATParameter("download-models")
        public void setDownloadModels(boolean downloadModels) {
            this.downloadModels = downloadModels;
        }
    }
}
