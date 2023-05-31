package de.lirium.base.setting.impl;

import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.ISetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ComboBox<T> extends ISetting<T> {
    public final ArrayList<T> modes = new ArrayList<>();

    public ComboBox(T value, T[] modes, Dependency<?>... dependencies) {
        super(value, dependencies);
        this.modes.add(value);
        this.modes.addAll(Arrays.asList(modes));
        this.modes.sort(Comparator.comparing(Object::toString));
    }

    public ComboBox(T value, T[] modes) {
        super(value);
        this.modes.add(value);
        this.modes.addAll(Arrays.asList(modes));
        this.modes.sort(Comparator.comparing(Object::toString));
    }
}
