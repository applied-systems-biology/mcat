package org.hkijena.mcat.api;

/**
 * An interface that generates data
 * @param <T>
 */
public interface MCATDataProvider<T extends MCATData> extends MCATValidatable {
    T get();
    String getName();
}
