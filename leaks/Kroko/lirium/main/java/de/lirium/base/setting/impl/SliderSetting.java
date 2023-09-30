package de.lirium.base.setting.impl;

import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.ISetting;

public class SliderSetting<T extends Number> extends ISetting<T> {
    public T min, max, increment, defaultValue;

    public SliderSetting(T value, T min, T max, T increment, Dependency<?>... dependencies) {
        super(value, dependencies);
        this.defaultValue = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public SliderSetting(T value, T min, T max, T increment) {
        super(value);
        this.defaultValue = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public SliderSetting(T value, T min, T max, Dependency<?>... dependencies) {
        super(value, dependencies);
        this.defaultValue = value;
        this.min = min;
        this.max = max;
        this.increment = null;
    }

    public SliderSetting(T value, T min, T max) {
        super(value);
        this.defaultValue = value;
        this.min = min;
        this.max = max;
        this.increment = null;
    }
}
