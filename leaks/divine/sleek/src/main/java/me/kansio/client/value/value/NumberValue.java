package me.kansio.client.value.value;

import lombok.Getter;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.Value;

@Getter
public final class NumberValue<T extends Number> extends Value<T> {

    private final T min, max, increment;

    public NumberValue(String name, Module owner, T value, T min, T max, T increment) {
        super(name, owner, value);
        //checkRetardMoment(value);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public NumberValue(String name, Module owner, T value, T min, T max, T increment, BooleanValue parent) {
        super(name, owner, value, parent);
        //checkRetardMoment(value);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public NumberValue(String name, Module owner, T value, T min, T max, T increment, ModeValue parent, String... parentmode) {
        super(name, owner, value, parent, parentmode);
        //checkRetardMoment(value);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    private void checkRetardMoment(T value) {
        if (value.doubleValue() < min.doubleValue()) {
            try {
                throw new Exception("Retard Exception: Default Value < Min Value");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (min.doubleValue() < 0 || max.doubleValue() < 0) {
            try {
                throw new Exception("Retard Exception: Min or Max Value below zero!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public T getMax() {
        return max;
    }

    public T getMin() {
        return min;
    }

    @SuppressWarnings("all")
    public T getCastedValue() {
        return value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValueAutoSave(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) || getValue().equals(o);
    }

    public boolean isInteger() {
        return (min instanceof Integer);
    }

}