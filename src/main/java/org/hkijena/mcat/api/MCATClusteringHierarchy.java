package org.hkijena.mcat.api;

public enum MCATClusteringHierarchy {
    AllInOne,
    PerTreatment,
    PerSubject;


    @Override
    public String toString() {
        switch(this) {
            case AllInOne:
                return "All in one";
            case PerTreatment:
                return "Per treatment";
            case PerSubject:
                return "Per subject";
            default:
                throw new RuntimeException();
        }
    }
}
