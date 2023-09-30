package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDigging extends Enchantment {
    protected EnchantmentDigging(final int enchID, final ResourceLocation enchName, final int enchWeight) {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.DIGGER);
        this.setName("digging");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int enchantmentLevel) {
        return 1 + 10 * (enchantmentLevel - 1);
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
        return 5;
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     *
     * @param stack The ItemStack that is attempting to become enchanted with with enchantment.
     */
    public boolean canApply(final ItemStack stack) {
        return stack.getItem() == Items.shears || super.canApply(stack);
    }
}
