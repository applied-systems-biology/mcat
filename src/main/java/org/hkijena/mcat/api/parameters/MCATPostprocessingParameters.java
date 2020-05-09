package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.events.ParameterChangedEvent;

import java.util.Objects;

/**
 * Contains postprocessing parameters
 * <p>
 * To create a parameter, create a a private field with getter & setter.
 * Annotate the getter with {@link JsonGetter}, {@link MCATParameter}, and {@link MCATDocumentation}
 * Annotate the setter with {@link MCATParameter} and {@link JsonSetter}
 * <p>
 * Post an event {@link ParameterChangedEvent} when a value is set.
 * <p>
 * Add the variable to getHashCode() and equals()
 */
public class MCATPostprocessingParameters implements MCATParameterCollection {
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

    @MCATDocumentation(name = "Analyze net increase")
    @MCATParameter(value = "analyze-net-increase", shortKey = "net-inc")
    @JsonGetter("analyze-net-increase")
    public boolean isAnalyzeNetIncrease() {
        return analyzeNetIncrease;
    }

    @MCATParameter("analyze-net-increase")
    @JsonSetter("analyze-net-increase")
    public void setAnalyzeNetIncrease(boolean analyzeNetIncrease) {
        this.analyzeNetIncrease = analyzeNetIncrease;
        eventBus.post(new ParameterChangedEvent(this, "analyze-net-increase"));
    }

    @MCATDocumentation(name = "Analyze net decrease")
    @MCATParameter(value = "analyze-net-decrease", shortKey = "net-dec")
    @JsonGetter("analyze-net-decrease")
    public boolean isAnalyzeNetDecrease() {
        return analyzeNetDecrease;
    }

    @MCATParameter("analyze-net-decrease")
    @JsonSetter("analyze-net-decrease")
    public void setAnalyzeNetDecrease(boolean analyzeNetDecrease) {
        this.analyzeNetDecrease = analyzeNetDecrease;
        eventBus.post(new ParameterChangedEvent(this, "analyze-net-decrease"));
    }

    @MCATDocumentation(name = "Analyze max increase")
    @MCATParameter(value = "analyze-max-increase", shortKey = "max-inc")
    @JsonGetter("analyze-max-increase")
    public boolean isAnalyzeMaxIncrease() {
        return analyzeMaxIncrease;
    }

    @MCATParameter("analyze-max-increase")
    @JsonSetter("analyze-max-increase")
    public void setAnalyzeMaxIncrease(boolean analyzeMaxIncrease) {
        this.analyzeMaxIncrease = analyzeMaxIncrease;
        eventBus.post(new ParameterChangedEvent(this, "analyze-max-increase"));
    }

    @MCATDocumentation(name = "Analyze max decrease")
    @MCATParameter(value = "analyze-max-decrease", shortKey = "max-dec")
    @JsonGetter("analyze-max-decrease")
    public boolean isAnalyzeMaxDecrease() {
        return analyzeMaxDecrease;
    }

    @MCATParameter("analyze-max-decrease")
    @JsonSetter("analyze-max-decrease")
    public void setAnalyzeMaxDecrease(boolean analyzeMaxDecrease) {
        this.analyzeMaxDecrease = analyzeMaxDecrease;
        eventBus.post(new ParameterChangedEvent(this, "analyze-max-decrease"));
    }

    @MCATDocumentation(name = "Perform cluster morphology analysis")
    @MCATParameter(value = "perform-cluster-morphology-analysis", shortKey = "cma")
    @JsonGetter("perform-cluster-morphology-analysis")
    public boolean isPerformClusterMorphologyAnalysis() {
        return performClusterMorphologyAnalysis;
    }

    @MCATParameter("perform-cluster-morphology-analysis")
    @JsonSetter("perform-cluster-morphology-analysis")
    public void setPerformClusterMorphologyAnalysis(boolean performClusterMorphologyAnalysis) {
        this.performClusterMorphologyAnalysis = performClusterMorphologyAnalysis;
        eventBus.post(new ParameterChangedEvent(this, "perform-morphology-analysis"));
    }

    @MCATDocumentation(name = "Cutoff value")
    @MCATParameter(value = "cutoff-value", shortKey = "cutoff")
    @JsonGetter("cutoff-value")
    public double getCutoffValue() {
        return cutoffValue;
    }

    @MCATParameter("cutoff-value")
    @JsonSetter("cutoff-value")
    public void setCutoffValue(double cutoffValue) {
        this.cutoffValue = cutoffValue;
        eventBus.post(new ParameterChangedEvent(this, "cutoff-value"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCATPostprocessingParameters that = (MCATPostprocessingParameters) o;
        return analyzeNetIncrease == that.analyzeNetIncrease &&
                analyzeNetDecrease == that.analyzeNetDecrease &&
                analyzeMaxIncrease == that.analyzeMaxIncrease &&
                analyzeMaxDecrease == that.analyzeMaxDecrease &&
                performClusterMorphologyAnalysis == that.performClusterMorphologyAnalysis &&
                Double.compare(that.cutoffValue, cutoffValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(analyzeNetIncrease, analyzeNetDecrease, analyzeMaxIncrease, analyzeMaxDecrease, performClusterMorphologyAnalysis, cutoffValue);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public String toString() {
        return MCATCustomParameterCollection.parametersToString((new MCATTraversedParameterCollection(this)).getParameters().values(), ",", "=");
    }
}
