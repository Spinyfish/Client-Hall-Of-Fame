package dev.africa.pandaware.impl.setting;

import dev.africa.pandaware.api.setting.Setting;
import lombok.Getter;

import java.util.function.Supplier;

/*
 * Created by sage on 30/07/2021 at 17:54
 */
public class NumberRangeSetting extends Setting<Number[]> {
    @Getter
    private final Number max, min, increment;
    private Number firstValue, secondValue;

    public NumberRangeSetting(String name, Number max, Number min, Number firstValue, Number secondValue) {
        super(name, () -> true);
        this.max = max;
        this.min = min;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.increment = null;
    }

    public NumberRangeSetting(String name, Number max, Number min, Number firstValue, Number secondValue, Number increment) {
        super(name, () -> true);
        this.max = max;
        this.min = min;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.increment = (increment.doubleValue() <= 0 ? null : increment);
    }

    public NumberRangeSetting(String name, Number max, Number min, Number firstValue, Number secondValue, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.max = max;
        this.min = min;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.increment = null;
    }

    public NumberRangeSetting(String name, Number max, Number min, Number firstValue, Number secondValue, Number increment, Supplier<Boolean> supplier) {
        super(name, supplier);
        this.max = max;
        this.min = min;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.increment = (increment.doubleValue() <= 0 ? null : increment);
    }

    @Override
    public Number[] getValue() {
        return new Number[]{this.firstValue, this.secondValue};
    }

    @Override
    public void setValue(Number[] value) {
        this.firstValue = value[0];
        this.secondValue = value[1];
    }

    public Number getFirstValue() {
        return this.firstValue;
    }

    public Number getSecondValue() {
        return this.secondValue;
    }

    @Override
    public NumberRangeSetting setSaveConfig(boolean saveConfig) {
        return (NumberRangeSetting) super.setSaveConfig(saveConfig);
    }
}
