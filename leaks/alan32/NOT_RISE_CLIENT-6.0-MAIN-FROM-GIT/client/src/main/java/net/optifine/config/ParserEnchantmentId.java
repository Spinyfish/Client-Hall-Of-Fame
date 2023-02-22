package net.optifine.config;

import net.minecraft.enchantment.Enchantment;

public class ParserEnchantmentId implements IParserInt {
    public int parse(final String str, final int defVal) {
        final Enchantment enchantment = Enchantment.getEnchantmentByLocation(str);
        return enchantment == null ? defVal : enchantment.effectId;
    }
}
