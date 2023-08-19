package cc.swift.value.impl;

import cc.swift.value.Value;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.MathHelper;

import java.util.function.Supplier;

@Getter @Setter
public final class DoubleValue extends Value<Double> {
    private final double minimum, maximum, increment;

    public DoubleValue(String name, Double defaultValue, double minimum, double maximum, double increment) {
        super(name, defaultValue);
        this.increment = increment;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public void setValue(Double value) {
        value = MathHelper.clamp_double(value, minimum, maximum);
        super.setValue(value);
    }

    public DoubleValue setDependency(Supplier<Boolean> dependency) {
        this.dependency = dependency;
        return this;
    }

    public void increment() {
        setValue(getValue() + increment);
    }
    public void decrement() {
        setValue(getValue() - increment);
    }
}
