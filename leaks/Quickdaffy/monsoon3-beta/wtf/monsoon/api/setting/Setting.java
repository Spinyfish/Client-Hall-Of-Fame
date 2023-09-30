/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventUpdateEnumSetting;

public class Setting<T> {
    private final String name;
    private String description = "";
    private Supplier<Boolean> visibility = () -> true;
    private Setting<?> parent = null;
    private List<Setting<?>> children = new ArrayList();
    private T value;
    private T defaultValue;
    private T minimum;
    private T maximum;
    private T incrementation;
    private int index = 0;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;
        if (this.getValue() instanceof Enum) {
            this.index = ((Enum)value).ordinal();
        }
    }

    public void setValue(T value) {
        if (value instanceof Enum) {
            EventUpdateEnumSetting event = new EventUpdateEnumSetting(this, (Enum)this.value, (Enum)value);
            Wrapper.getEventBus().post(event);
            if (!event.isCancelled()) {
                this.value = value;
            }
        } else {
            this.value = value;
        }
    }

    public void setValueSilent(T value) {
        this.value = value;
    }

    public T getMode(boolean previous) {
        if (this.getValue() instanceof Enum) {
            Enum enumeration = (Enum)this.getValue();
            String[] values = (String[])Arrays.stream(enumeration.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);
            this.index = !previous ? (this.index + 1 > values.length - 1 ? 0 : this.index + 1) : (this.index - 1 < 0 ? values.length - 1 : this.index - 1);
            return (T)Enum.valueOf(enumeration.getClass(), values[this.index]);
        }
        return null;
    }

    public Setting<T> minimum(T minimum) {
        this.minimum = minimum;
        return this;
    }

    public Setting<T> maximum(T maximum) {
        this.maximum = maximum;
        return this;
    }

    public Setting<T> incrementation(T incrementation) {
        this.incrementation = incrementation;
        return this;
    }

    public Setting<T> describedBy(String description) {
        this.description = description;
        return this;
    }

    public Setting<T> visibleWhen(Supplier<Boolean> visibility) {
        this.visibility = visibility;
        return this;
    }

    public Setting<T> childOf(Setting<?> parent) {
        this.parent = parent;
        this.parent.children.add(this);
        return this;
    }

    public String getPath() {
        return this.getParent() == null ? this.getName() : this.getParent().getPath() + this.getName();
    }

    public List<Setting<?>> getHierarchy() {
        ArrayList hierarchy = new ArrayList();
        for (Setting<?> subsetting : this.getChildren()) {
            hierarchy.add(subsetting);
            hierarchy.addAll(subsetting.getHierarchy());
        }
        return hierarchy;
    }

    public boolean isVisible() {
        return this.visibility.get();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Setting<?> getParent() {
        return this.parent;
    }

    public List<Setting<?>> getChildren() {
        return this.children;
    }

    public T getValue() {
        return this.value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getMinimum() {
        return this.minimum;
    }

    public T getMaximum() {
        return this.maximum;
    }

    public T getIncrementation() {
        return this.incrementation;
    }

    public int getIndex() {
        return this.index;
    }
}

