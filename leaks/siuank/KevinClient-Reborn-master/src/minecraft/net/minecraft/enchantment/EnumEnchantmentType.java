package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

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

    /**
     * Return true if the item passed can be enchanted by a enchantment of this type.
     */
    public boolean canEnchantItem(Item p_77557_1_)
    {
        if (this == ALL)
        {
            return true;
        }
        else if (this == BREAKABLE && p_77557_1_.isDamageable())
        {
            return true;
        }
        else if (p_77557_1_ instanceof ItemArmor)
        {
            if (this == ARMOR)
            {
                return true;
            }
            else
            {
                ItemArmor itemarmor = (ItemArmor)p_77557_1_;

                if (itemarmor.armorType == 0)
                {
                    return this == ARMOR_HEAD;
                }
                else if (itemarmor.armorType == 2)
                {
                    return this == ARMOR_LEGS;
                }
                else if (itemarmor.armorType == 1)
                {
                    return this == ARMOR_TORSO;
                }
                else if (itemarmor.armorType == 3)
                {
                    return this == ARMOR_FEET;
                }
                else
                {
                    return false;
                }
            }
        }
        else if (p_77557_1_ instanceof ItemSword)
        {
            return this == WEAPON;
        }
        else if (p_77557_1_ instanceof ItemTool)
        {
            return this == DIGGER;
        }
        else if (p_77557_1_ instanceof ItemBow)
        {
            return this == BOW;
        }
        else if (p_77557_1_ instanceof ItemFishingRod)
        {
            return this == FISHING_ROD;
        }
        else
        {
            return false;
        }
    }
}
