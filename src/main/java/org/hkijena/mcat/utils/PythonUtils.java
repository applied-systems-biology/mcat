package org.hkijena.mcat.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.WordUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PythonUtils {

    private PythonUtils() {

    }

    public static void runPython(String code, PythonEnvironment environment) {
        System.out.println(code);
        Path codeFilePath = PathUtils.createTempFile("py", ".py");
        try {
            Files.write(codeFilePath, code.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        runPython(codeFilePath, environment);
    }

    public static void setupLogger(CommandLine commandLine, DefaultExecutor executor) {
        System.out.println("Running " + Arrays.stream(commandLine.toStrings()).map(s -> {
            if (s.contains(" ")) {
                return "\"" + PythonUtils.escapeString(s) + "\"";
            } else {
                return PythonUtils.escapeString(s);
            }
        }).collect(Collectors.joining(" ")));

        LogOutputStream progressInfoLog = new LogOutputStream() {
            @Override
            protected void processLine(String s, int i) {
                for (String s1 : s.split("\\r")) {
                    System.out.println(WordUtils.wrap(s1, 120));
                }
            }
        };
        executor.setStreamHandler(new PumpStreamHandler(progressInfoLog, progressInfoLog));
    }

    /**
     * Runs a Python script file
     *
     * @param scriptFile  the script file
     * @param environment the environment
     */
    public static void runPython(Path scriptFile, PythonEnvironment environment) {
        Path pythonExecutable = environment.getExecutablePath();
        CommandLine commandLine = new CommandLine(pythonExecutable.toFile());

        Map<String, String> environmentVariables = new HashMap<>(System.getenv());
        environmentVariables.putAll(environment.getEnvironmentVariables());
        for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
            System.out.println("Setting environment variable " + entry.getKey() + "=" + entry.getValue());
        }

        switch (environment.getType()) {
            case System:
                commandLine.addArgument("-u");
                commandLine.addArgument(scriptFile.toString());
                break;
            case Conda:
                commandLine.addArgument("run");
                commandLine.addArgument("--no-capture-output");
                commandLine.addArgument("-p");
                commandLine.addArgument(environment.getCondaEnvironment());
                commandLine.addArgument("python");
                commandLine.addArgument("-u");
                commandLine.addArgument(scriptFile.toString());
            case VirtualEnvironment:
                commandLine.addArgument("-u");
                commandLine.addArgument(scriptFile.toString());
                break;
        }

        DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
        setupLogger(commandLine, executor);

        try {
            executor.execute(commandLine, environmentVariables);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Escapes a string to be used within Python code
     * Will not add quotes around the string
     *
     * @param value unescaped string
     * @return escaped string
     */
    public static String escapeString(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Converts the object into valid Python code
     *
     * @param object the object
     * @return Python code
     */
    public static String objectToPython(Object object) {
        if (object instanceof String) {
            return "\"" + escapeString(object.toString()) + "\"";
        }
        if (object instanceof Path) {
            return "\"" + escapeString(object.toString()) + "\"";
        } else if (object instanceof Boolean) {
            return (((Boolean) object) ? "True" : "False");
        } else if (object == null) {
            return "None";
        } else if (object instanceof Collection) {
            return listToPythonArray((Collection<Object>) object, true);
        } else if (object instanceof Map) {
            return mapToPythonDict((Map<String, Object>) object);
        } else {
            return "" + object;
        }
    }

    /**
     * Converts a collection into a Python array
     *
     * @param items                   the items
     * @param withSurroundingBrackets if enabled, surrounding brackets are added
     * @return Python code
     */
    public static String listToPythonArray(Collection<Object> items, boolean withSurroundingBrackets) {
        return (withSurroundingBrackets ? "[" : "") + items.stream().map(PythonUtils::objectToPython).collect(Collectors.joining(", "))
                + (withSurroundingBrackets ? "]" : "");
    }

    /**
     * Converts a dictionary into a set of Python function arguments
     *
     * @param parameters the parameters
     * @return Python code
     */
    public static String mapToPythonDict(Map<String, Object> parameters) {
        return "dict(" + mapToPythonArguments(parameters) + ")";
    }

    /**
     * Converts a dictionary into a set of Python function arguments
     *
     * @param parameters the parameters
     * @return Python code
     */
    public static String mapToPythonArguments(Map<String, Object> parameters) {
        return parameters.entrySet().stream().map(entry ->
                entry.getKey() + "=" + objectToPython(entry.getValue())).collect(Collectors.joining(", "));
    }

    public static RawPythonCode rawPythonCode(String code) {
        return new RawPythonCode(code);
    }

    public static class RawPythonCode {
        private final String code;

        public RawPythonCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return code;
        }
    }
}
