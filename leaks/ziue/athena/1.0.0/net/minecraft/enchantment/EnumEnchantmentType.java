package net.minecraft.enchantment;

import net.minecraft.item.*;

public enum EnumEnchantmentType
{
    ALL, 
    ARMOR, 
    ARMOR_FEET, 
    ARMOR_LEGS, 
    ARMOR_TORSO, 
    ARMOR_HEAD, 
    WEAPON, 
    DIGGER, 
    FISHING_ROD, 
    BREAKABLE, 
    BOW;
    
    public boolean canEnchantItem(final Item p_77557_1_) {
        if (this == EnumEnchantmentType.ALL) {
            return true;
        }
        if (this == EnumEnchantmentType.BREAKABLE && p_77557_1_.isDamageable()) {
            return true;
        }
        if (!(p_77557_1_ instanceof ItemArmor)) {
            return (p_77557_1_ instanceof ItemSword) ? (this == EnumEnchantmentType.WEAPON) : ((p_77557_1_ instanceof ItemTool) ? (this == EnumEnchantmentType.DIGGER) : ((p_77557_1_ instanceof ItemBow) ? (this == EnumEnchantmentType.BOW) : (p_77557_1_ instanceof ItemFishingRod && this == EnumEnchantmentType.FISHING_ROD)));
        }
        if (this == EnumEnchantmentType.ARMOR) {
            return true;
        }
        final ItemArmor itemarmor = (ItemArmor)p_77557_1_;
        return (itemarmor.armorType == 0) ? (this == EnumEnchantmentType.ARMOR_HEAD) : ((itemarmor.armorType == 2) ? (this == EnumEnchantmentType.ARMOR_LEGS) : ((itemarmor.armorType == 1) ? (this == EnumEnchantmentType.ARMOR_TORSO) : (itemarmor.armorType == 3 && this == EnumEnchantmentType.ARMOR_FEET)));
    }
}
