package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.utils.api.ACAQDocumentation;
import org.hkijena.mcat.utils.api.events.ParameterChangedEvent;
import org.hkijena.mcat.utils.api.parameters.ACAQParameter;
import org.hkijena.mcat.utils.api.parameters.ACAQParameterCollection;

/**
 * Contains postprocessing parameters
 */
public class MCATPostprocessingParameters implements ACAQParameterCollection {
    private EventBus eventBus = new EventBus();
    private boolean analyzeNetIncrease = true;
    private boolean analyzeNetDecrease = false;
    private boolean analyzeMaxIncrease = false;
    private boolean analyzeMaxDecrease = false;
    private boolean performClusterMorphologyAnalysis = false;
    private double cutoffValue = 0;

    public MCATPostprocessingParameters() {

    }

    public MCATPostprocessingParameters(MCATPostprocessingParameters other) {
        this.analyzeNetIncrease = other.analyzeNetIncrease;
        this.analyzeNetDecrease = other.analyzeNetDecrease;
        this.analyzeMaxIncrease = other.analyzeMaxIncrease;
        this.analyzeMaxDecrease = other.analyzeMaxDecrease;
        this.performClusterMorphologyAnalysis = other.performClusterMorphologyAnalysis;
        this.cutoffValue = other.cutoffValue;
    }

    @ACAQDocumentation(name = "Analyze net increase")
    @ACAQParameter("analyze-net-increase")
    @JsonGetter("analyze-net-increase")
    public boolean isAnalyzeNetIncrease() {
        return analyzeNetIncrease;
    }

    @ACAQParameter("analyze-net-increase")
    @JsonSetter("analyze-net-increase")
    public void setAnalyzeNetIncrease(boolean analyzeNetIncrease) {
        this.analyzeNetIncrease = analyzeNetIncrease;
        eventBus.post(new ParameterChangedEvent(this ,"analyze-net-increase"));
    }

    @ACAQDocumentation(name = "Analyze net decrease")
    @ACAQParameter("analyze-net-decrease")
    @JsonGetter("analyze-net-decrease")
    public boolean isAnalyzeNetDecrease() {
        return analyzeNetDecrease;
    }

    @ACAQParameter("analyze-net-decrease")
    @JsonSetter("analyze-net-decrease")
    public void setAnalyzeNetDecrease(boolean analyzeNetDecrease) {
        this.analyzeNetDecrease = analyzeNetDecrease;
        eventBus.post(new ParameterChangedEvent(this ,"analyze-net-decrease"));
    }

    @ACAQDocumentation(name = "Analyze max increase")
    @ACAQParameter("analyze-max-increase")
    @JsonGetter("analyze-max-increase")
    public boolean isAnalyzeMaxIncrease() {
        return analyzeMaxIncrease;
    }

    @ACAQParameter("analyze-max-increase")
    @JsonSetter("analyze-max-increase")
    public void setAnalyzeMaxIncrease(boolean analyzeMaxIncrease) {
        this.analyzeMaxIncrease = analyzeMaxIncrease;
        eventBus.post(new ParameterChangedEvent(this ,"analyze-max-increase"));
    }

    @ACAQDocumentation(name = "Analyze max decrease")
    @ACAQParameter("analyze-max-decrease")
    @JsonGetter("analyze-max-decrease")
    public boolean isAnalyzeMaxDecrease() {
        return analyzeMaxDecrease;
    }

    @ACAQParameter("analyze-max-decrease")
    @JsonSetter("analyze-max-decrease")
    public void setAnalyzeMaxDecrease(boolean analyzeMaxDecrease) {
        this.analyzeMaxDecrease = analyzeMaxDecrease;
        eventBus.post(new ParameterChangedEvent(this ,"analyze-max-decrease"));
    }

    @ACAQDocumentation(name = "Perform cluster morphology analysis")
    @ACAQParameter("perform-cluster-morphology-analysis")
    @JsonGetter("perform-cluster-morphology-analysis")
    public boolean isPerformClusterMorphologyAnalysis() {
        return performClusterMorphologyAnalysis;
    }

    @ACAQParameter("perform-cluster-morphology-analysis")
    @JsonSetter("perform-cluster-morphology-analysis")
    public void setPerformClusterMorphologyAnalysis(boolean performClusterMorphologyAnalysis) {
        this.performClusterMorphologyAnalysis = performClusterMorphologyAnalysis;
        eventBus.post(new ParameterChangedEvent(this , "perform-morphology-analysis"));
    }

    @ACAQDocumentation(name = "Cutoff value")
    @ACAQParameter("cutoff-value")
    @JsonGetter("cutoff-value")
    public double getCutoffValue() {
        return cutoffValue;
    }

    @ACAQParameter("cutoff-value")
    @JsonSetter("cutoff-value")
    public void setCutoffValue(double cutoffValue) {
        this.cutoffValue = cutoffValue;
        eventBus.post(new ParameterChangedEvent(this ,"cutoff-value"));
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
