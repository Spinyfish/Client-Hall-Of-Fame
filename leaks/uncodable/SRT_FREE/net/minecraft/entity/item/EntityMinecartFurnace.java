package net.minecraft.entity.item;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {
   private int fuel;
   public double pushX;
   public double pushZ;

   public EntityMinecartFurnace(World worldIn) {
      super(worldIn);
   }

   public EntityMinecartFurnace(World worldIn, double p_i1719_2_, double p_i1719_4_, double p_i1719_6_) {
      super(worldIn, p_i1719_2_, p_i1719_4_, p_i1719_6_);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.FURNACE;
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.fuel > 0) {
         --this.fuel;
      }

      if (this.fuel <= 0) {
         this.pushX = this.pushZ = 0.0;
      }

      this.setMinecartPowered(this.fuel > 0);
      if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 0.8, this.posZ, 0.0, 0.0, 0.0);
      }
   }

   @Override
   protected double getMaximumSpeed() {
      return 0.2;
   }

   @Override
   public void killMinecart(DamageSource p_94095_1_) {
      super.killMinecart(p_94095_1_);
      if (!p_94095_1_.isExplosion() && this.worldObj.getGameRules().getGameRuleBooleanValue("doEntityDrops")) {
         this.entityDropItem(new ItemStack(Blocks.furnace, 1), 0.0F);
      }
   }

   @Override
   protected void func_180460_a(BlockPos p_180460_1_, IBlockState p_180460_2_) {
      super.func_180460_a(p_180460_1_, p_180460_2_);
      double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d0 > 1.0E-4 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001) {
         d0 = (double)MathHelper.sqrt_double(d0);
         this.pushX /= d0;
         this.pushZ /= d0;
         if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0) {
            this.pushX = 0.0;
            this.pushZ = 0.0;
         } else {
            double d1 = d0 / this.getMaximumSpeed();
            this.pushX *= d1;
            this.pushZ *= d1;
         }
      }
   }

   @Override
   protected void applyDrag() {
      double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d0 > 1.0E-4) {
         d0 = (double)MathHelper.sqrt_double(d0);
         this.pushX /= d0;
         this.pushZ /= d0;
         double d1 = 1.0;
         this.motionX *= 0.8F;
         this.motionY *= 0.0;
         this.motionZ *= 0.8F;
         this.motionX += this.pushX * d1;
         this.motionZ += this.pushZ * d1;
      } else {
         this.motionX *= 0.98F;
         this.motionY *= 0.0;
         this.motionZ *= 0.98F;
      }

      super.applyDrag();
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      ItemStack itemstack = playerIn.inventory.getCurrentItem();
      if (itemstack != null && itemstack.getItem() == Items.coal) {
         if (!playerIn.capabilities.isCreativeMode && --itemstack.stackSize == 0) {
            playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
         }

         this.fuel += 3600;
      }

      this.pushX = this.posX - playerIn.posX;
      this.pushZ = this.posZ - playerIn.posZ;
      return false;
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setDouble("PushX", this.pushX);
      tagCompound.setDouble("PushZ", this.pushZ);
      tagCompound.setShort("Fuel", (short)this.fuel);
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.pushX = tagCompund.getDouble("PushX");
      this.pushZ = tagCompund.getDouble("PushZ");
      this.fuel = tagCompund.getShort("Fuel");
   }

   protected boolean isMinecartPowered() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   protected void setMinecartPowered(boolean p_94107_1_) {
      if (p_94107_1_) {
         this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) & -2));
      }
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return (this.isMinecartPowered() ? Blocks.lit_furnace : Blocks.furnace).getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
   }
}
