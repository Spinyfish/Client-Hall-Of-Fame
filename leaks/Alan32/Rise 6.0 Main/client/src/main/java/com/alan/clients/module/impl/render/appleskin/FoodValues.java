package com.alan.clients.module.impl.render.appleskin;

/**
 * @author Auth, Squeek
 * @since 02/07/2022
 */
public class FoodValues {

    public final int hunger;
    public final float saturationModifier;

    public FoodValues(final int hunger, final float saturationModifier) {
        this.hunger = hunger;
        this.saturationModifier = saturationModifier;
    }

    public float getSaturationIncrement() {
        return hunger * saturationModifier * 2f;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FoodValues)) {
            return false;
        }

        final FoodValues that = (FoodValues) o;
        return hunger == that.hunger && Float.compare(that.saturationModifier, saturationModifier) == 0;
    }

    @Override
    public int hashCode() {
        int result = hunger;
        result = 31 * result + (saturationModifier != +0.0f ? Float.floatToIntBits(saturationModifier) : 0);
        return result;
    }
}