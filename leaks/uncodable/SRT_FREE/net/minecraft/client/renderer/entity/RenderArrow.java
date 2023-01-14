package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderArrow extends Render<EntityArrow> {
   private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

   public RenderArrow(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.bindEntityTexture(entity);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      int i = 0;
      float f = 0.0F;
      float f1 = 0.5F;
      float f2 = 0.0F;
      float f3 = 0.15625F;
      float f4 = 0.0F;
      float f5 = 0.15625F;
      float f6 = 0.15625F;
      float f7 = 0.3125F;
      float f8 = 0.05625F;
      GlStateManager.enableRescaleNormal();
      float f9 = (float)entity.arrowShake - partialTicks;
      if (f9 > 0.0F) {
         float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
         GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(f8, f8, f8);
      GlStateManager.translate(-4.0F, 0.0F, 0.0F);
      GL11.glNormal3f(f8, 0.0F, 0.0F);
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      worldrenderer.func_181662_b(-7.0, -2.0, -2.0).func_181673_a((double)f4, (double)f6).func_181675_d();
      worldrenderer.func_181662_b(-7.0, -2.0, 2.0).func_181673_a((double)f5, (double)f6).func_181675_d();
      worldrenderer.func_181662_b(-7.0, 2.0, 2.0).func_181673_a((double)f5, (double)f7).func_181675_d();
      worldrenderer.func_181662_b(-7.0, 2.0, -2.0).func_181673_a((double)f4, (double)f7).func_181675_d();
      tessellator.draw();
      GL11.glNormal3f(-f8, 0.0F, 0.0F);
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      worldrenderer.func_181662_b(-7.0, 2.0, -2.0).func_181673_a((double)f4, (double)f6).func_181675_d();
      worldrenderer.func_181662_b(-7.0, 2.0, 2.0).func_181673_a((double)f5, (double)f6).func_181675_d();
      worldrenderer.func_181662_b(-7.0, -2.0, 2.0).func_181673_a((double)f5, (double)f7).func_181675_d();
      worldrenderer.func_181662_b(-7.0, -2.0, -2.0).func_181673_a((double)f4, (double)f7).func_181675_d();
      tessellator.draw();

      for(int j = 0; j < 4; ++j) {
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glNormal3f(0.0F, 0.0F, f8);
         worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         worldrenderer.func_181662_b(-8.0, -2.0, 0.0).func_181673_a((double)f, (double)f2).func_181675_d();
         worldrenderer.func_181662_b(8.0, -2.0, 0.0).func_181673_a((double)f1, (double)f2).func_181675_d();
         worldrenderer.func_181662_b(8.0, 2.0, 0.0).func_181673_a((double)f1, (double)f3).func_181675_d();
         worldrenderer.func_181662_b(-8.0, 2.0, 0.0).func_181673_a((double)f, (double)f3).func_181675_d();
         tessellator.draw();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected ResourceLocation getEntityTexture(EntityArrow entity) {
      return arrowTextures;
   }
}
