package org.hkijena.mcat.api;

import java.nio.file.Path;

/**
 * Base class for data
 */
public abstract class MCATData {
    public abstract void saveTo(Path file);
}
