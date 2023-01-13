package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;

public class ItemMultiTexture extends ItemBlock {
   protected final Block theBlock;
   protected final Function<ItemStack, String> nameFunction;

   public ItemMultiTexture(Block block, Block block2, Function<ItemStack, String> nameFunction) {
      super(block);
      this.theBlock = block2;
      this.nameFunction = nameFunction;
      this.setMaxDamage(0);
      this.setHasSubtypes();
   }

   public ItemMultiTexture(Block block, Block block2, String[] namesByMeta) {
      this(block, block2, p_apply_1_ -> {
         int i = p_apply_1_.getMetadata();
         if (i < 0 || i >= namesByMeta.length) {
            i = 0;
         }

         return namesByMeta[i];
      });
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + (String)this.nameFunction.apply(stack);
   }
}
