package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFireAspect extends Enchantment {
   protected EnchantmentFireAspect(ResourceLocation enchName) {
      super(20, enchName, 2, EnumEnchantmentType.WEAPON);
      this.setName("fire");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
