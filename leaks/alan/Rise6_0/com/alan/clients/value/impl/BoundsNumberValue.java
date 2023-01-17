package com.alan.clients.value.impl;

import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.module.Module;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

/**
 * @author Patrick
 * @since 10/19/2021
 */
@Getter
@Setter
public class BoundsNumberValue extends Value<Number> {

    private final Number min;
    private final Number max;
    private final Number decimalPlaces;
    private Number secondValue;

    public BoundsNumberValue(final String name, final Module parent,
                             final Number defaultValue, final Number defaultSecondValue,
                             final Number min, final Number max, final Number step) {
        super(name, parent, defaultValue);
        this.decimalPlaces = step;

        this.min = min;
        this.max = max;
        this.secondValue = defaultSecondValue;
    }

    public BoundsNumberValue(final String name, final Mode<?> parent,
                             final Number defaultValue, final Number defaultSecondValue,
                             final Number min, final Number max, final Number step) {
        super(name, parent, defaultValue);
        this.decimalPlaces = step;

        this.min = min;
        this.max = max;
        this.secondValue = defaultSecondValue;
    }

    public BoundsNumberValue(final String name, final Module parent,
                             final Number defaultValue, final Number defaultSecondValue,
                             final Number min, final Number max, final Number step, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
        this.decimalPlaces = step;

        this.min = min;
        this.max = max;
        this.secondValue = defaultSecondValue;
    }

    public BoundsNumberValue(final String name, final Mode<?> parent,
                             final Number defaultValue, final Number defaultSecondValue,
                             final Number min, final Number max, final Number step, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
        this.decimalPlaces = step;

        this.min = min;
        this.max = max;
        this.secondValue = defaultSecondValue;
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    public Number getRandomBetween() {
        long min = this.min.longValue();
        long max = this.max.longValue();

        if (min == max) {
            return min;
        } else if (min > max) {
            final long d = min;
            min = max;
            max = d;
        }

        long random = ThreadLocalRandom.current().nextLong(min, max);
        return new Number() {
            @Override
            public int intValue() {
                return (int) random;
            }

            @Override
            public long longValue() {
                return random;
            }

            @Override
            public float floatValue() {
                return (float) random;
            }

            @Override
            public double doubleValue() {
                return (double) random;
            }
        };
    }
}
