package net.minecraft.enchantment;

import net.minecraft.util.*;

public class EnchantmentArrowDamage extends Enchantment
{
    public EnchantmentArrowDamage(final int enchID, final ResourceLocation enchName, final int enchWeight) {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.BOW);
        this.setName("arrowDamage");
    }
    
    @Override
    public int getMinEnchantability(final int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 10;
    }
    
    @Override
    public int getMaxEnchantability(final int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }
    
    @Override
    public int getMaxLevel() {
        return 5;
    }
}
