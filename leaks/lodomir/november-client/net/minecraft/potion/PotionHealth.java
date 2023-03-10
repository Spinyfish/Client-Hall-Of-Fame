/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionHealth
extends Potion {
    public PotionHealth(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
        super(potionID, location, badEffect, potionColor);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean isReady(int p_76397_1_, int p_76397_2_) {
        return p_76397_1_ >= 1;
    }
}

