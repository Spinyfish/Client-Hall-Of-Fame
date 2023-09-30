package dev.tenacity.module.settings.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.tenacity.module.settings.Setting;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class NumberSetting extends Setting {

    private final double maxValue, minValue, increment, defaultValue;

    @Expose
    @SerializedName("value")
    private Double value;

    public NumberSetting(String name, double defaultValue, double maxValue, double minValue, double increment) {
        this.name = name;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.increment = increment;
    }

    private static double clamp(double value, double min, double max) {
        value = Math.max(min, value);
        value = Math.min(max, value);
        return value;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getMaxValue() {
        return maxValue;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getMinValue() {
        return minValue;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getDefaultValue() {
        return defaultValue;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public Double getValue() {
        return value;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setValue(double value) {
        value = clamp(value, this.minValue, this.maxValue);
        value = Math.round(value * (1.0 / this.increment)) / (1.0 / this.increment);
        this.value = value;
    }

    public double getIncrement() {
        return increment;
    }

    @Override
    public Double getConfigValue() {
        return getValue();
    }

}
