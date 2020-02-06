package org.hkijena.mcat.api;

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
