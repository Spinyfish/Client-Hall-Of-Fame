package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFootStepFX extends EntityFX {
   private static final ResourceLocation FOOTPRINT_TEXTURE = new ResourceLocation("textures/particle/footprint.png");
   private int footstepAge;
   private final int footstepMaxAge;
   private final TextureManager currentFootSteps;

   protected EntityFootStepFX(TextureManager currentFootStepsIn, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.currentFootSteps = currentFootStepsIn;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.footstepMaxAge = 200;
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float p_180434_4_,
      float p_180434_5_,
      float p_180434_6_,
      float p_180434_7_,
      float p_180434_8_
   ) {
      float f = ((float)this.footstepAge + partialTicks) / (float)this.footstepMaxAge;
      f *= f;
      float f1 = 2.0F - f * 2.0F;
      if (f1 > 1.0F) {
         f1 = 1.0F;
      }

      f1 *= 0.2F;
      GlStateManager.disableLighting();
      float f2 = 0.125F;
      float f3 = (float)(this.posX - interpPosX);
      float f4 = (float)(this.posY - interpPosY);
      float f5 = (float)(this.posZ - interpPosZ);
      float f6 = this.worldObj.getLightBrightness(new BlockPos(this));
      this.currentFootSteps.bindTexture(FOOTPRINT_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      worldRendererIn.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      worldRendererIn.func_181662_b((double)(f3 - 0.125F), (double)f4, (double)(f5 + 0.125F))
         .func_181673_a(0.0, 1.0)
         .func_181666_a(f6, f6, f6, f1)
         .func_181675_d();
      worldRendererIn.func_181662_b((double)(f3 + 0.125F), (double)f4, (double)(f5 + 0.125F))
         .func_181673_a(1.0, 1.0)
         .func_181666_a(f6, f6, f6, f1)
         .func_181675_d();
      worldRendererIn.func_181662_b((double)(f3 + 0.125F), (double)f4, (double)(f5 - 0.125F))
         .func_181673_a(1.0, 0.0)
         .func_181666_a(f6, f6, f6, f1)
         .func_181675_d();
      worldRendererIn.func_181662_b((double)(f3 - 0.125F), (double)f4, (double)(f5 - 0.125F))
         .func_181673_a(0.0, 0.0)
         .func_181666_a(f6, f6, f6, f1)
         .func_181675_d();
      Tessellator.getInstance().draw();
      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
   }

   @Override
   public void onUpdate() {
      ++this.footstepAge;
      if (this.footstepAge == this.footstepMaxAge) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
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
         return new EntityFootStepFX(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn);
      }
   }
}
