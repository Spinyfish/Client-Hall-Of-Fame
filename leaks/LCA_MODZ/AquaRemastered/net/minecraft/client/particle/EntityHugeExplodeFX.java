package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityHugeExplodeFX extends EntityFX {
   private int timeSinceStart;
   private int maximumTime = 8;

   protected EntityHugeExplodeFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i1214_8_, double p_i1214_10_, double p_i1214_12_) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
   }

   @Override
   public void onUpdate() {
      for(int i = 0; i < 6; ++i) {
         double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double d1 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d0, d1, d2, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0, 0.0);
      }

      ++this.timeSinceStart;
      if (this.timeSinceStart == this.maximumTime) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 1;
   }

   public static class Factory implements IParticleFactory {
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
         return new EntityHugeExplodeFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
