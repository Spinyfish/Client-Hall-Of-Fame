package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowFire extends Enchantment {
    public EnchantmentArrowFire(final int enchID, final ResourceLocation enchName, final int enchWeight) {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.BOW);
        this.setName("arrowFire");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int enchantmentLevel) {
        return 20;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int enchantmentLevel) {
        return 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 1;
    }
}
