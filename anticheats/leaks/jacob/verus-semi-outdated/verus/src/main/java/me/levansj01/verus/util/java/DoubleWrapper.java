package me.levansj01.verus.util.java;

public class DoubleWrapper {
    private double value;

    public double addAndGet(double var1) {
        this.value += var1;
        return this.value;
    }

    public double get() {
        return this.value;
    }

    public DoubleWrapper(double value) {
        this.value = (double)Double.doubleToRawLongBits(value);
    }

    public void set(double value) {
        this.value = value;
    }
}
