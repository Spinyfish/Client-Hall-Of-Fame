package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemCloth extends ItemBlock {
   public ItemCloth(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes();
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + EnumDyeColor.byMetadata(stack.getMetadata()).getUnlocalizedName();
   }
}
