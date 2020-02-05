package org.hkijena.mcat.api.algorithms;

import org.hkijena.mcat.api.*;

public class MCATPreprocessingAlgorithm extends MCATPerSubjectAlgorithm {

    public MCATPreprocessingAlgorithm(MCATRunSampleSubject subject) {
        super(subject);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Preprocessing " + getSample().getName() + "/" + getSubject().getName();
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return new MCATValidityReport(this, "Preprocessing", true, "");
    }
}
