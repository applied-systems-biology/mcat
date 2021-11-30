package org.hkijena.mcat.api.cellpose;

import java.awt.Component;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameter;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.extension.parameters.editors.StringParameterSettings;
import org.hkijena.mcat.extension.parameters.optional.OptionalPathParameter;
import org.hkijena.mcat.extension.parameters.optional.OptionalStringParameter;
import org.hkijena.mcat.ui.components.MarkdownDocument;
import org.hkijena.mcat.ui.parameters.ParameterPanel;
import org.hkijena.mcat.utils.PathUtils;
import org.hkijena.mcat.utils.PythonEnvironment;
import org.hkijena.mcat.utils.PythonUtils;
import org.hkijena.mcat.utils.WebUtils;
import org.scijava.Context;

import com.google.common.eventbus.EventBus;

public class BasicMinicondaEnvPythonInstaller implements Runnable {

    private final Component parent;
    private final Context context;
    private final MCATParameterAccess parameterAccess;
    private Configuration configuration = new Configuration();
    private PythonEnvironment generatedEnvironment;
    
    public BasicMinicondaEnvPythonInstaller(Component parent, Context context, MCATParameterAccess parameterAccess) {
        this.parent = parent;
        this.context = context;
        this.parameterAccess = parameterAccess;
    }

    public MCATParameterAccess getParameterAccess() {
        return parameterAccess;
    }

    public String getTaskLabel() {
        return "Install Conda";
    }

    public PythonEnvironment getInstalledEnvironment() {
        return generatedEnvironment;
    }

