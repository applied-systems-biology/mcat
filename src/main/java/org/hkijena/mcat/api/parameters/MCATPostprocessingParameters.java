package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
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

    public MCATPostprocessingParameters() {

    }

    public MCATPostprocessingParameters(MCATPostprocessingParameters other) {
        this.analyzeNetIncrease = other.analyzeNetIncrease;
        this.analyzeNetDecrease = other.analyzeNetDecrease;
        this.analyzeMaxIncrease = other.analyzeMaxIncrease;
        this.analyzeMaxDecrease = other.analyzeMaxDecrease;
        this.performClusterMorphologyAnalysis = other.performClusterMorphologyAnalysis;
    }

    @JsonGetter("analyze-net-increase")
    public boolean isAnalyzeNetIncrease() {
        return analyzeNetIncrease;
    }

    @JsonSetter("analyze-net-increase")
    public void setAnalyzeNetIncrease(boolean analyzeNetIncrease) {
        this.analyzeNetIncrease = analyzeNetIncrease;
        postChangedEvent("analyze-net-increase");
    }

    @JsonGetter("analyze-net-decrease")
    public boolean isAnalyzeNetDecrease() {
        return analyzeNetDecrease;
    }

    @JsonSetter("analyze-net-decrease")
    public void setAnalyzeNetDecrease(boolean analyzeNetDecrease) {
        this.analyzeNetDecrease = analyzeNetDecrease;
        postChangedEvent("analyze-net-decrease");
    }

    @JsonGetter("analyze-max-increase")
    public boolean isAnalyzeMaxIncrease() {
        return analyzeMaxIncrease;
    }

    @JsonSetter("analyze-max-increase")
    public void setAnalyzeMaxIncrease(boolean analyzeMaxIncrease) {
        this.analyzeMaxIncrease = analyzeMaxIncrease;
        postChangedEvent("analyze-max-increase");
    }

    @JsonGetter("analyze-max-decrease")
    public boolean isAnalyzeMaxDecrease() {
        return analyzeMaxDecrease;
    }

    @JsonSetter("analyze-max-decrease")
    public void setAnalyzeMaxDecrease(boolean analyzeMaxDecrease) {
        this.analyzeMaxDecrease = analyzeMaxDecrease;
        postChangedEvent("analyze-max-decrease");
    }

    @JsonGetter("perform-cluster-morphology-analysis")
    public boolean isPerformClusterMorphologyAnalysis() {
        return performClusterMorphologyAnalysis;
    }

    @JsonSetter("perform-cluster-morphology-analysis")
    public void setPerformClusterMorphologyAnalysis(boolean performClusterMorphologyAnalysis) {
        this.performClusterMorphologyAnalysis = performClusterMorphologyAnalysis;
        postChangedEvent("perform-morphology-analysis");
    }
}
