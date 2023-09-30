/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowKnockback
extends Enchantment {
    public EnchantmentArrowKnockback(int enchID, ResourceLocation enchName, int enchWeight) {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.BOW);
        this.setName("arrowKnockback");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 12 + (enchantmentLevel - 1) * 20;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

