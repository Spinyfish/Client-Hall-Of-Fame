package net.minecraft.client.particle;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDropParticleFX extends EntityFX {
   private Material materialType;
   private int bobTimer;

   protected EntityDropParticleFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, Material p_i1203_8_) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.motionX = this.motionY = this.motionZ = 0.0;
      if (p_i1203_8_ == Material.water) {
         this.particleRed = 0.0F;
         this.particleGreen = 0.0F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 0.0F;
         this.particleBlue = 0.0F;
      }

      this.setParticleTextureIndex(113);
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.materialType = p_i1203_8_;
      this.bobTimer = 40;
      this.particleMaxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
      this.motionX = this.motionY = this.motionZ = 0.0;
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return this.materialType == Material.water ? super.getBrightnessForRender(partialTicks) : 257;
   }

   @Override
   public float getBrightness(float partialTicks) {
      return this.materialType == Material.water ? super.getBrightness(partialTicks) : 1.0F;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.materialType == Material.water) {
         this.particleRed = 0.2F;
         this.particleGreen = 0.3F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 16.0F / (float)(40 - this.bobTimer + 16);
         this.particleBlue = 4.0F / (float)(40 - this.bobTimer + 8);
      }

      this.motionY -= (double)this.particleGravity;
      if (this.bobTimer-- > 0) {
         this.motionX *= 0.02;
         this.motionY *= 0.02;
         this.motionZ *= 0.02;
         this.setParticleTextureIndex(113);
      } else {
         this.setParticleTextureIndex(112);
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }

      if (this.onGround) {
         if (this.materialType == Material.water) {
            this.setDead();
            this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
         } else {
            this.setParticleTextureIndex(114);
         }

         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }

      BlockPos blockpos = new BlockPos(this);
      IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
      Material material = iblockstate.getBlock().getMaterial();
      if (material.isLiquid() || material.isSolid()) {
         double d0 = 0.0;
         if (iblockstate.getBlock() instanceof BlockLiquid) {
            d0 = (double)BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL));
         }

         double d1 = (double)(MathHelper.floor_double(this.posY) + 1) - d0;
         if (this.posY < d1) {
            this.setDead();
         }
      }
   }

   public static class LavaFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID,
         World worldIn,
         double xCoordIn,
         double yCoordIn,
         double zCoordIn,
         double xSpeedIn,
         double ySpeedIn,
         double zSpeedIn,
         int... p_178902_15_
      ) {
         return new EntityDropParticleFX(worldIn, xCoordIn, yCoordIn, zCoordIn, Material.lava);
      }
   }

   public static class WaterFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID,
         World worldIn,
         double xCoordIn,
         double yCoordIn,
         double zCoordIn,
         double xSpeedIn,
         double ySpeedIn,
         double zSpeedIn,
         int... p_178902_15_
      ) {
         return new EntityDropParticleFX(worldIn, xCoordIn, yCoordIn, zCoordIn, Material.water);
      }
   }
}
