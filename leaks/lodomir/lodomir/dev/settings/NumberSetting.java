/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.settings;

import lodomir.dev.settings.Setting;

public class NumberSetting
extends Setting {
    private double min;
    private double max;
    private double increment;
    private double value;

    public NumberSetting(String name, double min, double max, double defaultValue, double increment) {
        super(name);
        this.min = min;
        this.max = max;
        this.value = defaultValue;
        this.increment = increment;
    }

    public static double clamp(double value, double min, double max) {
        value = Math.max(min, value);
        value = Math.min(max, value);
        return value;
    }

    public double getValue() {
        return this.value;
    }

    public float getValueFloat() {
        return (float)this.value;
    }

    public int getValueInt() {
        return (int)this.value;
    }

    public void setValue(double value) {
        value = NumberSetting.clamp(value, this.min, this.max);
        this.value = value = (double)Math.round(value * (1.0 / this.increment)) / (1.0 / this.increment);
    }

    public double getIncrement() {
        return this.increment;
    }

    public void increment(boolean positive) {
        if (positive) {
            this.setValue(this.getValue() + this.getIncrement());
        } else {
            this.setValue(this.getValue() - this.getIncrement());
        }
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }
}

