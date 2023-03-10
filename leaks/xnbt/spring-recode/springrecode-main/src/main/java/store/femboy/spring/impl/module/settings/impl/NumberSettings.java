package store.femboy.spring.impl.module.settings.impl;

import store.femboy.spring.impl.module.settings.Settings;

public final class NumberSettings extends Settings {
    public float value;
    public float min;
    public float max;
    public float step;

    public NumberSettings(String name, float value, float min, float max, float step) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public NumberSettings(String name,  float value, float min, float max, float step,  ModeSetting parent, String mode) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
        this.parent = parent;
        this.mode = mode;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        float precision = 1 / step;
        this.value = Math.round(Math.max(min, Math.min(max, value)) * precision) / precision;
    }

    public void step(boolean positive){
        setValue(getValue() + (positive ? 1 : -1) * step);
    }

    public double getMin(){
        return min;
    }

    public double setMin(float min){
        this.min = min;
        return min;
    }

    public double getMax(){
        return max;
    }

    public void setMax(float max){
        this.max = max;
    }

    public double getStep(){
        return step;
    }

    public void setStep(float step){
        this.step = step;
    }
}
