package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentLootBonus extends Enchantment {
    protected EnchantmentLootBonus(final int p_i45767_1_, final ResourceLocation p_i45767_2_, final int p_i45767_3_, final EnumEnchantmentType p_i45767_4_) {
        super(p_i45767_1_, p_i45767_2_, p_i45767_3_, p_i45767_4_);

        if (p_i45767_4_ == EnumEnchantmentType.DIGGER) {
            this.setName("lootBonusDigger");
        } else if (p_i45767_4_ == EnumEnchantmentType.FISHING_ROD) {
            this.setName("lootBonusFishing");
        } else {
            this.setName("lootBonus");
        }
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 9;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 3;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     *
     * @param ench A possible enchantment that may be applied along side this enchantment, depending on the results.
     */
    public boolean canApplyTogether(final Enchantment ench) {
        return super.canApplyTogether(ench) && ench.effectId != silkTouch.effectId;
    }
}
