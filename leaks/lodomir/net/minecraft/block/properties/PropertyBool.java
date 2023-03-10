/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import net.minecraft.block.properties.PropertyHelper;

public class PropertyBool
extends PropertyHelper<Boolean> {
    private final ImmutableSet<Boolean> allowedValues = ImmutableSet.of((Object)true, (Object)false);

    protected PropertyBool(String name) {
        super(name, Boolean.class);
    }

    @Override
    public Collection<Boolean> getAllowedValues() {
        return this.allowedValues;
    }

    public static PropertyBool create(String name) {
        return new PropertyBool(name);
    }

    @Override
    public String getName(Boolean value) {
        return value.toString();
    }
}

