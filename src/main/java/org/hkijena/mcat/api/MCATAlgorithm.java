package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datainterfaces.MCATClusteredDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;

/**
 * Base class for an algorithm node
 * Please use the provided properties to access data and parameters to later allow easy extension to hyperparameters
 */
public abstract class MCATAlgorithm implements MCATValidatable, Runnable {

    private MCATRun run;

    public MCATAlgorithm(MCATRun run) {
        this.run = run;
    }

    public abstract void run();

    public abstract String getName();

    public MCATRun getRun() {
        return run;
    }
}
