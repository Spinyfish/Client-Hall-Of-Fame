package cc.swift.value.impl;

import cc.swift.value.Value;

import java.util.*;
import java.util.function.Supplier;

public final class ModeValue<T> extends Value<T> {
    private final LinkedHashMap<String, T> values = new LinkedHashMap<>();

    @SafeVarargs
    public ModeValue(String name, T... values) {
        super(name, values[0]);
        for (T value : values) {
            this.values.put(value.toString(), value);
        }
    }

    public Collection<T> getValues() {
        return values.values();
    }

    public T getMode(String name) {
        return values.get(name);
    }

    public void setValueFromString(String string) {
        T value = values.get(string);
        if (value != null) {
            this.setValue(value);
        }
    }
    public void next() {
        List<T> list = new ArrayList<>(values.values());
        setValue(list.get((list.indexOf(getValue()) + 1) % list.size()));
    }

    public void previous() {
        List<T> list = new ArrayList<>(values.values());
        int index = list.indexOf(getValue()) - 1;
        if (index < 0)
            index = list.size() - 1;
        setValue(list.get(index));
    }

    public ModeValue<T> setDependency(Supplier<Boolean> dependency) {
        this.dependency = dependency;
        return this;
    }
}
