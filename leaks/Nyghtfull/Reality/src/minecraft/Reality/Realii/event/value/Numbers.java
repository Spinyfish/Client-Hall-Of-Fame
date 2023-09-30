/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.event.value;

public class Numbers<T extends Number>
        extends Value<T> {
    public T min;
    public T max;
    public T inc;
    private boolean integer;

    public Numbers(String displayName, String name, T value, T min, T max, T inc) {
        super(displayName, name);
        this.setValue(value);
        this.min = min;
        this.max = max;
        this.inc = inc;
        this.integer = false;
    }

    public Numbers(String name, T value, T min, T max, T inc) {
        super(name, name);
        this.setValue(value);
        this.min = min;
        this.max = max;
        this.inc = inc;
        this.integer = false;
    }

    public Numbers(String name, T value, T min, T max, T inc, boolean visible) {
        super(name, name);
        this.setValue(value);
        this.min = min;
        this.max = max;
        this.inc = inc;
        this.integer = false;
        this.visible = visible;
    }

    public T getMinimum() {
        return this.min;
    }

    public T getMaximum() {
        return this.max;
    }

    public void setIncrement(T inc) {
        this.inc = inc;
    }

    public T getIncrement() {
        return this.inc;
    }

}

