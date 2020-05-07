package org.hkijena.mcat.api;

import java.nio.file.Path;

/**
 * Base class for data
 */
public interface MCATData {
    /**
     * Stores the data to the specified <strong>folder</strong>
     *
     * @param folder
     * @param name   optional name that is considered during generating the output file name
     */
    void saveTo(Path folder, String name, String identifier);
}
