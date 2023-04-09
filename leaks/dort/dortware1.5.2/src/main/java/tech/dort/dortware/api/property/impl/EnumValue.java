package tech.dort.dortware.api.property.impl;

import tech.dort.dortware.api.property.Value;
import tech.dort.dortware.api.property.impl.interfaces.INameable;

import java.util.Arrays;
import java.util.List;

public final class EnumValue<Type extends INameable> extends Value<Type> {

    private final List<Type> values;
    private final Class type;

    @SafeVarargs
    public EnumValue(String name, Object owner, Type... values) {
        super(name, owner, values[0]);
        this.values = Arrays.asList(values);
        this.type = values[0].getClass();
    }

    public EnumValue(String name, Object owner, Type[] values, Value<?> parent) {
        super(name, owner, values[0]);
        this.values = Arrays.asList(values);
        this.parent = parent;
        this.type = values[0].getClass();
    }

    public void setValueAutoSave(String value) {

        values.forEach(nameable -> {
            if (nameable.name().equals(value)) {
                super.setValueAutoSave(nameable);
            }
        });

    }

    public List<Type> getValues() {
        return values;
    }

    @SuppressWarnings("all")
    public <T extends Type> T getCastedValue() {
        return (T) super.getValue();
    }

    public void setValue(String value) {
        values.forEach(nameable -> {
            if (nameable.name().equals(value)) {
                super.setValue(nameable);
            }
        });
    }

    public Class<?> getType() {
        return type;
    }
}
