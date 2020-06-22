package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.events.ParameterChangedEvent;

/**
 * Contains preprocessing parameters
 * <p>
 * To create a parameter, create a a private field with getter & setter.
 * Annotate the getter with {@link JsonGetter}, {@link MCATParameter}, and {@link MCATDocumentation}
 * Annotate the setter with {@link MCATParameter} and {@link JsonSetter}
 * <p>
 * Post an event {@link ParameterChangedEvent} when a value is set.
 * <p>
 * Add the variable to getHashCode() and equals()
 */
public class MCATPreprocessingParameters implements MCATParameterCollection {
	public static final int MIN_TIME_DEFAULT = 0, MAX_TIME_DEFAULT = Integer.MAX_VALUE;
	
    private EventBus eventBus = new EventBus();
    private int downsamplingFactor = 4;
    private int channelOfInterest = 2;
    private int anatomicChannel = 1;
    private boolean saveRawImage = true;
    private boolean saveRoi = true;
    private int minTime = MIN_TIME_DEFAULT;
    private int maxTime = MAX_TIME_DEFAULT;

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

    @MCATDocumentation(name = "Downsamping factor")
    @JsonGetter("downsampling-factor")
    @MCATParameter(value = "downsampling-factor", shortKey = "down")
    public int getDownsamplingFactor() {
        return downsamplingFactor;
    }

    @JsonSetter("downsampling-factor")
    @MCATParameter("downsampling-factor")
    public void setDownsamplingFactor(int downsamplingFactor) {
        this.downsamplingFactor = downsamplingFactor;
        eventBus.post(new ParameterChangedEvent(this, "downsampling-factor"));
    }

    @MCATDocumentation(name = "Channel of interest")
    @JsonGetter("channel-of-interest")
    @MCATParameter(value = "channel-of-interest", shortKey = "signalCh")
    public int getChannelOfInterest() {
        return channelOfInterest;
    }

    @JsonSetter("channel-of-interest")
    @MCATParameter("channel-of-interest")
    public void setChannelOfInterest(int channelOfInterest) {
        this.channelOfInterest = channelOfInterest;
        eventBus.post(new ParameterChangedEvent(this, "channel-of-interest"));
    }

    @MCATDocumentation(name = "Anatomic channel")
    @JsonGetter("anatomic-channel")
    @MCATParameter(value = "anatomic-channel", shortKey = "anatomyCh")
    public int getAnatomicChannel() {
        return anatomicChannel;
    }

    @JsonSetter("anatomic-channel")
    @MCATParameter("anatomic-channel")
    public void setAnatomicChannel(int anatomicChannel) {
        this.anatomicChannel = anatomicChannel;
        eventBus.post(new ParameterChangedEvent(this, "anatomic-channel"));
    }

    @MCATDocumentation(name = "Save raw image")
    @JsonGetter("save-raw-image")
    @MCATParameter(value = "save-raw-image", shortKey = "sRaw")
    public boolean isSaveRawImage() {
        return saveRawImage;
    }

    @JsonSetter("save-raw-image")
    @MCATParameter("save-raw-image")
    public void setSaveRawImage(boolean saveRawImage) {
        this.saveRawImage = saveRawImage;
        eventBus.post(new ParameterChangedEvent(this, "save-raw-image"));
    }

    @MCATDocumentation(name = "Save ROI")
    @JsonGetter("save-roi")
    @MCATParameter(value = "save-roi", shortKey = "sRoi")
    public boolean isSaveRoi() {
        return saveRoi;
    }

    @JsonSetter("save-roi")
    @MCATParameter("save-roi")
    public void setSaveRoi(boolean saveRoi) {
        this.saveRoi = saveRoi;
        eventBus.post(new ParameterChangedEvent(this, "save-roi"));
    }

    @MCATDocumentation(name = "Minimum time")
    @JsonGetter("min-time")
    @MCATParameter(value = "min-time", shortKey = "minTime")
    public int getMinTime() {
        return minTime;
    }

    @JsonSetter("min-time")
    @MCATParameter("min-time")
    public void setMinTime(int minTime) {
        this.minTime = minTime;
        eventBus.post(new ParameterChangedEvent(this, "min-time"));
    }

    @MCATDocumentation(name = "Maximum time")
    @JsonGetter("max-time")
    @MCATParameter(value = "max-time", shortKey = "maxTime")
    public int getMaxTime() {
        return maxTime;
    }

    @JsonSetter("max-time")
    @MCATParameter("max-time")
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
        eventBus.post(new ParameterChangedEvent(this, "max-time"));
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
		String minT = minTime == MIN_TIME_DEFAULT? "" : "_minTime-" + minTime;
		String maxT = maxTime == MAX_TIME_DEFAULT? "" : "_maxTime-" + maxTime;
		
		return "_anatomyCh-" + anatomicChannel + "_signalCh-" + channelOfInterest + 
				"_down-" + downsamplingFactor + minT + maxT;
	}
}
