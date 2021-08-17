package org.hkijena.mcat.api.cellpose;

import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameter;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.extension.parameters.editors.FilePathParameterSettings;
import org.hkijena.mcat.extension.parameters.editors.StringParameterSettings;
import org.hkijena.mcat.extension.parameters.optional.OptionalPathParameter;
import org.hkijena.mcat.ui.components.FileSelection;
import org.hkijena.mcat.ui.components.MarkdownDocument;
import org.hkijena.mcat.ui.parameters.ParameterPanel;
import org.hkijena.mcat.utils.PythonEnvironment;
import org.hkijena.mcat.utils.PythonEnvironmentType;
import org.scijava.Context;


import javax.swing.*;
import java.awt.Component;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class SelectCondaEnvPythonInstaller implements Runnable {

    private final Component parent;
    private final Context context;
    private final MCATParameterAccess parameterAccess;
    private PythonEnvironment generatedEnvironment;

    /**
     * @param parent
     * @param parameterAccess the parameter access that will receive the generated environment
     */
    public SelectCondaEnvPythonInstaller(Component parent, Context context, MCATParameterAccess parameterAccess) {
        this.parent = parent;
        this.context = context;
        this.parameterAccess = parameterAccess;
    }

    public PythonEnvironment getInstalledEnvironment() {
        return generatedEnvironment;
    }

    @Override
    public void run() {
        AtomicBoolean windowOpened = new AtomicBoolean(true);
        AtomicBoolean userCancelled = new AtomicBoolean(true);
        Configuration configuration = new Configuration();
        Object lock = new Object();

        synchronized (lock) {
            SwingUtilities.invokeLater(() -> {
                boolean result = ParameterPanel.showDialog(parent, context, configuration, new MarkdownDocument("# Conda environment\n\n" +
                                "Please choose the Conda executable and the environment name."), "Select Conda environment",
                        ParameterPanel.NO_GROUP_HEADERS | ParameterPanel.WITH_DOCUMENTATION | ParameterPanel.WITH_SCROLLING);
                userCancelled.set(!result);
                windowOpened.set(false);
                synchronized (lock) {
                    lock.notify();
                }
            });
            try {
                while (windowOpened.get()) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (userCancelled.get())
            return;

        generatedEnvironment = createCondaEnvironment(configuration);
        if (getParameterAccess() != null) {
            SwingUtilities.invokeLater(() -> {
                if (getParameterAccess().getFieldClass().isAssignableFrom(generatedEnvironment.getClass())) {
                    getParameterAccess().set(generatedEnvironment);
                }
            });
        }
    }

    public MCATParameterAccess getParameterAccess() {
        return parameterAccess;
    }

    public static PythonEnvironment createCondaEnvironment(Configuration configuration) {
        PythonEnvironment generatedEnvironment = new PythonEnvironment();
        generatedEnvironment.setType(PythonEnvironmentType.Conda);
        generatedEnvironment.setExecutablePath(configuration.condaExecutable);
        return generatedEnvironment;
    }

    public static class Configuration implements MCATParameterCollection {
        private final EventBus eventBus = new EventBus();
        private Path condaExecutable = Paths.get("");
        private String environmentName = "base";
        private OptionalPathParameter overrideEnvironment = new OptionalPathParameter();
        private String name = "Conda";

        @Override
        public EventBus getEventBus() {
            return eventBus;
        }

        @MCATDocumentation(name = "Conda executable", description = "The conda executable. Located in the Miniconda/Anaconda folder. On Windows it is inside the Scripts directory.")
        @FilePathParameterSettings(ioMode = FileSelection.IOMode.Open, pathMode = FileSelection.PathMode.FilesOnly)
        @MCATParameter("conda-executable")
        public Path getCondaExecutable() {
            return condaExecutable;
        }

        @MCATParameter("conda-executable")
        public void setCondaExecutable(Path condaExecutable) {
            this.condaExecutable = condaExecutable;
        }

        @MCATDocumentation(name = "Environment name", description = "The name of the selected Conda environment")
        @MCATParameter("environment-name")
        @StringParameterSettings(monospace = true)
        public String getEnvironmentName() {
            return environmentName;
        }

        @MCATParameter("environment-name")
        public void setEnvironmentName(String environmentName) {
            this.environmentName = environmentName;
        }

        @MCATDocumentation(name = "Override environment path", description = "Alternative to using an environment name. You can provide " +
                "the environment directory here.")
        @FilePathParameterSettings(ioMode = FileSelection.IOMode.Open, pathMode = FileSelection.PathMode.DirectoriesOnly)
        @MCATParameter("override-environment")
        public OptionalPathParameter getOverrideEnvironment() {
            return overrideEnvironment;
        }

        @MCATParameter("override-environment")
        public void setOverrideEnvironment(OptionalPathParameter overrideEnvironment) {
            this.overrideEnvironment = overrideEnvironment;
        }

        @MCATDocumentation(name = "Name", description = "Name of the created environment")
        @MCATParameter("name")
        public String getName() {
            return name;
        }

        @MCATParameter("name")
        public void setName(String name) {
            this.name = name;
        }
    }
}
