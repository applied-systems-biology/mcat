package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hkijena.mcat.api.MCATParameters;

/**
 * Contains preprocessing parameters
 */
public class MCATPreprocessingParameters extends MCATParameters  {
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

    @JsonGetter("downsampling-factor")
    public int getDownsamplingFactor() {
        return downsamplingFactor;
    }

    @JsonSetter("downsampling-factor")
    public void setDownsamplingFactor(int downsamplingFactor) {
        this.downsamplingFactor = downsamplingFactor;
        postChangedEvent("downsampling-factor");
    }

    @JsonGetter("channel-of-interest")
    public int getChannelOfInterest() {
        return channelOfInterest;
    }

    @JsonSetter("channel-of-interest")
    public void setChannelOfInterest(int channelOfInterest) {
        this.channelOfInterest = channelOfInterest;
        postChangedEvent("channel-of-interest");
    }

    @JsonGetter("anatomic-channel")
    public int getAnatomicChannel() {
        return anatomicChannel;
    }

    @JsonSetter("anatomic-channel")
    public void setAnatomicChannel(int anatomicChannel) {
        this.anatomicChannel = anatomicChannel;
        postChangedEvent("anatomic-channel");
    }
    
    @JsonGetter("save-raw-image")
    public boolean isSaveRawImage() {
        return saveRawImage;
    }

    @JsonSetter("save-raw-image")
    public void setSaveRawImage(boolean saveRawImage) {
        this.saveRawImage = saveRawImage;
        postChangedEvent("save-raw-image");
    }
    
    @JsonGetter("save-roi")
    public boolean isSaveRoi() {
        return saveRoi;
    }

    @JsonSetter("save-roi")
    public void setSaveRoi(boolean saveRoi) {
        this.saveRoi = saveRoi;
        postChangedEvent("save-roi");
    }

    @JsonGetter("min-time")
    public int getMinTime() {
        return minTime;
    }

    @JsonSetter("min-time")
    public void setMinTime(int minTime) {
        this.minTime = minTime;
        postChangedEvent("min-time");
    }

    @JsonGetter("max-time")
    public int getMaxTime() {
        return maxTime;
    }

    @JsonSetter("max-time")
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
        postChangedEvent("max-time");
    }
}
