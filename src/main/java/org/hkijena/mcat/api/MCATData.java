package org.hkijena.mcat.api;

import java.nio.file.Path;

/**
 * Base class for data
 */
public interface MCATData {
    /**
     * Stores the data to the specified folder with provided filename
     *  @param folder the folder where the data is saved
     * @param fileName the file name
     *
     */
    void saveTo(Path folder, Path fileName);
}
