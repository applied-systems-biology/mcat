package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.events.ParameterChangedEvent;

/**
 * Row in {@link MCATParametersTable}
 */
public class MCATParametersTableRow implements MCATParameterCollection {
    private EventBus eventBus = new EventBus();
    private MCATPreprocessingParameters preprocessingParameters = new MCATPreprocessingParameters();
    private MCATPostprocessingParameters postprocessingParameters = new MCATPostprocessingParameters();
    private MCATClusteringParameters clusteringParameters = new MCATClusteringParameters();

    public MCATParametersTableRow() {
        preprocessingParameters.getEventBus().register(this);
        postprocessingParameters.getEventBus().register(this);
        clusteringParameters.getEventBus().register(this);
    }

    public MCATParametersTableRow(MCATParametersTableRow other) {
        this.preprocessingParameters = new MCATPreprocessingParameters(other.preprocessingParameters);
        this.postprocessingParameters = new MCATPostprocessingParameters(other.postprocessingParameters);
        this.clusteringParameters = new MCATClusteringParameters(other.clusteringParameters);
        preprocessingParameters.getEventBus().register(this);
        postprocessingParameters.getEventBus().register(this);
        clusteringParameters.getEventBus().register(this);
    }

    @MCATDocumentation(name = "Preprocessing", description = "Following parameters apply to the preprocessing.")
    @MCATParameter("preprocessing")
    @JsonGetter("preprocessing")
    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    @JsonSetter("preprocessing")
    public void setPreprocessingParameters(MCATPreprocessingParameters preprocessingParameters) {
        this.preprocessingParameters = preprocessingParameters;
    }

    @MCATDocumentation(name = "Postprocessing", description = "Following parameters apply to the postprocessing.")
    @MCATParameter("postprocessing")
    @JsonGetter("postprocessing")
    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }

    @JsonSetter("postprocessing")
    public void setPostprocessingParameters(MCATPostprocessingParameters postprocessingParameters) {
        this.postprocessingParameters = postprocessingParameters;
    }

    @MCATDocumentation(name = "Clustering", description = "Following parameters apply to the clustering.")
    @MCATParameter("clustering")
    @JsonGetter("clustering")
    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }

    @JsonSetter("clustering")
    public void setClusteringParameters(MCATClusteringParameters clusteringParameters) {
        this.clusteringParameters = clusteringParameters;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Subscribe
    public void onParameterChanged(ParameterChangedEvent event) {
        eventBus.post(event);
    }
}
