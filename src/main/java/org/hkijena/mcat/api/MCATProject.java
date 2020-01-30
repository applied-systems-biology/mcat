package org.hkijena.mcat.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.events.MCATSampleAddedEvent;
import org.hkijena.mcat.api.events.MCATSampleRemovedEvent;
import org.hkijena.mcat.api.events.MCATSampleRenamedEvent;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An ACAQ5 project.
 * It contains all information to setup and run an analysis
 */
public class MCATProject {

    private EventBus eventBus = new EventBus();
    private MCATPreprocessingParameters preprocessingParameters = new MCATPreprocessingParameters();
    private MCATClusteringParameters clusteringParameters = new MCATClusteringParameters();
    private MCATPostprocessingParameters postprocessingParameters = new MCATPostprocessingParameters();
    private BiMap<String, MCATSample> samples = HashBiMap.create();

    public MCATProject() {
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BiMap<String, MCATSample> getSamples() {
        return ImmutableBiMap.copyOf(samples);
    }

    public Map<String, Set<MCATSample>> getSamplesGroupedByTreatment() {
        Map<String, Set<MCATSample>> result = new HashMap<>();
        for(MCATSample sample : samples.values()) {
            if(!result.containsKey(sample.getParameters().getTreatment())) {
                result.put(sample.getParameters().getTreatment(), new HashSet<>());
            }
            result.get(sample.getParameters().getTreatment()).add(sample);
        }
        return result;
    }

    public MCATSample addSample(String sampleName) {
        if(samples.containsKey(sampleName)) {
            return samples.get(sampleName);
        }
        else {
            MCATSample sample = new MCATSample(this);
            samples.put(sampleName, sample);
            eventBus.post(new MCATSampleAddedEvent(sample));
            return sample;
        }
    }

    public boolean removeSample(MCATSample sample) {
        String name = sample.getName();
        if(samples.containsKey(name)) {
            samples.remove(name);
            eventBus.post(new MCATSampleRemovedEvent(sample));
            return true;
        }
        return false;
    }

    public boolean renameSample(MCATSample sample, String name) {
        if(name == null)
            return false;
        name = name.trim();
        if(name.isEmpty() || samples.containsKey(name))
            return false;
        samples.remove(sample.getName());
        samples.put(name, sample);
        eventBus.post(new MCATSampleRenamedEvent(sample));
        return true;
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }
}
