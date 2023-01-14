package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb extends Entity {
   public int xpColor;
   public int xpOrbAge;
   public int delayBeforeCanPickup;
   private int xpOrbHealth = 5;
   private int xpValue;
   private EntityPlayer closestPlayer;
   private int xpTargetColor;

   public EntityXPOrb(World worldIn, double x, double y, double z, int expValue) {
      super(worldIn);
      this.setSize(0.5F, 0.5F);
      this.setPosition(x, y, z);
      this.rotationYaw = (float)(Math.random() * 360.0);
      this.motionX = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
      this.motionY = (double)((float)(Math.random() * 0.2) * 2.0F);
      this.motionZ = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
      this.xpValue = expValue;
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   public EntityXPOrb(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
   }

   @Override
   protected void entityInit() {
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      float f = 0.5F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      int i = super.getBrightnessForRender(partialTicks);
      int j = i & 0xFF;
      int k = i >> 16 & 0xFF;
      j += (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.delayBeforeCanPickup > 0) {
         --this.delayBeforeCanPickup;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.03F;
      if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.lava) {
         this.motionY = 0.2F;
         this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
         this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
         this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
      }

      this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0, this.posZ);
      double d0 = 8.0;
      if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
         if (this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > d0 * d0) {
            this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, d0);
         }

         this.xpTargetColor = this.xpColor;
      }

      if (this.closestPlayer != null && this.closestPlayer.isSpectator()) {
         this.closestPlayer = null;
      }

      if (this.closestPlayer != null) {
         double d1 = (this.closestPlayer.posX - this.posX) / d0;
         double d2 = (this.closestPlayer.posY + (double)this.closestPlayer.getEyeHeight() - this.posY) / d0;
         double d3 = (this.closestPlayer.posZ - this.posZ) / d0;
         double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
         double d5 = 1.0 - d4;
         if (d5 > 0.0) {
            d5 *= d5;
            this.motionX += d1 / d4 * d5 * 0.1;
            this.motionY += d2 / d4 * d5 * 0.1;
            this.motionZ += d3 / d4 * d5 * 0.1;
         }
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      float f = 0.98F;
      if (this.onGround) {
         f = this.worldObj
               .getBlockState(
                  new BlockPos(
                     MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ)
                  )
               )
               .getBlock()
               .slipperiness
            * 0.98F;
      }

      this.motionX *= (double)f;
      this.motionY *= 0.98F;
      this.motionZ *= (double)f;
      if (this.onGround) {
         this.motionY *= -0.9F;
      }

      ++this.xpColor;
      ++this.xpOrbAge;
      if (this.xpOrbAge >= 6000) {
         this.setDead();
      }
   }

   @Override
   public void handleWaterMovement() {
      this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.water, this);
   }

   @Override
   protected void dealFireDamage() {
      this.attackEntityFrom(DamageSource.inFire, 1.0F);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else {
         this.setBeenAttacked();
         this.xpOrbHealth = (int)((float)this.xpOrbHealth - amount);
         if (this.xpOrbHealth <= 0) {
            this.setDead();
         }

         return false;
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("Health", (short)((byte)this.xpOrbHealth));
      tagCompound.setShort("Age", (short)this.xpOrbAge);
      tagCompound.setShort("Value", (short)this.xpValue);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.xpOrbHealth = tagCompund.getShort("Health") & 255;
      this.xpOrbAge = tagCompund.getShort("Age");
      this.xpValue = tagCompund.getShort("Value");
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.worldObj.isRemote && this.delayBeforeCanPickup == 0 && entityIn.xpCooldown == 0) {
         entityIn.xpCooldown = 2;
         this.worldObj.playSoundAtEntity(entityIn, "random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
         entityIn.onItemPickup(this, 1);
         entityIn.addExperience(this.xpValue);
         this.setDead();
      }
   }

   public int getXpValue() {
      return this.xpValue;
   }

   public int getTextureByXP() {
      return this.xpValue >= 2477
         ? 10
         : (
            this.xpValue >= 1237
               ? 9
               : (
                  this.xpValue >= 617
                     ? 8
                     : (
                        this.xpValue >= 307
                           ? 7
                           : (
                              this.xpValue >= 149
                                 ? 6
                                 : (
                                    this.xpValue >= 73
                                       ? 5
                                       : (this.xpValue >= 37 ? 4 : (this.xpValue >= 17 ? 3 : (this.xpValue >= 7 ? 2 : (this.xpValue >= 3 ? 1 : 0))))
                                 )
                           )
                     )
               )
         );
   }

   public static int getXPSplit(int expValue) {
      return expValue >= 2477
         ? 2477
         : (
            expValue >= 1237
               ? 1237
               : (
                  expValue >= 617
                     ? 617
                     : (
                        expValue >= 307
                           ? 307
                           : (
                              expValue >= 149
                                 ? 149
                                 : (expValue >= 73 ? 73 : (expValue >= 37 ? 37 : (expValue >= 17 ? 17 : (expValue >= 7 ? 7 : (expValue >= 3 ? 3 : 1)))))
                           )
                     )
               )
         );
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }
}
