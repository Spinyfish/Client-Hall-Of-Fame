package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFishingSpeed extends Enchantment {
   protected EnchantmentFishingSpeed(ResourceLocation enchName) {
      super(62, enchName, 2, EnumEnchantmentType.FISHING_ROD);
      this.setName("fishingSpeed");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 15 + (enchantmentLevel - 1) * 9;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
