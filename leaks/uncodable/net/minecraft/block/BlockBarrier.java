package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockBarrier extends Block {
   private static boolean render;

   protected BlockBarrier() {
      super(Material.barrier);
      this.setBlockUnbreakable();
      this.setResistance(6000001.0F);
      this.disableStats();
      this.translucent = true;
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public int getRenderType() {
      return render ? 3 : -1;
   }

   @Override
   public boolean isOpaqueCube() {
      return render;
   }

   @Override
   public float getAmbientOcclusionLightValue() {
      return 1.0F;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   public static void setRender(boolean shouldRender) {
      render = shouldRender;
   }

   public static boolean getRender() {
      return render;
   }
}