    @Override
    public void run() {

        // Config phase
        if (!configure()) return;

        // Cleanup phase
        Path installationPath = PathUtils.relativeToImageJToAbsolute(getConfiguration().getInstallationPath());
        if (Files.exists(installationPath)) {
            System.out.println("Deleting old installation");
            System.out.println("Deleting: " + installationPath);
            PathUtils.deleteDirectoryRecursively(installationPath);
        }
        if (!Files.isDirectory(installationPath)) {
            try {
                Files.createDirectories(installationPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Download phase
        System.out.println("Acquire setup ...");
        Path installerPath = download();

        // Install phase
        System.out.println("Install ...");
        install(installerPath);

        // Postprocess phase
        System.out.println("Postprocess install ...");
        postprocessInstall();

        // Generate phase
        System.out.println("Generating config ...");
        SelectCondaEnvPythonInstaller.Configuration condaConfig = generateCondaConfig();

        generatedEnvironment = SelectCondaEnvPythonInstaller.createCondaEnvironment(condaConfig);
        if (getParameterAccess() != null) {
            SwingUtilities.invokeLater(() -> {
                if (getParameterAccess().getFieldClass().isAssignableFrom(generatedEnvironment.getClass())) {
                    getParameterAccess().set(generatedEnvironment);
                }
            });
        }
    }

    /**
     * Runs the conda executable (for postprocessing)
     *
     * @param args arguments
     */
    public void runConda(String... args) {
        CommandLine commandLine = new CommandLine(getCondaExecutableInInstallationPath().toFile());
        for (String arg : args) {
            commandLine.addArgument(arg);
        }

        // We must add Library/bin to Path. Otherwise, there SSL won't work
        Map<String, String> environmentVariables = new HashMap<>();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            environmentVariables.put(entry.getKey(), entry.getValue());
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            environmentVariables.put("Path", getConfiguration().getInstallationPath().resolve("Library").resolve("bin").toAbsolutePath() + ";" +
                    environmentVariables.getOrDefault("Path", ""));
        }

        DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
        PythonUtils.setupLogger(commandLine, executor);

        // Set working directory, so conda can see its DLLs
        executor.setWorkingDirectory(getCondaExecutableInInstallationPath().toAbsolutePath().getParent().toFile());

        try {
            executor.execute(commandLine, environmentVariables);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Applies postprocessing for the installation
     */
    protected void postprocessInstall() {
        if (getConfiguration().getForcePythonVersion().isEnabled()) {
            runConda("install", "--yes", "python=" + getConfiguration().getForcePythonVersion().getContent());
        }
    }

    /**
     * Returns the path to the conda executable within the configured installation directory
     *
     * @return the conda path
     */
    public Path getCondaExecutableInInstallationPath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return getConfiguration().getInstallationPath().resolve("Scripts").resolve("conda.exe");
        } else {
            return getConfiguration().getInstallationPath().resolve("bin").resolve("conda");
        }
    }

    /**
     * Generates the configuration for the conda environment
     *
     * @return the config
     */
    protected SelectCondaEnvPythonInstaller.Configuration generateCondaConfig() {
        SelectCondaEnvPythonInstaller.Configuration condaConfig = new SelectCondaEnvPythonInstaller.Configuration();
        if (SystemUtils.IS_OS_WINDOWS) {
            condaConfig.setCondaExecutable(getConfiguration().getInstallationPath().resolve("Scripts").resolve("conda.exe"));
        } else {
            condaConfig.setCondaExecutable(getConfiguration().getInstallationPath().resolve("bin").resolve("conda"));
        }
        condaConfig.setEnvironmentName("base");
        condaConfig.setName(getConfiguration().getName());
        return condaConfig;
    }

    /**
     * Installs Miniconda
     *
     * @param installerPath the setup
     */
    protected void install(Path installerPath) {
        if (SystemUtils.IS_OS_WINDOWS) {
            installMinicondaWindows(installerPath);
        } else {
            installMinicondaLinuxMac(installerPath);
        }
    }

    /**
     * Downloads the installer
     *
     * @return the installer path
     */
    protected Path download() {
        Path installerPath;
        if (configuration.getCustomInstallerPath().isEnabled())
            installerPath = configuration.getCustomInstallerPath().getContent();
        else
            installerPath = downloadMiniconda();
        return installerPath;
    }

    /**
     * UI configuration
     *
     * @return false if the operation was cancelled
     */
    protected boolean configure() {
        AtomicBoolean windowOpened = new AtomicBoolean(true);
        AtomicBoolean userCancelled = new AtomicBoolean(true);
        Object lock = new Object();

        System.out.println("Waiting for user input ...");
        synchronized (lock) {
            SwingUtilities.invokeLater(() -> {
                boolean result = ParameterPanel.showDialog(parent, context, configuration, new MarkdownDocument("# Install Miniconda\n\n" +
                                "Please review the settings on the left-hand side. Click OK to install Miniconda.\n\n" +
                                "You have to agree to the following license: https://docs.conda.io/en/latest/license.html"), "Download & install Miniconda",
                        ParameterPanel.NO_GROUP_HEADERS | ParameterPanel.WITH_DOCUMENTATION | ParameterPanel.WITH_SCROLLING);
                if (result && Files.exists(getConfiguration().installationPath)) {
                    if (JOptionPane.showConfirmDialog(parent, "The directory " + getConfiguration().getInstallationPath().toAbsolutePath()
                            + " already exists. Do you want to overwrite it?", getTaskLabel(), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        result = false;
                    }
                }
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

        return !userCancelled.get();
    }

    private void installMinicondaLinuxMac(Path installerPath) {
        try {
            Files.setPosixFilePermissions(installerPath, PosixFilePermissions.fromString("rwxrwxr-x"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LogOutputStream progressInfoLog = new LogOutputStream() {
            @Override
            protected void processLine(String s, int i) {
                System.out.println(s);
            }
        };

        System.out.println("Installation path: " + configuration.installationPath.toAbsolutePath());
        System.out.println("Please note that you agreed to the Conda license: https://docs.conda.io/en/latest/license.html");
        CommandLine commandLine = new CommandLine(installerPath.toFile());
        commandLine.addArgument("-b");
        commandLine.addArgument("-f");
        commandLine.addArgument("-p");
        commandLine.addArgument(getConfiguration().installationPath.toAbsolutePath().toString());

        DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
        PythonUtils.setupLogger(commandLine, executor);

        try {
            executor.execute(commandLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void installMinicondaWindows(Path installerPath) {
        System.out.println("Miniconda is currently installing. Unfortunately, the installer provides no log.");

        LogOutputStream progressInfoLog = new LogOutputStream() {
            @Override
            protected void processLine(String s, int i) {
                System.out.println(s);
            }
        };

        System.out.println("Installation path: " + getConfiguration().installationPath.toAbsolutePath());
        System.out.println("Please note that you agreed to the Conda license: https://docs.conda.io/en/latest/license.html");
        CommandLine commandLine = new CommandLine(installerPath.toFile());
        commandLine.addArgument("/InstallationType=JustMe");
        commandLine.addArgument("/AddToPath=0");
        commandLine.addArgument("/RegisterPython=0");
        commandLine.addArgument("/S");
        commandLine.addArgument("/D=" + getConfiguration().installationPath.toAbsolutePath());

        DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
        PythonUtils.setupLogger(commandLine, executor);

        try {
            executor.execute(commandLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path downloadMiniconda() {
        Path targetFile = PathUtils.generateTempFile("conda", SystemUtils.IS_OS_WINDOWS ? ".exe" : ".sh");

        URL url;
        try {
            url = new URL(getConfiguration().getCondaDownloadURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        WebUtils.download(url, targetFile);
        return targetFile;
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Gets the latest download link for Miniconda
     *
     * @return the download URL
     */
    public static String getLatestDownload() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "https://repo.anaconda.com/miniconda/Miniconda3-latest-Windows-x86_64.exe";
        } else if (SystemUtils.IS_OS_MAC) {
            return "https://repo.anaconda.com/miniconda/Miniconda3-latest-MacOSX-x86_64.sh";
        } else {
            return "https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh";
        }
    }

    /**
     * Gets the latest download link for Miniconda
     *
     * @return the download URL
     */
    public static String getLatestPy37Download() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "https://repo.anaconda.com/miniconda/Miniconda3-py37_4.8.2-Windows-x86_64.exe";
        } else if (SystemUtils.IS_OS_MAC) {
            return "https://repo.anaconda.com/miniconda/Miniconda3-py37_4.8.2-MacOSX-x86_64.sh";
        } else {
            return "https://repo.anaconda.com/miniconda/Miniconda3-py37_4.8.2-Linux-x86_64.sh";
        }
    }

    public static class Configuration implements MCATParameterCollection {

        private final EventBus eventBus = new EventBus();
        private String condaDownloadURL = getLatestDownload();
        private Path installationPath;
        private OptionalPathParameter customInstallerPath = new OptionalPathParameter();
        private OptionalStringParameter forcePythonVersion = new OptionalStringParameter("3.7", false);
        private String name = "Conda";

        public Configuration() {
            installationPath = Paths.get("jipipe").resolve("miniconda").toAbsolutePath();
        }

        @Override
        public EventBus getEventBus() {
            return eventBus;
        }

        @MCATDocumentation(name = "Download URL", description = "This URL is used to download Conda. If you change it, please ensure that URL " +
                "is the correct one for your current operating system. The Python version that is installed can be viewed here: https://docs.conda.io/en/latest/miniconda.html")
        @MCATParameter("conda-download-url")
        @StringParameterSettings(monospace = true)
        public String getCondaDownloadURL() {
            return condaDownloadURL;
        }

        @MCATParameter("conda-download-url")
        public void setCondaDownloadURL(String condaDownloadURL) {
            this.condaDownloadURL = condaDownloadURL;
        }

        @MCATDocumentation(name = "Installation path", description = "The folder where Miniconda is installed. Please choose an non-existing or empty folder.")
        @MCATParameter("installation-path")
        public Path getInstallationPath() {
            return installationPath;
        }

        @MCATParameter("installation-path")
        public void setInstallationPath(Path installationPath) {
            this.installationPath = installationPath;
        }

        @MCATDocumentation(name = "Use custom installer", description = "Instead of downloading Miniconda, use a custom installer executable.")
        @MCATParameter("custom-installer-path")
        public OptionalPathParameter getCustomInstallerPath() {
            return customInstallerPath;
        }

        @MCATParameter("custom-installer-path")
        public void setCustomInstallerPath(OptionalPathParameter customInstallerPath) {
            this.customInstallerPath = customInstallerPath;
        }

        @MCATDocumentation(name = "Specific Python version", description = "Allows to specify the Python version of the environment")
        @MCATParameter("force-python-version")
        public OptionalStringParameter getForcePythonVersion() {
            return forcePythonVersion;
        }

        @MCATParameter("force-python-version")
        public void setForcePythonVersion(OptionalStringParameter forcePythonVersion) {
            this.forcePythonVersion = forcePythonVersion;
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
