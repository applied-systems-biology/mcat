package org.hkijena.mcat;

import org.hkijena.mcat.api.MCATProject;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        Path projectFilePath = null;
        Path outputPath = null;
        for (String arg : args) {
            if(arg.startsWith("--project-file=")) {
                projectFilePath = Paths.get(arg.substring("--project-file=".length()));
            }
            else if(arg.startsWith("--output-path=")) {
                outputPath = Paths.get(arg.substring("--output-path=".length()));
            }
        }
        if(projectFilePath == null || !Files.exists(projectFilePath))
            throw new IllegalArgumentException("Project file does not exist: " + projectFilePath);
        if(outputPath == null)
            throw new IllegalArgumentException("Output path not provided!");
        MCATProject project = MCATProject.loadProject(projectFilePath);
        MCATValidityReport report = new MCATValidityReport();
        MCATRun run = new MCATRun(project);
        run.setOutputPath(outputPath);
        run.reportValidity(report);
        if(!report.isValid())
            throw new RuntimeException("Project did not pass validity check!");
        run.run(status -> System.out.println(status.getProgress() + "/" + status.getMaxProgress() + ": " + status.getCurrentTask()), () -> false);
    }
}
