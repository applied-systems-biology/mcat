package org.hkijena.mcat.api.parameters;

import org.hkijena.mcat.api.MCATParameters;

/**
 * Contains postprocessing parameters
 */
public class MCATPostprocessingParameters extends MCATParameters {
    private boolean analyzeNetIncrease = true;
    private boolean analyzeNetDecrease = true;
    private boolean analyzeMaxIncrease = true;
    private boolean analyzeMaxDecrease = true;
    private boolean performClusterMorphologyAnalysis = true;

    public boolean isAnalyzeNetIncrease() {
        return analyzeNetIncrease;
    }

    public void setAnalyzeNetIncrease(boolean analyzeNetIncrease) {
        this.analyzeNetIncrease = analyzeNetIncrease;
        postChangedEvent("analyze-net-increase");
    }

    public boolean isAnalyzeNetDecrease() {
        return analyzeNetDecrease;
    }

    public void setAnalyzeNetDecrease(boolean analyzeNetDecrease) {
        this.analyzeNetDecrease = analyzeNetDecrease;
        postChangedEvent("analyze-net-decrease");
    }

    public boolean isAnalyzeMaxIncrease() {
        return analyzeMaxIncrease;
    }

    public void setAnalyzeMaxIncrease(boolean analyzeMaxIncrease) {
        this.analyzeMaxIncrease = analyzeMaxIncrease;
        postChangedEvent("analyze-max-increase");
    }

    public boolean isAnalyzeMaxDecrease() {
        return analyzeMaxDecrease;
    }

    public void setAnalyzeMaxDecrease(boolean analyzeMaxDecrease) {
        this.analyzeMaxDecrease = analyzeMaxDecrease;
        postChangedEvent("analyze-max-decrease");
    }

    public boolean isPerformClusterMorphologyAnalysis() {
        return performClusterMorphologyAnalysis;
    }

    public void setPerformClusterMorphologyAnalysis(boolean performClusterMorphologyAnalysis) {
        this.performClusterMorphologyAnalysis = performClusterMorphologyAnalysis;
        postChangedEvent("perform-morphology-analysis");
    }
}
