package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity {
   private boolean isBoatEmpty = true;
   private double speedMultiplier = 0.07;
   private int boatPosRotationIncrements;
   private double boatX;
   private double boatY;
   private double boatZ;
   private double boatYaw;
   private double boatPitch;
   private double velocityX;
   private double velocityY;
   private double velocityZ;

   public EntityBoat(World worldIn) {
      super(worldIn);
      this.preventEntitySpawning = true;
      this.setSize(1.5F, 0.6F);
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   protected void entityInit() {
      this.dataWatcher.addObject(17, 0);
      this.dataWatcher.addObject(18, 1);
      this.dataWatcher.addObject(19, 0.0F);
   }

   @Override
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return entityIn.getEntityBoundingBox();
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.getEntityBoundingBox();
   }

   @Override
   public boolean canBePushed() {
      return true;
   }

   public EntityBoat(World worldIn, double p_i1705_2_, double p_i1705_4_, double p_i1705_6_) {
      this(worldIn);
      this.setPosition(p_i1705_2_, p_i1705_4_, p_i1705_6_);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = p_i1705_2_;
      this.prevPosY = p_i1705_4_;
      this.prevPosZ = p_i1705_6_;
   }

   @Override
   public double getMountedYOffset() {
      return -0.3;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else if (!this.worldObj.isRemote && !this.isDead) {
         if (this.riddenByEntity != null && this.riddenByEntity == source.getEntity() && source instanceof EntityDamageSourceIndirect) {
            return false;
         } else {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
            this.setBeenAttacked();
            boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
            if (flag || this.getDamageTaken() > 40.0F) {
               if (this.riddenByEntity != null) {
                  this.riddenByEntity.mountEntity(this);
               }

               if (!flag && this.worldObj.getGameRules().getGameRuleBooleanValue("doEntityDrops")) {
                  this.dropItemWithOffset(Items.boat, 1, 0.0F);
               }

               this.setDead();
            }

            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public void performHurtAnimation() {
      this.setForwardDirection(-this.getForwardDirection());
      this.setTimeSinceHit(10);
      this.setDamageTaken(this.getDamageTaken() * 11.0F);
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
      if (p_180426_10_ && this.riddenByEntity != null) {
         this.prevPosX = this.posX = x;
         this.prevPosY = this.posY = y;
         this.prevPosZ = this.posZ = z;
         this.rotationYaw = yaw;
         this.rotationPitch = pitch;
         this.boatPosRotationIncrements = 0;
         this.setPosition(x, y, z);
         this.motionX = this.velocityX = 0.0;
         this.motionY = this.velocityY = 0.0;
         this.motionZ = this.velocityZ = 0.0;
      } else {
         if (this.isBoatEmpty) {
            this.boatPosRotationIncrements = posRotationIncrements + 5;
         } else {
            double d0 = x - this.posX;
            double d1 = y - this.posY;
            double d2 = z - this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 <= 1.0) {
               return;
            }

            this.boatPosRotationIncrements = 3;
         }

         this.boatX = x;
         this.boatY = y;
         this.boatZ = z;
         this.boatYaw = (double)yaw;
         this.boatPitch = (double)pitch;
         this.motionX = this.velocityX;
         this.motionY = this.velocityY;
         this.motionZ = this.velocityZ;
      }
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.velocityX = this.motionX = x;
      this.velocityY = this.motionY = y;
      this.velocityZ = this.motionZ = z;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.getDamageTaken() > 0.0F) {
         this.setDamageTaken(this.getDamageTaken() - 1.0F);
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      int i = 5;
      double d0 = 0.0;

      for(int j = 0; j < i; ++j) {
         double d1 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double)j / (double)i - 0.125;
         double d3 = this.getEntityBoundingBox().minY
            + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double)(j + 1) / (double)i
            - 0.125;
         AxisAlignedBB axisalignedbb = new AxisAlignedBB(
            this.getEntityBoundingBox().minX, d1, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().maxX, d3, this.getEntityBoundingBox().maxZ
         );
         if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
            d0 += 1.0 / (double)i;
         }
      }

      double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (d9 > 0.2975) {
         double d2 = Math.cos((double)this.rotationYaw * Math.PI / 180.0);
         double d4 = Math.sin((double)this.rotationYaw * Math.PI / 180.0);

         for(int k = 0; (double)k < 1.0 + d9 * 60.0; ++k) {
            double d5 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double d6 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7;
            if (this.rand.nextBoolean()) {
               double d7 = this.posX - d2 * d5 * 0.8 + d4 * d6;
               double d8 = this.posZ - d4 * d5 * 0.8 - d2 * d6;
               this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d7, this.posY - 0.125, d8, this.motionX, this.motionY, this.motionZ);
            } else {
               double d24 = this.posX + d2 + d4 * d5 * 0.7;
               double d25 = this.posZ + d4 - d2 * d5 * 0.7;
               this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d24, this.posY - 0.125, d25, this.motionX, this.motionY, this.motionZ);
            }
         }
      }

      if (this.worldObj.isRemote && this.isBoatEmpty) {
         if (this.boatPosRotationIncrements > 0) {
            double d12 = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
            double d16 = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
            double d19 = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;
            double d22 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d22 / (double)this.boatPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.boatPosRotationIncrements);
            --this.boatPosRotationIncrements;
            this.setPosition(d12, d16, d19);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            double d13 = this.posX + this.motionX;
            double d17 = this.posY + this.motionY;
            double d20 = this.posZ + this.motionZ;
            this.setPosition(d13, d17, d20);
            if (this.onGround) {
               this.motionX *= 0.5;
               this.motionY *= 0.5;
               this.motionZ *= 0.5;
            }

            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
         }
      } else {
         if (d0 < 1.0) {
            double d10 = d0 * 2.0 - 1.0;
            this.motionY += 0.04F * d10;
         } else {
            if (this.motionY < 0.0) {
               this.motionY /= 2.0;
            }

            this.motionY += 0.007F;
         }

         if (this.riddenByEntity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)this.riddenByEntity;
            float f = this.riddenByEntity.rotationYaw + -entitylivingbase.moveStrafing * 90.0F;
            this.motionX += -Math.sin((double)(f * (float) Math.PI / 180.0F)) * this.speedMultiplier * (double)entitylivingbase.moveForward * 0.05F;
            this.motionZ += Math.cos((double)(f * (float) Math.PI / 180.0F)) * this.speedMultiplier * (double)entitylivingbase.moveForward * 0.05F;
         }

         double d11 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d11 > 0.35) {
            double d14 = 0.35 / d11;
            this.motionX *= d14;
            this.motionZ *= d14;
            d11 = 0.35;
         }

         if (d11 > d9 && this.speedMultiplier < 0.35) {
            this.speedMultiplier += (0.35 - this.speedMultiplier) / 35.0;
            if (this.speedMultiplier > 0.35) {
               this.speedMultiplier = 0.35;
            }
         } else {
            this.speedMultiplier -= (this.speedMultiplier - 0.07) / 35.0;
            if (this.speedMultiplier < 0.07) {
               this.speedMultiplier = 0.07;
            }
         }

         for(int i1 = 0; i1 < 4; ++i1) {
            int l1 = MathHelper.floor_double(this.posX + ((double)(i1 % 2) - 0.5) * 0.8);
            int i2 = MathHelper.floor_double(this.posZ + ((double)(i1 / 2) - 0.5) * 0.8);

            for(int j2 = 0; j2 < 2; ++j2) {
               int l = MathHelper.floor_double(this.posY) + j2;
               BlockPos blockpos = new BlockPos(l1, l, i2);
               Block block = this.worldObj.getBlockState(blockpos).getBlock();
               if (block == Blocks.snow_layer) {
                  this.worldObj.setBlockToAir(blockpos);
                  this.isCollidedHorizontally = false;
               } else if (block == Blocks.waterlily) {
                  this.worldObj.destroyBlock(blockpos, true);
                  this.isCollidedHorizontally = false;
               }
            }
         }

         if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
         }

         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         if (this.isCollidedHorizontally && d9 > 0.2975) {
            if (!this.worldObj.isRemote && !this.isDead) {
               this.setDead();
               if (this.worldObj.getGameRules().getGameRuleBooleanValue("doEntityDrops")) {
                  for(int j1 = 0; j1 < 3; ++j1) {
                     this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                  }

                  for(int k1 = 0; k1 < 2; ++k1) {
                     this.dropItemWithOffset(Items.stick, 1, 0.0F);
                  }
               }
            }
         } else {
            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
         }

         this.rotationPitch = 0.0F;
         double d15 = (double)this.rotationYaw;
         double d18 = this.prevPosX - this.posX;
         double d21 = this.prevPosZ - this.posZ;
         if (d18 * d18 + d21 * d21 > 0.001) {
            d15 = (double)((float)(MathHelper.func_181159_b(d21, d18) * 180.0 / Math.PI));
         }

         double d23 = MathHelper.wrapAngleTo180_double(d15 - (double)this.rotationYaw);
         if (d23 > 20.0) {
            d23 = 20.0;
         }

         if (d23 < -20.0) {
            d23 = -20.0;
         }

         this.rotationYaw = (float)((double)this.rotationYaw + d23);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (!this.worldObj.isRemote) {
            List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F));
            if (list != null && !list.isEmpty()) {
               for(Entity entity : list) {
                  if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityBoat) {
                     entity.applyEntityCollision(this);
                  }
               }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
               this.riddenByEntity = null;
            }
         }
      }
   }

   @Override
   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         double d0 = Math.cos((double)this.rotationYaw * Math.PI / 180.0) * 0.4;
         double d1 = Math.sin((double)this.rotationYaw * Math.PI / 180.0) * 0.4;
         this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
      }
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tagCompound) {
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound tagCompund) {
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if ((this.riddenByEntity == null || !(this.riddenByEntity instanceof EntityPlayer) || this.riddenByEntity == playerIn) && !this.worldObj.isRemote) {
         playerIn.mountEntity(this);
      }

      return false;
   }

   @Override
   protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (onGroundIn) {
         if (this.fallDistance > 3.0F) {
            this.fall(this.fallDistance, 1.0F);
            if (!this.worldObj.isRemote && !this.isDead) {
               this.setDead();
               if (this.worldObj.getGameRules().getGameRuleBooleanValue("doEntityDrops")) {
                  for(int i = 0; i < 3; ++i) {
                     this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                  }

                  for(int j = 0; j < 2; ++j) {
                     this.dropItemWithOffset(Items.stick, 1, 0.0F);
                  }
               }
            }

            this.fallDistance = 0.0F;
         }
      } else if (this.worldObj.getBlockState(new BlockPos(this).down()).getBlock().getMaterial() != Material.water && y < 0.0) {
         this.fallDistance = (float)((double)this.fallDistance - y);
      }
   }

   public void setDamageTaken(float p_70266_1_) {
      this.dataWatcher.updateObject(19, p_70266_1_);
   }

   public float getDamageTaken() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public void setTimeSinceHit(int p_70265_1_) {
      this.dataWatcher.updateObject(17, p_70265_1_);
   }

   public int getTimeSinceHit() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public void setForwardDirection(int p_70269_1_) {
      this.dataWatcher.updateObject(18, p_70269_1_);
   }

   public int getForwardDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public void setIsBoatEmpty(boolean p_70270_1_) {
      this.isBoatEmpty = p_70270_1_;
   }
}
