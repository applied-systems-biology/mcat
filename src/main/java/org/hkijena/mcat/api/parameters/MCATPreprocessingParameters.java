package org.hkijena.mcat.api.parameters;

import org.hkijena.mcat.api.MCATParameters;

public class MCATPreprocessingParameters extends MCATParameters  {
    private int downsamplingFactor = 4;
    private int channelOfInterest = 4;
    private int anatomicChannel = 0;
    private int minTime = 0;
    private int maxTime = Integer.MAX_VALUE;

    public int getDownsamplingFactor() {
        return downsamplingFactor;
    }

    public void setDownsamplingFactor(int downsamplingFactor) {
        this.downsamplingFactor = downsamplingFactor;
        postChangedEvent("downsampling-factor");
    }

    public int getChannelOfInterest() {
        return channelOfInterest;
    }

    public void setChannelOfInterest(int channelOfInterest) {
        this.channelOfInterest = channelOfInterest;
        postChangedEvent("channel-of-interest");
    }

    public int getAnatomicChannel() {
        return anatomicChannel;
    }

    public void setAnatomicChannel(int anatomicChannel) {
        this.anatomicChannel = anatomicChannel;
        postChangedEvent("anatomic-channel");
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
        postChangedEvent("min-time");
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
        postChangedEvent("max-time");
    }
}
