package club.pulsive.impl.property.implementations;

import club.pulsive.impl.property.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MultiSelectEnumProperty<T extends Enum<T>> extends Property<List<T>> {

    private final T[] values;

    @SuppressWarnings("unchecked")
    public MultiSelectEnumProperty(String name, List<T> selected, T[] values, Supplier<Boolean> dependency) {
        super(name, selected, dependency);

        this.values = ((Class<T>) values[0].getClass()).getEnumConstants();
    }

    public MultiSelectEnumProperty(String name, List<T> selected, T[] values) {
        this(name, selected, values, () -> true);
    }
    

    public void select(int idx) {
        this.getValue().add(this.values[this.clamp(idx)]);
    }

    public void unselect(int idx) {
        this.getValue().remove(this.values[this.clamp(idx)]);
    }

    public boolean isSelected(final T value) {
        return this.getValue().contains(value);
    }

    public boolean isSelected(int idx) {
        return this.getValue().contains(this.getValues()[idx]);
    }

    public boolean hasSelections() {
        return !this.getValue().isEmpty();
    }

    private int clamp(int idx) {
        return Math.max(0, Math.min(this.values.length - 1, idx));
    }

    public String[] getValueNames() {
        return Arrays.stream(this.values)
                .map(Enum::toString)
                .toArray(String[]::new);
    }

    public int[] getValueIndices() {
        return this.getValue().stream()
                .mapToInt(Enum::ordinal)
                .toArray();
    }

    public T[] getValues() {
        return values;
    }

    public Boolean isSelectedConstant(Enum<?> constant) {
        return getValue().contains(constant);
    }
}