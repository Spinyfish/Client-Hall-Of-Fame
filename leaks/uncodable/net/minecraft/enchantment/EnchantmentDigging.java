package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDigging extends Enchantment {
   protected EnchantmentDigging(ResourceLocation enchName) {
      super(32, enchName, 10, EnumEnchantmentType.DIGGER);
      this.setName("digging");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + 10 * (enchantmentLevel - 1);
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }

   @Override
   public boolean canApply(ItemStack stack) {
      return stack.getItem() == Items.shears || super.canApply(stack);
   }
}
