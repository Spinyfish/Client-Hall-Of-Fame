package dev.africa.pandaware.impl.setting;

import dev.africa.pandaware.api.setting.Setting;
import lombok.Getter;

import java.util.function.Supplier;

public class NumberSetting extends Setting<Number> {
    @Getter
    private final Number max, min, increment;
    private Number value;

    public NumberSetting(String name, Number max, Number min, Number value) {
        super(name, () -> true);
        this.max = max;
        this.min = min;
        this.value = value;
        this.increment = null;
    }

    public NumberSetting(String name, Number max, Number min, Number value, Number increment) {
        super(name, () -> true);
        this.max = max;
        this.min = min;
        this.value = value;
        this.increment = (increment.doubleValue() <= 0 ? null : increment);
    }

    public NumberSetting(String name, Number max, Number min, Number value, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.max = max;
        this.min = min;
        this.value = value;
        this.increment = null;
    }

    public NumberSetting(String name, Number max, Number min, Number value, Number increment, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.max = max;
        this.min = min;
        this.value = value;
        this.increment = (increment.doubleValue() <= 0 ? null : increment);
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public void setValue(Number value) {
        this.value = value;
    }

    @Override
    public NumberSetting setSaveConfig(boolean saveConfig) {
        return (NumberSetting) super.setSaveConfig(saveConfig);
    }
}
