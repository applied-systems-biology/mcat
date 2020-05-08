package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATClusteringHierarchy;
import org.hkijena.mcat.utils.api.ACAQDocumentation;
import org.hkijena.mcat.utils.api.events.ParameterChangedEvent;
import org.hkijena.mcat.utils.api.parameters.ACAQCustomParameterCollection;
import org.hkijena.mcat.utils.api.parameters.ACAQParameter;
import org.hkijena.mcat.utils.api.parameters.ACAQParameterCollection;
import org.hkijena.mcat.utils.api.parameters.ACAQTraversedParameterCollection;

import java.util.Objects;

/**
 * Class that contains all clustering parameters.
 * <p>
 * To create a parameter, create a a private field with getter & setter.
 * Annotate the getter with {@link JsonGetter}, {@link ACAQParameter}, and {@link ACAQDocumentation}
 * Annotate the setter with {@link ACAQParameter} and {@link JsonSetter}
 * <p>
 * Post an event {@link ParameterChangedEvent} when a value is set.
 * <p>
 * Add the variable to getHashCode() and equals()
 */
public class MCATClusteringParameters implements ACAQParameterCollection {
    private EventBus eventBus = new EventBus();
    private int kMeansK = 5;
    private int minLength = Integer.MAX_VALUE;

    private MCATClusteringHierarchy clusteringHierarchy = MCATClusteringHierarchy.PerTreatment;

    public MCATClusteringParameters() {
    }

    public MCATClusteringParameters(MCATClusteringParameters other) {
        this.kMeansK = other.kMeansK;
        this.minLength = other.minLength;
        this.clusteringHierarchy = other.clusteringHierarchy;
    }

    @ACAQDocumentation(name = "Clustering hierarchy")
    @ACAQParameter(value = "clustering-hierarchy", shortKey = "ch")
    @JsonGetter("clustering-hierarchy")
    public MCATClusteringHierarchy getClusteringHierarchy() {
        return clusteringHierarchy;
    }

    @ACAQParameter("clustering-hierarchy")
    @JsonSetter("clustering-hierarchy")
    public void setClusteringHierarchy(MCATClusteringHierarchy clusteringHierarchy) {
        this.clusteringHierarchy = clusteringHierarchy;
        eventBus.post(new ParameterChangedEvent(this, "clustering-hierarchy"));
    }

    @ACAQDocumentation(name = "K-Means groups (K)")
    @ACAQParameter(value = "kmeans-k", shortKey = "k")
    @JsonGetter("kmeans-k")
    public int getkMeansK() {
        return kMeansK;
    }

    @ACAQParameter("kmeans-k")
    @JsonSetter("kmeans-k")
    public void setkMeansK(int kMeansK) {
        this.kMeansK = kMeansK;
        eventBus.post(new ParameterChangedEvent(this, "kmeans-k"));
    }

    @ACAQDocumentation(name = "Minimum length")
    @ACAQParameter(value = "min-length", shortKey = "mlength")
    @JsonGetter("min-length")
    public int getMinLength() {
        return minLength;
    }

    @ACAQParameter("min-length")
    @JsonSetter("min-length")
    public void setMinLength(int minLength) {
        this.minLength = minLength;
        eventBus.post(new ParameterChangedEvent(this, "min-length"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCATClusteringParameters that = (MCATClusteringParameters) o;
        return kMeansK == that.kMeansK &&
                minLength == that.minLength &&
                clusteringHierarchy == that.clusteringHierarchy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kMeansK, minLength, clusteringHierarchy);
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
