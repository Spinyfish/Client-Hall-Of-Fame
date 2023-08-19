package cc.swift.value.impl;

import cc.swift.value.Value;

import java.util.function.Supplier;

public final class BooleanValue extends Value<Boolean> {

    public BooleanValue(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    public BooleanValue setDependency(Supplier<Boolean> dependency) {
        this.dependency = dependency;
        return this;
    }
}
