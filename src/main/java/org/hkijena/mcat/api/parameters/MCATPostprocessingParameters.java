package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.utils.api.ACAQDocumentation;
import org.hkijena.mcat.utils.api.events.ParameterChangedEvent;
import org.hkijena.mcat.utils.api.parameters.ACAQCustomParameterCollection;
import org.hkijena.mcat.utils.api.parameters.ACAQParameter;
import org.hkijena.mcat.utils.api.parameters.ACAQParameterCollection;
import org.hkijena.mcat.utils.api.parameters.ACAQTraversedParameterCollection;

import java.util.Objects;

/**
 * Contains postprocessing parameters
 * <p>
 * To create a parameter, create a a private field with getter & setter.
 * Annotate the getter with {@link JsonGetter}, {@link ACAQParameter}, and {@link ACAQDocumentation}
 * Annotate the setter with {@link ACAQParameter} and {@link JsonSetter}
 * <p>
 * Post an event {@link ParameterChangedEvent} when a value is set.
 * <p>
 * Add the variable to getHashCode() and equals()
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
    @ACAQParameter(value = "analyze-net-increase", shortKey = "net-inc")
    @JsonGetter("analyze-net-increase")
    public boolean isAnalyzeNetIncrease() {
        return analyzeNetIncrease;
    }

    @ACAQParameter("analyze-net-increase")
    @JsonSetter("analyze-net-increase")
    public void setAnalyzeNetIncrease(boolean analyzeNetIncrease) {
        this.analyzeNetIncrease = analyzeNetIncrease;
        eventBus.post(new ParameterChangedEvent(this, "analyze-net-increase"));
    }

    @ACAQDocumentation(name = "Analyze net decrease")
    @ACAQParameter(value = "analyze-net-decrease", shortKey = "net-dec")
    @JsonGetter("analyze-net-decrease")
    public boolean isAnalyzeNetDecrease() {
        return analyzeNetDecrease;
    }

    @ACAQParameter("analyze-net-decrease")
    @JsonSetter("analyze-net-decrease")
    public void setAnalyzeNetDecrease(boolean analyzeNetDecrease) {
        this.analyzeNetDecrease = analyzeNetDecrease;
        eventBus.post(new ParameterChangedEvent(this, "analyze-net-decrease"));
    }

    @ACAQDocumentation(name = "Analyze max increase")
    @ACAQParameter(value = "analyze-max-increase", shortKey = "max-inc")
    @JsonGetter("analyze-max-increase")
    public boolean isAnalyzeMaxIncrease() {
        return analyzeMaxIncrease;
    }

    @ACAQParameter("analyze-max-increase")
    @JsonSetter("analyze-max-increase")
    public void setAnalyzeMaxIncrease(boolean analyzeMaxIncrease) {
        this.analyzeMaxIncrease = analyzeMaxIncrease;
        eventBus.post(new ParameterChangedEvent(this, "analyze-max-increase"));
    }

    @ACAQDocumentation(name = "Analyze max decrease")
    @ACAQParameter(value = "analyze-max-decrease", shortKey = "max-dec")
    @JsonGetter("analyze-max-decrease")
    public boolean isAnalyzeMaxDecrease() {
        return analyzeMaxDecrease;
    }

    @ACAQParameter("analyze-max-decrease")
    @JsonSetter("analyze-max-decrease")
    public void setAnalyzeMaxDecrease(boolean analyzeMaxDecrease) {
        this.analyzeMaxDecrease = analyzeMaxDecrease;
        eventBus.post(new ParameterChangedEvent(this, "analyze-max-decrease"));
    }

    @ACAQDocumentation(name = "Perform cluster morphology analysis")
    @ACAQParameter(value = "perform-cluster-morphology-analysis", shortKey = "cma")
    @JsonGetter("perform-cluster-morphology-analysis")
    public boolean isPerformClusterMorphologyAnalysis() {
        return performClusterMorphologyAnalysis;
    }

    @ACAQParameter("perform-cluster-morphology-analysis")
    @JsonSetter("perform-cluster-morphology-analysis")
    public void setPerformClusterMorphologyAnalysis(boolean performClusterMorphologyAnalysis) {
        this.performClusterMorphologyAnalysis = performClusterMorphologyAnalysis;
        eventBus.post(new ParameterChangedEvent(this, "perform-morphology-analysis"));
    }

    @ACAQDocumentation(name = "Cutoff value")
    @ACAQParameter(value = "cutoff-value", shortKey = "cutoff")
    @JsonGetter("cutoff-value")
    public double getCutoffValue() {
        return cutoffValue;
    }

    @ACAQParameter("cutoff-value")
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
        return ACAQCustomParameterCollection.parametersToString((new ACAQTraversedParameterCollection(this)).getParameters().values(), ",", "=");
    }
}
