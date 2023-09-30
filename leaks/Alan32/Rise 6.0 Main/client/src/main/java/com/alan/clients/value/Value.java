package com.alan.clients.value;

import com.alan.clients.module.Module;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * @author Patrick
 * @since 10/19/2021
 */
@Getter
@Setter 
public abstract class Value<T> {

    private final String name;

    public BooleanSupplier hideIf;

    private T value;
    private boolean visible;
    private Module parent;

    private Consumer<T> valueChangeConsumer;
    private T defaultValue;

    public Value(final String name, final Module parent, final T defaultValue) {
        this.name = name;
        this.hideIf = null;
        this.parent = parent;
        this.defaultValue = defaultValue;
        this.setValue(defaultValue);
        parent.getValues().add(this);
    }

    public Value(final String name, final Mode<?> parent, final T defaultValue) {
        this.name = name;
        this.hideIf = null;
        this.defaultValue = defaultValue;
        this.setValue(defaultValue);
        parent.getValues().add(this);
    }

    public Value(final String name, final Module parent, final T defaultValue, final BooleanSupplier hideIf) {
        this.name = name;
        this.hideIf = hideIf;
        this.parent = parent;
        this.defaultValue = defaultValue;
        this.setValue(defaultValue);
        parent.getValues().add(this);
    }

    public Value(final String name, final Mode<?> parent, final T defaultValue, final BooleanSupplier hideIf) {
        this.name = name;
        this.hideIf = hideIf;
        this.defaultValue = defaultValue;
        this.setValue(defaultValue);
        parent.getValues().add(this);
    }

    public void setValueAsObject(final Object value) {
        if (this.valueChangeConsumer != null) this.valueChangeConsumer.accept((T) value);
        this.value = (T) value;
    }

    public void setValue(final T value) {
        if (this.valueChangeConsumer != null) this.valueChangeConsumer.accept(value);
        this.value = value;
    }
    public abstract List<Value<?>> getSubValues();
}