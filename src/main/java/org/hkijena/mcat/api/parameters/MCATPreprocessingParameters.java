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

import java.util.Objects;

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
    public static final int MIN_TIME_DEFAULT = 1, MAX_TIME_DEFAULT = Integer.MAX_VALUE;

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

    @MCATDocumentation(name = "Smoothing factor", description = "res:///org/hkijena/mcat/documentation/parameter_preprocessing_downsampling_factor.md")
    @JsonGetter("downsampling-factor")
    @MCATParameter(value = "downsampling-factor", shortKey = "smooth", uiOrder = 3)
    public int getDownsamplingFactor() {
        return downsamplingFactor;
    }

    @JsonSetter("downsampling-factor")
    @MCATParameter("downsampling-factor")
    public boolean setDownsamplingFactor(int downsamplingFactor) {
        if (downsamplingFactor <= 0) {
            return false;
        }
        this.downsamplingFactor = downsamplingFactor;
        eventBus.post(new ParameterChangedEvent(this, "downsampling-factor"));
        return true;
    }

    @MCATDocumentation(name = "Signal channel", description = "res:///org/hkijena/mcat/documentation/parameter_preprocessing_channel_of_interest.md")
    @JsonGetter("channel-of-interest")
    @MCATParameter(value = "channel-of-interest", shortKey = "signalCh", uiOrder = 2)
    public int getChannelOfInterest() {
        return channelOfInterest;
    }

    @JsonSetter("channel-of-interest")
    @MCATParameter("channel-of-interest")
    public boolean setChannelOfInterest(int channelOfInterest) {
        if (channelOfInterest <= 0) {
            return false;
        }
        this.channelOfInterest = channelOfInterest;
        eventBus.post(new ParameterChangedEvent(this, "channel-of-interest"));
        return true;
    }

    @MCATDocumentation(name = "Anatomy channel", description = "res:///org/hkijena/mcat/documentation/parameter_preprocessing_anatomic_channel.md")
    @JsonGetter("anatomic-channel")
    @MCATParameter(value = "anatomic-channel", shortKey = "anatomyCh", uiOrder = 1)
    public int getAnatomicChannel() {
        return anatomicChannel;
    }

    @JsonSetter("anatomic-channel")
    @MCATParameter("anatomic-channel")
    public boolean setAnatomicChannel(int anatomicChannel) {
        if (anatomicChannel < 0) {
            return false;
        }
        this.anatomicChannel = anatomicChannel;
        eventBus.post(new ParameterChangedEvent(this, "anatomic-channel"));
        return true;
    }

    @MCATDocumentation(name = "Save raw image", description = "res:///org/hkijena/mcat/documentation/parameter_preprocessing_save_raw_image.md")
    @JsonGetter("save-raw-image")
    @MCATParameter(value = "save-raw-image", shortKey = "sRaw", uiOrder = 7)
    public boolean isSaveRawImage() {
        return saveRawImage;
    }

    @JsonSetter("save-raw-image")
    @MCATParameter("save-raw-image")
    public void setSaveRawImage(boolean saveRawImage) {
        this.saveRawImage = saveRawImage;
        eventBus.post(new ParameterChangedEvent(this, "save-raw-image"));
    }

    @MCATDocumentation(name = "Save ROI", description = "res:///org/hkijena/mcat/documentation/parameter_preprocessing_save_roi.md")
    @JsonGetter("save-roi")
    @MCATParameter(value = "save-roi", shortKey = "sRoi", uiOrder = 6)
    public boolean isSaveRoi() {
        return saveRoi;
    }

    @JsonSetter("save-roi")
    @MCATParameter("save-roi")
    public void setSaveRoi(boolean saveRoi) {
        this.saveRoi = saveRoi;
        eventBus.post(new ParameterChangedEvent(this, "save-roi"));
    }

    @MCATDocumentation(name = "Start time frame", description = "res:///org/hkijena/mcat/documentation/parameter_preprocessing_minimum_time.md")
    @JsonGetter("min-time")
    @MCATParameter(value = "min-time", shortKey = "startTime", uiOrder = 4)
    public int getMinTime() {
        return minTime;
    }

    @JsonSetter("min-time")
    @MCATParameter("min-time")
    public boolean setMinTime(int minTime) {
        if (minTime < 1) {
            return false;
        }
        this.minTime = minTime;
        eventBus.post(new ParameterChangedEvent(this, "min-time"));
        return true;
    }

    @MCATDocumentation(name = "End time frame", description = "res:///org/hkijena/mcat/documentation/parameter_preprocessing_maximum_time.md")
    @JsonGetter("max-time")
    @MCATParameter(value = "max-time", shortKey = "endTime", uiOrder = 5)
    public int getMaxTime() {
        return maxTime;
    }

    @JsonSetter("max-time")
    @MCATParameter("max-time")
    public boolean setMaxTime(int maxTime) {
        if (maxTime < 1) {
            return false;
        }
        this.maxTime = maxTime;
        eventBus.post(new ParameterChangedEvent(this, "max-time"));
        return true;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public String toString() {
        return MCATCustomParameterCollection.parametersToString((new MCATTraversedParameterCollection(this)).getParameters().values(), "_", "-");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCATPreprocessingParameters that = (MCATPreprocessingParameters) o;
        return downsamplingFactor == that.downsamplingFactor &&
                channelOfInterest == that.channelOfInterest &&
                anatomicChannel == that.anatomicChannel &&
                saveRawImage == that.saveRawImage &&
                saveRoi == that.saveRoi &&
                minTime == that.minTime &&
                maxTime == that.maxTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(downsamplingFactor, channelOfInterest, anatomicChannel, saveRawImage, saveRoi, minTime, maxTime);
    }

    public String toShortenedString() {
        String minT = minTime == MIN_TIME_DEFAULT ? "" : "_minTime-" + minTime;
        String maxT = maxTime == MAX_TIME_DEFAULT ? "" : "_maxTime-" + maxTime;

        return "_anatomyCh-" + anatomicChannel + "_signalCh-" + channelOfInterest +
                "_down-" + downsamplingFactor + minT + maxT;
    }
}
