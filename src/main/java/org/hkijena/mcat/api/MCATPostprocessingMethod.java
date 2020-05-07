package org.hkijena.mcat.api;

public enum MCATPostprocessingMethod {
    MaxDecrease,
    MaxIncrease,
    NetDecrease,
    NetIncrease;


    @Override
    public String toString() {
        switch (this) {
            case MaxDecrease:
                return "maximum increase";
            case MaxIncrease:
                return "maximum increase";
            case NetDecrease:
                return "net decrease";
            case NetIncrease:
                return "net increase";
            default:
                throw new RuntimeException();
        }
    }
}
