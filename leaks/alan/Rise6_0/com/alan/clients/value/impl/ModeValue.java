package com.alan.clients.value.impl;

import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.module.Module;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @author Patrick
 * @since 10/19/2021
 */
@Getter
public class ModeValue extends ListValue<Mode<?>> {

    private final List<Mode<?>> modes = new ArrayList<>();

    public ModeValue(final String name, final Module parent) {
        super(name, parent);
    }

    public ModeValue(final String name, final Mode<?> parent) {
        super(name, parent);
    }

    public ModeValue(final String name, final Module parent, final BooleanSupplier hideIf) {
        super(name, parent, hideIf);
    }

    public ModeValue(final String name, final Mode<?> parent, final BooleanSupplier hideIf) {
        super(name, parent, hideIf);
    }

    public void update(final Mode<?> value) {
        if (this.getParent() != null && this.getParent().isEnabled()) {
            getValue().unregister();
            setValue(value);
            getValue().register();
        } else {
            setValue(value);
        }
    }

    public ModeValue add(final Mode<?>... modes) {
        if (modes == null) {
            return this;
        }

        this.modes.addAll(Arrays.asList(modes));
        return this;
    }

    public ModeValue setDefault(final String name) {
        setValue(modes.stream()
                .filter(mode -> mode.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(modes.get(0))
        );

        setDefaultValue(getValue());

        modes.forEach(mode -> mode.getValues().forEach(value -> value.setHideIf(() -> mode != this.getValue())));

        return this;
    }

    @Override
    public List<Value<?>> getSubValues() {
        ArrayList<Value<?>> allValues = new ArrayList<>();

        for (Mode<?> mode : getModes()) {
            allValues.addAll(mode.getValues());
        }

        return allValues;
    }
}