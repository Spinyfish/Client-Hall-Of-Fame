package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   protected BlockBasePressurePlate(Material materialIn) {
      this(materialIn, materialIn.getMaterialMapColor());
   }

   protected BlockBasePressurePlate(Material p_i46401_1_, MapColor p_i46401_2_) {
      super(p_i46401_1_, p_i46401_2_);
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState0(worldIn.getBlockState(pos));
   }

   protected void setBlockBoundsBasedOnState0(IBlockState state) {
      boolean flag = this.getRedstoneStrength(state) > 0;
      float f = 0.0625F;
      if (flag) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
      } else {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
      }
   }

   @Override
   public int tickRate(World worldIn) {
      return 20;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public boolean canSpawnInBlock() {
      return true;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.canBePlacedOn(worldIn, pos.down());
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.canBePlacedOn(worldIn, pos.down())) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   private boolean canBePlacedOn(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos) || worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         int i = this.getRedstoneStrength(state);
         if (i > 0) {
            this.updateState(worldIn, pos, state, i);
         }
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote) {
         int i = this.getRedstoneStrength(state);
         if (i == 0) {
            this.updateState(worldIn, pos, state, i);
         }
      }
   }

   protected void updateState(World worldIn, BlockPos pos, IBlockState state, int oldRedstoneStrength) {
      int i = this.computeRedstoneStrength(worldIn, pos);
      boolean flag = oldRedstoneStrength > 0;
      boolean flag1 = i > 0;
      if (oldRedstoneStrength != i) {
         state = this.setRedstoneStrength(state, i);
         worldIn.setBlockState(pos, state, 2);
         this.updateNeighbors(worldIn, pos);
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
      }

      if (!flag1 && flag) {
         worldIn.playSoundEffect((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      } else if (flag1 && !flag) {
         worldIn.playSoundEffect((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (flag1) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }

   protected AxisAlignedBB getSensitiveAABB(BlockPos pos) {
      float f = 0.125F;
      return new AxisAlignedBB(
         (double)((float)pos.getX() + 0.125F),
         (double)pos.getY(),
         (double)((float)pos.getZ() + 0.125F),
         (double)((float)(pos.getX() + 1) - 0.125F),
         (double)pos.getY() + 0.25,
         (double)((float)(pos.getZ() + 1) - 0.125F)
      );
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (this.getRedstoneStrength(state) > 0) {
         this.updateNeighbors(worldIn, pos);
      }

      super.breakBlock(worldIn, pos, state);
   }

   protected void updateNeighbors(World worldIn, BlockPos pos) {
      worldIn.notifyNeighborsOfStateChange(pos, this);
      worldIn.notifyNeighborsOfStateChange(pos.down(), this);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return this.getRedstoneStrength(state);
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return side == EnumFacing.UP ? this.getRedstoneStrength(state) : 0;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float f = 0.5F;
      float f1 = 0.125F;
      float f2 = 0.5F;
      this.setBlockBounds(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   protected abstract int computeRedstoneStrength(World var1, BlockPos var2);

   protected abstract int getRedstoneStrength(IBlockState var1);

   protected abstract IBlockState setRedstoneStrength(IBlockState var1, int var2);
}
