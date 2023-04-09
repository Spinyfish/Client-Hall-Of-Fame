package tech.dort.dortware.api.property.impl;

import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.Value;

public final class NumberValue extends Value<Double> {

    private final double min, max;
    private final SliderUnit unit;
    private boolean isInt;

    public NumberValue(String name, Object owner, double value, double min, double max, SliderUnit unit) {
        super(name, owner, value);
        checkRetardMoment(value);
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    public NumberValue(String name, Object owner, double value, double min, double max, SliderUnit unit, boolean isInt) {
        this(name, owner, value, min, max, unit);
        this.isInt = isInt;
    }

    public NumberValue(String name, Object owner, double value, double min, double max, SliderUnit unit, boolean isInt, Value parent) {
        this(name, owner, value, min, max, unit, isInt);
        this.parent = parent;
    }

    public NumberValue(String name, Object owner, double value, double min, double max, SliderUnit unit, Value parent) {
        this(name, owner, value, min, max, unit, false, parent);
    }

    public NumberValue(String name, Object owner, double value, double min, double max, boolean isInt) {
        this(name, owner, value, min, max, null, isInt);
    }

    public NumberValue(String name, Object owner, double value, double min, double max, boolean isInt, Value parent) {
        this(name, owner, value, min, max, null, isInt, parent);
    }

    public NumberValue(String name, Object owner, double value, double min, double max) {
        super(name, owner, value);
        checkRetardMoment(value);
        this.min = min;
        this.max = max;
        this.unit = null;
    }

    public SliderUnit getUnit() {
        return unit;
    }

    private void checkRetardMoment(double value) {
        if (value < min) {
            try {
                throw new Exception("Retard Exception: Default Value < Min Value");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (min < 0 || max < 0) {
            try {
                throw new Exception("Retard Exception: Min or Max Value below zero!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasUnit() {
        return unit != null;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    @SuppressWarnings("all")
    public <T extends Number> T getCastedValue() {
        return (T) super.getValue();
    }

    @Override
    public Double getValue() {
        if (isInt) {
            return (double) super.getValue().intValue();
        }
        return super.getValue();
    }

    @Override
    public void setValueAutoSave(Double value) {
        super.setValueAutoSave(Math.min(Math.max(min, value), max));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) || getValue().equals(o);
    }

    public boolean isInteger() {
        return isInt;
    }

}
