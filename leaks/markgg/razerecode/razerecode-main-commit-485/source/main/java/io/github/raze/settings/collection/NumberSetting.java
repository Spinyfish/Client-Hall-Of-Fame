package io.github.raze.settings.collection;

import io.github.raze.modules.system.BaseModule;
import io.github.raze.settings.system.BaseSetting;

public class NumberSetting extends BaseSetting {

    public Number min, max, value;

    public NumberSetting(BaseModule parent, String name, Number min, Number max, Number value) {
        this.parent = parent;
        this.name = name;
        this.min = min;
        this.max = max;
        this.value = value;
    }

    public Number min() {
        return min;
    }

    public Number max() {
        return max;
    }

    public Number get() {
        return value;
    }

    public void set(Number value) {
        change(value);
        onChange(this.value, value);
    }

    public void change(Number value) {
        this.value = value;
    }

    public void onChange(Number old, Number current) { /* */ };

}
