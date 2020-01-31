package org.hkijena.mcat.api;

import java.nio.file.Path;

public abstract class MCATData {
    public abstract void saveTo(Path file);
}
