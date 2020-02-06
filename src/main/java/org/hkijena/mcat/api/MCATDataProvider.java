package org.hkijena.mcat.api;

import org.apache.commons.lang.reflect.ConstructorUtils;

import java.beans.Expression;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * An interface that generates data
 * @param <T>
 */
public interface MCATDataProvider<T extends MCATData> extends MCATValidatable {
    T get();
    String getName();
    boolean providesData();

    static <U extends MCATData> MCATDataProvider<U> duplicate(MCATDataProvider<U> other) {
        try {
            Constructor<?> constructor = ConstructorUtils.getMatchingAccessibleConstructor(other.getClass(), new Class[]{ other.getClass() });
            return (MCATDataProvider<U>) constructor.newInstance(other);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
