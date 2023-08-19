package cc.swift.value.impl;

import cc.swift.value.Value;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

public final class MultiValue<T> extends Value<T> {

    private final LinkedHashMap<String, Boolean> values = new LinkedHashMap<>();

    @SafeVarargs
    public MultiValue(String name, T... values) {
        super(name, values[0]);
        for(T val : values){
            this.values.put(val.toString(), false);
        }
    }

    public MultiValue<T> setDependency(Supplier<Boolean> dependency) {
        this.dependency = dependency;
        return this;
    }

    public void setValue(String key, boolean value) {
        values.put(key, value);
    }
}
