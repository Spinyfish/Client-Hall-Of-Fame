package net.minecraft.enchantment;

import net.minecraft.util.*;

public class EnchantmentData extends WeightedRandom.Item
{
    public final Enchantment enchantmentobj;
    public final int enchantmentLevel;
    
    public EnchantmentData(final Enchantment enchantmentObj, final int enchLevel) {
        super(enchantmentObj.getWeight());
        this.enchantmentobj = enchantmentObj;
        this.enchantmentLevel = enchLevel;
    }
}
