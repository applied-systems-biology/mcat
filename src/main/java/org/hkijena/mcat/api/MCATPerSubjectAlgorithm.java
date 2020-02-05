package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;

public abstract class MCATPerSubjectAlgorithm extends MCATPerSampleAlgorithm {
    private MCATRunSampleSubject subject;

    public MCATPerSubjectAlgorithm(MCATRunSampleSubject subject) {
        super(subject.getSample());
        this.subject = subject;
    }

    public MCATRunSampleSubject getSubject() {
        return subject;
    }

    public MCATSampleParameters getParameters() {
        return subject.getParameters();
    }

    public MCATRawDataInterface getRawDataInterface() {
        return subject.getRawDataInterface();
    }

    public MCATPreprocessedDataInterface getPreprocessedDataInterface() {
        return subject.getPreprocessedDataInterface();
    }
}
