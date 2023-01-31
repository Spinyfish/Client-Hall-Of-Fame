package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowDamage extends Enchantment {
   public EnchantmentArrowDamage(int enchID, ResourceLocation enchName, int enchWeight) {
      super(enchID, enchName, enchWeight, EnumEnchantmentType.BOW);
      this.setName("arrowDamage");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + (enchantmentLevel - 1) * 10;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 15;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }
}
