package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentUntouching extends Enchantment {
   protected EnchantmentUntouching(ResourceLocation p_i45763_2_) {
      super(33, p_i45763_2_, 1, EnumEnchantmentType.DIGGER);
      this.setName("untouching");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 15;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }

   @Override
   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) || ench.effectId == fortune.effectId;
   }

   @Override
   public boolean canApply(ItemStack stack) {
      return stack.getItem() == Items.shears || super.canApply(stack);
   }
}
