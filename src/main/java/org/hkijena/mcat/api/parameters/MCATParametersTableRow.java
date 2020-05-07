package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.utils.api.ACAQDocumentation;
import org.hkijena.mcat.utils.api.parameters.ACAQParameter;
import org.hkijena.mcat.utils.api.parameters.ACAQParameterCollection;

/**
 * Row in {@link MCATParametersTable}
 */
public class MCATParametersTableRow implements ACAQParameterCollection {
    private EventBus eventBus = new EventBus();
    private MCATPreprocessingParameters preprocessingParameters = new MCATPreprocessingParameters();
    private MCATPostprocessingParameters postprocessingParameters = new MCATPostprocessingParameters();
    private MCATClusteringParameters clusteringParameters = new MCATClusteringParameters();

    public MCATParametersTableRow() {

    }

    public MCATParametersTableRow(MCATParametersTableRow other) {
        this.preprocessingParameters = new MCATPreprocessingParameters(other.preprocessingParameters);
        this.postprocessingParameters = new MCATPostprocessingParameters(other.postprocessingParameters);
        this.clusteringParameters = new MCATClusteringParameters(other.clusteringParameters);
    }

    @ACAQDocumentation(name = "Preprocessing", description = "Following parameters apply to the preprocessing.")
    @ACAQParameter("preprocessing")
    @JsonGetter("preprocessing")
    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    @ACAQDocumentation(name = "Postprocessing", description = "Following parameters apply to the postprocessing.")
    @ACAQParameter("postprocessing")
    @JsonGetter("postprocessing")
    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }

    @ACAQDocumentation(name = "Clustering", description = "Following parameters apply to the clustering.")
    @ACAQParameter("clustering")
    @JsonGetter("clustering")
    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }

    @JsonSetter("preprocessing")
    public void setPreprocessingParameters(MCATPreprocessingParameters preprocessingParameters) {
        this.preprocessingParameters = preprocessingParameters;
    }

    @JsonSetter("postprocessing")
    public void setPostprocessingParameters(MCATPostprocessingParameters postprocessingParameters) {
        this.postprocessingParameters = postprocessingParameters;
    }

    @JsonSetter("clustering")
    public void setClusteringParameters(MCATClusteringParameters clusteringParameters) {
        this.clusteringParameters = clusteringParameters;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
