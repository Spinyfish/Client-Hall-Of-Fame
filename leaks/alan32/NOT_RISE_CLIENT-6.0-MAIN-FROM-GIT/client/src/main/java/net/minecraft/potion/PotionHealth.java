package net.minecraft.potion;

import net.minecraft.util.ResourceLocation;

public class PotionHealth extends Potion {
    public PotionHealth(final int potionID, final ResourceLocation location, final boolean badEffect, final int potionColor) {
        super(potionID, location, badEffect, potionColor);
    }

    /**
     * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
     */
    public boolean isInstant() {
        return true;
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isReady(final int p_76397_1_, final int p_76397_2_) {
        return p_76397_1_ >= 1;
    }
}
