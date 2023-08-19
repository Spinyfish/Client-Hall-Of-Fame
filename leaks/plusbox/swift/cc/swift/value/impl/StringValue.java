package cc.swift.value.impl;

import cc.swift.value.Value;

import java.util.function.Supplier;

public final class StringValue extends Value<String> {
    public StringValue(String name, String defaultValue) {
        super(name, defaultValue);
    }

    public StringValue setDependency(Supplier<Boolean> dependency) {
        this.dependency = dependency;
        return this;
    }
}
