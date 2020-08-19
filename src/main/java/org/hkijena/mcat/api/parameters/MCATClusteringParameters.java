/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATClusteringHierarchy;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.events.ParameterChangedEvent;

import java.util.Objects;

/**
 * Class that contains all clustering parameters.
 * <p>
 * To create a parameter, create a a private field with getter & setter.
 * Annotate the getter with {@link JsonGetter}, {@link MCATParameter}, and {@link MCATDocumentation}
 * Annotate the setter with {@link MCATParameter} and {@link JsonSetter}
 * <p>
 * Post an event {@link ParameterChangedEvent} when a value is set.
 * <p>
 * Add the variable to getHashCode() and equals()
 */
public class MCATClusteringParameters implements MCATParameterCollection {
	public static final int MIN_LENGTH_DEFAULT = Integer.MAX_VALUE;
	
    private EventBus eventBus = new EventBus();
    private int kMeansK = 5;
    private int minLength = MIN_LENGTH_DEFAULT;

    private MCATClusteringHierarchy clusteringHierarchy = MCATClusteringHierarchy.PerTreatment;

    public MCATClusteringParameters() {
    }

    public MCATClusteringParameters(MCATClusteringParameters other) {
        this.kMeansK = other.kMeansK;
        this.minLength = other.minLength;
        this.clusteringHierarchy = other.clusteringHierarchy;
    }

    @MCATDocumentation(name = "Clustering hierarchy", description = "res:///org/hkijena/mcat/documentation/parameter_clustering_hierarchy.md")
    @MCATParameter(value = "clustering-hierarchy", shortKey = "grouping")
    @JsonGetter("clustering-hierarchy")
    public MCATClusteringHierarchy getClusteringHierarchy() {
        return clusteringHierarchy;
    }

    @MCATParameter("clustering-hierarchy")
    @JsonSetter("clustering-hierarchy")
    public void setClusteringHierarchy(MCATClusteringHierarchy clusteringHierarchy) {
        this.clusteringHierarchy = clusteringHierarchy;
        eventBus.post(new ParameterChangedEvent(this, "clustering-hierarchy"));
    }

    @MCATDocumentation(name = "K-Means k", description = "res:///org/hkijena/mcat/documentation/parameter_clustering_k.md")
    @MCATParameter(value = "kmeans-k", shortKey = "k")
    @JsonGetter("kmeans-k")
    public int getkMeansK() {
        return kMeansK;
    }

    @MCATParameter("kmeans-k")
    @JsonSetter("kmeans-k")
    public boolean setkMeansK(int kMeansK) {
        if (kMeansK <= 0) {
            return false;
        }
        this.kMeansK = kMeansK;
        eventBus.post(new ParameterChangedEvent(this, "kmeans-k"));
        return true;
    }

//    @MCATDocumentation(name = "Minimum length")
//    @MCATParameter(value = "min-length", shortKey = "mlength")
    @JsonGetter("min-length")
    public int getMinLength() {
        return minLength;
    }

//    @MCATParameter("min-length")
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
        return MCATCustomParameterCollection.parametersToString((new MCATTraversedParameterCollection(this)).getParameters().values(), "_", "-");
    }
    
    public String toShortenedString() {
		return "_k-" + kMeansK + "_hierarchy-" + clusteringHierarchy;
	}
}
