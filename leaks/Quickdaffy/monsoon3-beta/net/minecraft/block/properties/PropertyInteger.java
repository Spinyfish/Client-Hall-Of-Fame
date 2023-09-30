/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 */
package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.block.properties.PropertyHelper;

public class PropertyInteger
extends PropertyHelper<Integer> {
    private final ImmutableSet<Integer> allowedValues;

    protected PropertyInteger(String name, int min, int max) {
        super(name, Integer.class);
        if (min < 0) {
            throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
        }
        if (max <= min) {
            throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
        }
        HashSet set = Sets.newHashSet();
        for (int i = min; i <= max; ++i) {
            set.add(i);
        }
        this.allowedValues = ImmutableSet.copyOf((Collection)set);
    }

    @Override
    public Collection<Integer> getAllowedValues() {
        return this.allowedValues;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            if (!super.equals(p_equals_1_)) {
                return false;
            }
            PropertyInteger propertyinteger = (PropertyInteger)p_equals_1_;
            return this.allowedValues.equals(propertyinteger.allowedValues);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int i = super.hashCode();
        i = 31 * i + this.allowedValues.hashCode();
        return i;
    }

    public static PropertyInteger create(String name, int min, int max) {
        return new PropertyInteger(name, min, max);
    }

    @Override
    public String getName(Integer value) {
        return value.toString();
    }
}

