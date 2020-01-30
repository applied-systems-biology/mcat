package org.hkijena.mcat.api.parameters;

import org.hkijena.mcat.api.MCATParameters;

public class MCATPreprocessingParameters extends MCATParameters  {
    private int downsamplingFactor = 4;
    private int channelOfInterest = 4;
    private int anatomicChannel = 0;
    private int minTime = 0;
    private int maxTime = Integer.MAX_VALUE;
}
