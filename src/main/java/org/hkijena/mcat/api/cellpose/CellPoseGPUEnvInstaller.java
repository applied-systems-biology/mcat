package org.hkijena.mcat.api.cellpose;

import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameter;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.scijava.Context;

import java.awt.Component;
import java.nio.file.Paths;

public class CellPoseGPUEnvInstaller extends CellPoseEnvInstaller {

    public CellPoseGPUEnvInstaller(Component parent, Context context, MCATParameterAccess parameterAccess) {
        super(parent, context, parameterAccess);
        this.setConfiguration(new Configuration());
        getConfiguration().setInstallationPath(Paths.get("mcat").resolve("cellpose-gpu").toAbsolutePath());
        getConfiguration().setName("Cellpose (GPU)");
    }

    @Override
    protected void postprocessInstall() {
        super.postprocessInstall();

        // Uninstall torch
        runConda("run", "--no-capture-output", "pip", "uninstall", "--yes", "torch");

        // Install CUDA + pytorch
        System.out.println("Starting with GPU library installation. This will take a long time.");
        runConda("install",
                "--yes",
                "pytorch",
                "cudatoolkit=" + ((Configuration) getConfiguration()).getCudaToolkitVersion(),
                "-c",
                "pytorch",
                "--force-reinstall");
    }

    public static class Configuration extends CellPoseEnvInstaller.Configuration {
        private String cudaToolkitVersion = "10.2";

        @MCATDocumentation(name = "CUDA Toolkit version", description = "The version of the CUDA toolkit that should be " +
                "installed. The correct version depends on the operating system. Please see here: https://pytorch.org/get-started/locally/")
        @MCATParameter("cuda-toolkit-version")
        public String getCudaToolkitVersion() {
            return cudaToolkitVersion;
        }

        @MCATParameter("cuda-toolkit-version")
        public void setCudaToolkitVersion(String cudaToolkitVersion) {
            this.cudaToolkitVersion = cudaToolkitVersion;
        }
    }
}
