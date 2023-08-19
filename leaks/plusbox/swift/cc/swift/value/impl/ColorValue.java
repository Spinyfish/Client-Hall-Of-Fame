package cc.swift.value.impl;

import cc.swift.value.Value;

import java.awt.*;
import java.util.function.Supplier;

public final class ColorValue extends Value<Color> {

    public ColorValue(String name, Color defaultColor) {
        super(name, defaultColor);
    }

    public ColorValue setDependency(Supplier<Boolean> dependency) {
        this.dependency = dependency;
        return this;
    }
}
