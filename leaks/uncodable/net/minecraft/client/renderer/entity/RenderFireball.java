package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class RenderFireball extends Render<EntityFireball> {
   private final float scale;

   public RenderFireball(RenderManager renderManagerIn, float scaleIn) {
      super(renderManagerIn);
      this.scale = scaleIn;
   }

   public void doRender(EntityFireball entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture(entity);
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(this.scale, this.scale, this.scale);
      TextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.fire_charge);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      float f = textureatlassprite.getMinU();
      float f1 = textureatlassprite.getMaxU();
      float f2 = textureatlassprite.getMinV();
      float f3 = textureatlassprite.getMaxV();
      float f4 = 1.0F;
      float f5 = 0.5F;
      float f6 = 0.25F;
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181710_j);
      worldrenderer.func_181662_b(-0.5, -0.25, 0.0).func_181673_a((double)f, (double)f3).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      worldrenderer.func_181662_b(0.5, -0.25, 0.0).func_181673_a((double)f1, (double)f3).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      worldrenderer.func_181662_b(0.5, 0.75, 0.0).func_181673_a((double)f1, (double)f2).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      worldrenderer.func_181662_b(-0.5, 0.75, 0.0).func_181673_a((double)f, (double)f2).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      tessellator.draw();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected ResourceLocation getEntityTexture(EntityFireball entity) {
      return TextureMap.locationBlocksTexture;
   }
}
