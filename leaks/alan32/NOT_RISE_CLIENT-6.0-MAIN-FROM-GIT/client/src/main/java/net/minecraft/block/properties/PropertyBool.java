package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public class PropertyBool extends PropertyHelper<Boolean> {
    private final ImmutableSet<Boolean> allowedValues = ImmutableSet.of(true, false);

    protected PropertyBool(final String name) {
        super(name, Boolean.class);
    }

    public Collection<Boolean> getAllowedValues() {
        return this.allowedValues;
    }

    public static PropertyBool create(final String name) {
        return new PropertyBool(name);
    }

    /**
     * Get the name for the given value.
     */
    public String getName(final Boolean value) {
        return value.toString();
    }
}
