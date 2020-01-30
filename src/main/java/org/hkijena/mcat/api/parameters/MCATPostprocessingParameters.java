package org.hkijena.mcat.api.parameters;

import org.hkijena.mcat.api.MCATParameters;

public class MCATPostprocessingParameters extends MCATParameters {
    private boolean analyzeNetIncrease = true;
    private boolean analyzeNetDecrease = true;
    private boolean analyzeMaxIncrease = true;
    private boolean analyzeMaxDecrease = true;
    private boolean performClusterMorphologyAnalysis = true;
}
