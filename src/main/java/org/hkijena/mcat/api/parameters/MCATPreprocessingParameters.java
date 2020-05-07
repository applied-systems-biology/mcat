package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.utils.api.ACAQDocumentation;
import org.hkijena.mcat.utils.api.events.ParameterChangedEvent;
import org.hkijena.mcat.utils.api.parameters.ACAQParameter;
import org.hkijena.mcat.utils.api.parameters.ACAQParameterCollection;

/**
 * Contains preprocessing parameters
 */
public class MCATPreprocessingParameters implements ACAQParameterCollection {
    private EventBus eventBus = new EventBus();
    private int downsamplingFactor = 4;
    private int channelOfInterest = 2;
    private int anatomicChannel = 1;
    private boolean saveRawImage = true;
    private boolean saveRoi = true;
    private int minTime = 0;
    private int maxTime = Integer.MAX_VALUE;

    public MCATPreprocessingParameters() {

    }

    public MCATPreprocessingParameters(MCATPreprocessingParameters other) {
        this.downsamplingFactor = other.downsamplingFactor;
        this.channelOfInterest = other.channelOfInterest;
        this.anatomicChannel = other.anatomicChannel;
        this.saveRawImage = other.saveRawImage;
        this.saveRoi = other.saveRoi;
        this.minTime = other.minTime;
        this.maxTime = other.maxTime;
    }

    @ACAQDocumentation(name = "Downsamping factor")
    @JsonGetter("downsampling-factor")
    @ACAQParameter("downsampling-factor")
    public int getDownsamplingFactor() {
        return downsamplingFactor;
    }

    @JsonSetter("downsampling-factor")
    @ACAQParameter("downsampling-factor")
    public void setDownsamplingFactor(int downsamplingFactor) {
        this.downsamplingFactor = downsamplingFactor;
        eventBus.post(new ParameterChangedEvent(this, "downsampling-factor"));
    }

    @ACAQDocumentation(name = "Channel of interest")
    @JsonGetter("channel-of-interest")
    @ACAQParameter("channel-of-interest")
    public int getChannelOfInterest() {
        return channelOfInterest;
    }

    @JsonSetter("channel-of-interest")
    @ACAQParameter("channel-of-interest")
    public void setChannelOfInterest(int channelOfInterest) {
        this.channelOfInterest = channelOfInterest;
        eventBus.post(new ParameterChangedEvent(this, "channel-of-interest"));
    }

    @ACAQDocumentation(name = "Anatomic channel")
    @JsonGetter("anatomic-channel")
    @ACAQParameter("anatomic-channel")
    public int getAnatomicChannel() {
        return anatomicChannel;
    }

    @JsonSetter("anatomic-channel")
    @ACAQParameter("anatomic-channel")
    public void setAnatomicChannel(int anatomicChannel) {
        this.anatomicChannel = anatomicChannel;
        eventBus.post(new ParameterChangedEvent(this, "anatomic-channel"));
    }

    @ACAQDocumentation(name = "Save raw image")
    @JsonGetter("save-raw-image")
    @ACAQParameter("save-raw-image")
    public boolean isSaveRawImage() {
        return saveRawImage;
    }

    @JsonSetter("save-raw-image")
    @ACAQParameter("save-raw-image")
    public void setSaveRawImage(boolean saveRawImage) {
        this.saveRawImage = saveRawImage;
        eventBus.post(new ParameterChangedEvent(this, "save-raw-image"));
    }

    @ACAQDocumentation(name = "Save ROI")
    @JsonGetter("save-roi")
    @ACAQParameter("save-roi")
    public boolean isSaveRoi() {
        return saveRoi;
    }

    @JsonSetter("save-roi")
    @ACAQParameter("save-roi")
    public void setSaveRoi(boolean saveRoi) {
        this.saveRoi = saveRoi;
        eventBus.post(new ParameterChangedEvent(this, "save-roi"));
    }

    @ACAQDocumentation(name = "Minimum time")
    @JsonGetter("min-time")
    @ACAQParameter("min-time")
    public int getMinTime() {
        return minTime;
    }

    @JsonSetter("min-time")
    @ACAQParameter("min-time")
    public void setMinTime(int minTime) {
        this.minTime = minTime;
        eventBus.post(new ParameterChangedEvent(this, "min-time"));
    }

    @ACAQDocumentation(name = "Maximum time")
    @JsonGetter("max-time")
    @ACAQParameter("max-time")
    public int getMaxTime() {
        return maxTime;
    }

    @JsonSetter("max-time")
    @ACAQParameter("max-time")
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
        eventBus.post(new ParameterChangedEvent(this, "max-time"));
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
