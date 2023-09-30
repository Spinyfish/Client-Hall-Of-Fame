package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

public class PropertyInteger extends PropertyHelper<Integer> {
    private final ImmutableSet<Integer> allowedValues;

    protected PropertyInteger(final String name, final int min, final int max) {
        super(name, Integer.class);

        if (min < 0) {
            throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
        } else if (max <= min) {
            throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
        } else {
            final Set<Integer> set = Sets.newHashSet();

            for (int i = min; i <= max; ++i) {
                set.add(i);
            }

            this.allowedValues = ImmutableSet.copyOf(set);
        }
    }

    public Collection<Integer> getAllowedValues() {
        return this.allowedValues;
    }

    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            if (!super.equals(p_equals_1_)) {
                return false;
            } else {
                final PropertyInteger propertyinteger = (PropertyInteger) p_equals_1_;
                return this.allowedValues.equals(propertyinteger.allowedValues);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = super.hashCode();
        i = 31 * i + this.allowedValues.hashCode();
        return i;
    }

    public static PropertyInteger create(final String name, final int min, final int max) {
        return new PropertyInteger(name, min, max);
    }

    /**
     * Get the name for the given value.
     */
    public String getName(final Integer value) {
        return value.toString();
    }
}
