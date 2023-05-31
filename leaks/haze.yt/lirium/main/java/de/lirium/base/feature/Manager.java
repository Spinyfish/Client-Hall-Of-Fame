package de.lirium.base.feature;

import java.lang.reflect.Type;
import java.util.ArrayList;

public interface Manager<T extends Feature> {
    ArrayList<T> getFeatures();
    <U extends T> U get(Class<U> clazz);
    T get(Type type);
}