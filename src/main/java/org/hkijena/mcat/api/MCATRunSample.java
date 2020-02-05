package org.hkijena.mcat.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import org.hkijena.mcat.api.datainterfaces.MCATClusteredDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessedDataInterface;

import java.util.ArrayList;
import java.util.List;

public class MCATRunSample implements MCATDataInterface, Comparable<MCATRunSample> {
    private MCATRun run;

    private MCATClusteredDataInterface clusteredDataInterface;
    private MCATPostprocessedDataInterface postprocessedDataInterface;
    private BiMap<String, MCATRunSampleSubject> subjects = HashBiMap.create();

    public MCATRunSample(MCATRun run, List<MCATProjectSample> sources) {
        this.run = run;

        // Initialize subjects
        for(MCATProjectSample sample : sources) {
            subjects.put(sample.getName(), new MCATRunSampleSubject(this, sample));
        }

        // Initialize from source sample
        if(sources.size() == 1) {
            MCATProjectSample source = sources.get(0);
            this.clusteredDataInterface = new MCATClusteredDataInterface(source.getClusteredDataInterface());
            this.postprocessedDataInterface = new MCATPostprocessedDataInterface(source.getPostprocessedDataInterface());
        }
        else {
            // If we have multiple source samples, we cannot do this
            this.clusteredDataInterface = new MCATClusteredDataInterface();
            this.postprocessedDataInterface = new MCATPostprocessedDataInterface();
        }

    }

    public MCATRun getRun() {
        return run;
    }

    public MCATClusteredDataInterface getClusteredDataInterface() {
        return clusteredDataInterface;
    }

    public MCATPostprocessedDataInterface getPostprocessedDataInterface() {
        return postprocessedDataInterface;
    }

    public String getName() {
        return  getRun().getSamples().inverse().get(this);
    }

    public BiMap<String, MCATRunSampleSubject> getSubjects() {
        return ImmutableBiMap.copyOf(subjects);
    }

    @Override
    public List<MCATDataSlot<?>> getSlots() {
        List<MCATDataSlot<?>> result = new ArrayList<>();
        result.addAll(clusteredDataInterface.getSlots());
        result.addAll(postprocessedDataInterface.getSlots());
        return result;
    }

    @Override
    public int compareTo(MCATRunSample o) {
        return getName().compareTo(o.getName());
    }
}
