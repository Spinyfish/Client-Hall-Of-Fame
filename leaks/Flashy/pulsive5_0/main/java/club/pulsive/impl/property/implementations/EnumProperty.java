package club.pulsive.impl.property.implementations;

import club.pulsive.impl.property.Property;

import java.util.Arrays;
import java.util.function.Supplier;

public final class EnumProperty<T extends Enum<T>> extends Property<T> {

    private final T[] values;

    public EnumProperty(final String name, final T value) {
        this(name, value, () -> true);
    }

    public EnumProperty(final String name, final T value,
                        final Supplier<Boolean> dependency) {
        super(name, value, dependency);

        this.values = this.getType().getEnumConstants();
    }

    public void setValue(int index) {
        this.setValue(this.values[Math.max(0, Math.min(this.values.length - 1, index))]);
    }

    public Class<T> getType() {
        return (Class<T>) this.getValue().getClass();
    }

    public String[] getValueNames() {
        return Arrays.stream(this.values)
                .map(Enum::toString)
                .toArray(String[]::new);
    }

    public T[] values() {
        return values;
    }
}
