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
package org.hkijena.mcat.api;

import org.hkijena.mcat.extension.dataproviders.api.HyperstackFromTifDataProvider;
import org.hkijena.mcat.extension.dataproviders.api.ROIFromFileDataProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Imports samples from a folder
 */
public class MCATBatchImporter {
    private MCATProject project;

    private Path inputFolder;
    private boolean subfoldersAreTreatments = true;
    private boolean includeTreatmentInName = false;

    private boolean importRawImages = true;
    private String rawImagesPattern = ".*\\.tif";

    private boolean importROI = true;
    private String roiPattern = ".*\\.roi";

    private boolean importPreprocessedImages = false;
    private String preprocessedImagesPattern = ".*\\.tif";

    private boolean importDerivativeMatrix = false;
    private String derivativeMatrixPattern = ".*\\.derivative_matrix.csv";

    private boolean importClusters = false;
    private String clustersPattern = ".*\\.clusters.csv";

    public MCATBatchImporter(MCATProject project) {
        this.project = project;
    }

    public Path getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(Path inputFolder) {
        this.inputFolder = inputFolder;
    }

    public boolean isSubfoldersAreTreatments() {
        return subfoldersAreTreatments;
    }

    public void setSubfoldersAreTreatments(boolean subfoldersAreTreatments) {
        this.subfoldersAreTreatments = subfoldersAreTreatments;
    }

    public boolean isIncludeTreatmentInName() {
        return includeTreatmentInName;
    }

    public void setIncludeTreatmentInName(boolean includeTreatmentInName) {
        this.includeTreatmentInName = includeTreatmentInName;
    }

    public boolean isImportRawImages() {
        return importRawImages;
    }

    public void setImportRawImages(boolean importRawImages) {
        this.importRawImages = importRawImages;
    }

    public String getRawImagesPattern() {
        return rawImagesPattern;
    }

    public void setRawImagesPattern(String rawImagesPattern) {
        this.rawImagesPattern = rawImagesPattern;
    }

    public boolean isImportROI() {
        return importROI;
    }

    public void setImportROI(boolean importROI) {
        this.importROI = importROI;
    }

    public String getRoiPattern() {
        return roiPattern;
    }

    public void setRoiPattern(String roiPattern) {
        this.roiPattern = roiPattern;
    }

    public boolean isImportPreprocessedImages() {
        return importPreprocessedImages;
    }

    public void setImportPreprocessedImages(boolean importPreprocessedImages) {
        this.importPreprocessedImages = importPreprocessedImages;
    }

    public boolean isImportDerivativeMatrix() {
        return importDerivativeMatrix;
    }

    public void setImportDerivativeMatrix(boolean importDerivativeMatrix) {
        this.importDerivativeMatrix = importDerivativeMatrix;
    }

    public String getDerivativeMatrixPattern() {
        return derivativeMatrixPattern;
    }

    public void setDerivativeMatrixPattern(String derivativeMatrixPattern) {
        this.derivativeMatrixPattern = derivativeMatrixPattern;
    }

    public String getPreprocessedImagesPattern() {
        return preprocessedImagesPattern;
    }

    public void setPreprocessedImagesPattern(String preprocessedImagesPattern) {
        this.preprocessedImagesPattern = preprocessedImagesPattern;
    }

    public boolean isImportClusters() {
        return importClusters;
    }

    public void setImportClusters(boolean importClusters) {
        this.importClusters = importClusters;
    }

    public String getClustersPattern() {
        return clustersPattern;
    }

    public void setClustersPattern(String clustersPattern) {
        this.clustersPattern = clustersPattern;
    }

    public MCATProject getProject() {
        return project;
    }

    private Path matchFile(Path folder, String pattern) throws IOException {
        return Files.list(folder).filter(path -> path.getFileName().toString().matches(pattern)).findFirst().orElse(null);
    }

    private MCATProjectDataSet importSample(Path folder, String treatment) throws IOException {
        String name = folder.getFileName().toString();
        if (treatment != null && includeTreatmentInName) {
            name = treatment + "_" + name;
        }

        MCATProjectDataSet sample = getProject().addSample(name);
        sample.getParameters().setTreatment(treatment);

        if (importRawImages) {
            Path matched = matchFile(folder, rawImagesPattern);
            if (matched != null) {
                HyperstackFromTifDataProvider provider = new HyperstackFromTifDataProvider();
                provider.setFilePath(matched);
                sample.getRawDataInterface().getRawImage().setCurrentProvider(provider);
            }
        }
        if (importROI) {
            Path matched = matchFile(folder, roiPattern);
            if (matched != null) {
                ROIFromFileDataProvider provider = new ROIFromFileDataProvider();
                provider.setFilePath(matched);
                sample.getRawDataInterface().getTissueROI().setCurrentProvider(provider);
            }
        }

        return sample;
    }

    public Set<MCATProjectDataSet> run() throws IOException {
        Set<MCATProjectDataSet> result = new HashSet<>();
        List<Path> treatmentPaths;

        if (subfoldersAreTreatments) {
            treatmentPaths = Files.list(inputFolder).filter(d -> Files.isDirectory(d)).collect(Collectors.toList());
        } else {
            treatmentPaths = Arrays.asList(inputFolder);
        }

        for (Path treatmentPath : treatmentPaths) {
            String treatmentName = treatmentPath.equals(inputFolder) ? null : treatmentPath.getFileName().toString();
            for (Path samplePath : Files.list(treatmentPath).filter(d -> Files.isDirectory(d)).collect(Collectors.toList())) {
                result.add(importSample(samplePath, treatmentName));
            }
        }

        return result;
    }


}
