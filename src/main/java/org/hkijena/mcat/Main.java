/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 * 
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 * 
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
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
        System.out.println("Loading project from " + projectFilePath);
        MCATProject project = MCATProject.loadProject(projectFilePath);
        MCATValidityReport report = new MCATValidityReport();
        System.out.println("Output will be written to " + outputPath);
        MCATRun run = new MCATRun(project);
        run.setOutputPath(outputPath);
        run.reportValidity(report);
        if(!report.isValid())
            throw new RuntimeException("Project did not pass validity check!");
        run.run(status -> System.out.println(status.getProgress() + "/" + status.getMaxProgress() + ": " + status.getCurrentTask()), () -> false);
    }
}
