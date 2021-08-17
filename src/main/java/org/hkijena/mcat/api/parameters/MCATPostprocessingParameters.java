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
package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.events.ParameterChangedEvent;
import org.hkijena.mcat.extension.parameters.editors.NumberParameterSettings;

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
    public static final double CUTOFF_DEFAULT = 0.0;

    private EventBus eventBus = new EventBus();
    private boolean analyzeNetIncrease = true;
    private boolean analyzeNetDecrease = false;
    private boolean analyzeMaxIncrease = false;
    private boolean analyzeMaxDecrease = false;
    //    private boolean performClusterMorphologyAnalysis = false;
    private double cutoffValue = CUTOFF_DEFAULT;

    public MCATPostprocessingParameters() {

    }

    public MCATPostprocessingParameters(MCATPostprocessingParameters other) {
        this.analyzeNetIncrease = other.analyzeNetIncrease;
        this.analyzeNetDecrease = other.analyzeNetDecrease;
        this.analyzeMaxIncrease = other.analyzeMaxIncrease;
        this.analyzeMaxDecrease = other.analyzeMaxDecrease;
//        this.performClusterMorphologyAnalysis = other.performClusterMorphologyAnalysis;
        this.cutoffValue = other.cutoffValue;
    }

    @MCATDocumentation(name = "Analyze net increase", description = "res:///org/hkijena/mcat/documentation/parameter_postprocessing_analyze_net_increase.md")
    @MCATParameter(value = "analyze-net-increase", shortKey = "netInc", uiOrder = 3)
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

    @MCATDocumentation(name = "Analyze net decrease", description = "res:///org/hkijena/mcat/documentation/parameter_postprocessing_analyze_net_decrease.md")
    @MCATParameter(value = "analyze-net-decrease", shortKey = "netDec", uiOrder = 2)
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

    @MCATDocumentation(name = "Analyze max increase", description = "res:///org/hkijena/mcat/documentation/parameter_postprocessing_analyze_max_increase.md")
    @MCATParameter(value = "analyze-max-increase", shortKey = "maxInc", uiOrder = 1)
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

    @MCATDocumentation(name = "Analyze max decrease", description = "res:///org/hkijena/mcat/documentation/parameter_postprocessing_analyze_max_decrease.md")
    @MCATParameter(value = "analyze-max-decrease", shortKey = "maxDec", uiOrder = 0)
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
//
//    @MCATDocumentation(name = "Perform cluster morphology analysis", description = "res:///org/hkijena/mcat/documentation/parameter_postprocessing_perform_cluster_morphology_analysis.md")
//    @MCATParameter(value = "perform-cluster-morphology-analysis", shortKey = "cma")
//    @JsonGetter("perform-cluster-morphology-analysis")
//    public boolean isPerformClusterMorphologyAnalysis() {
//        return performClusterMorphologyAnalysis;
//    }

//    @MCATParameter("perform-cluster-morphology-analysis")
//    @JsonSetter("perform-cluster-morphology-analysis")
//    public void setPerformClusterMorphologyAnalysis(boolean performClusterMorphologyAnalysis) {
//        this.performClusterMorphologyAnalysis = performClusterMorphologyAnalysis;
//        eventBus.post(new ParameterChangedEvent(this, "perform-morphology-analysis"));
//    }

    @MCATDocumentation(name = "Cutoff value", description = "res:///org/hkijena/mcat/documentation/parameter_postprocessing_cutoff_value.md")
    @MCATParameter(value = "cutoff-value", shortKey = "cutoff", uiOrder = -1)
    @JsonGetter("cutoff-value")
    @NumberParameterSettings(step = 0.1)
    public double getCutoffValue() {
        return cutoffValue;
    }

    @MCATParameter("cutoff-value")
    @JsonSetter("cutoff-value")
    public boolean setCutoffValue(double cutoffValue) {
        if (cutoffValue < 0 | cutoffValue > 1) {
            return false;
        }
        this.cutoffValue = cutoffValue;
        eventBus.post(new ParameterChangedEvent(this, "cutoff-value"));
        return true;
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
//                performClusterMorphologyAnalysis == that.performClusterMorphologyAnalysis &&
                Double.compare(that.cutoffValue, cutoffValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(analyzeNetIncrease, analyzeNetDecrease, analyzeMaxIncrease, analyzeMaxDecrease,
//        		performClusterMorphologyAnalysis, 
                cutoffValue);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public String toString() {
        return MCATCustomParameterCollection.parametersToString((new MCATTraversedParameterCollection(this)).getParameters().values(), "_", "-");
    }

    public String toShortenedString() {
        String cutoff = cutoffValue == CUTOFF_DEFAULT ? "" : "_cutoff-" + cutoffValue;
        String netInc = analyzeNetIncrease ? "_netInc" : "";
        String netDec = analyzeNetDecrease ? "_netDec" : "";
        String maxInc = analyzeMaxIncrease ? "_maxInc" : "";
        String maxDec = analyzeMaxDecrease ? "_maxDec" : "";

        return cutoff + netInc + netDec + maxInc + maxDec;
    }
}
