package me.jinthium.straight.impl.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import imgui.type.ImFloat;
import me.jinthium.straight.api.setting.Setting;

public class NumberSetting extends Setting {

    private final double maxValue, minValue, increment, defaultValue;
    private final ImFloat imEquivalent;

    @Expose
    @SerializedName("value")
    private Double value;

    public NumberSetting(String name, double defaultValue, double minValue, double maxValue, double increment) {
        this.name = name;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.increment = increment;
        this.imEquivalent = new ImFloat((float) defaultValue);
    }

    private static double clamp(double value, double min, double max) {
        value = Math.max(min, value);
        value = Math.min(max, value);
        return value;
    }

    public ImFloat getImEquivalent() {
        return imEquivalent;
    }

    public double getMaxValue() {
        return maxValue;
    }

    
    public double getMinValue() {
        return minValue;
    }

    
    public double getDefaultValue() {
        return defaultValue;
    }

    
    public Double getValue() {
        return value;
    }

    
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