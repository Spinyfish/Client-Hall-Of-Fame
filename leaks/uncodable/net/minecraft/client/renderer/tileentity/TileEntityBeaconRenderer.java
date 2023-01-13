package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon> {
   private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

   public void renderTileEntityAt(TileEntityBeacon te, double x, double y, double z, float partialTicks, int destroyStage) {
      float f = te.shouldBeamRender();
      if ((double)f > 0.0) {
         if (Config.isShaders()) {
            Shaders.beginBeacon();
         }

         GlStateManager.alphaFunc(516, 0.1F);
         if (f > 0.0F) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            GlStateManager.disableFog();
            List<TileEntityBeacon.BeamSegment> list = te.getBeamSegments();
            int i = 0;

            for(TileEntityBeacon.BeamSegment tileentitybeacon$beamsegment : list) {
               int k = i + tileentitybeacon$beamsegment.getHeight();
               this.bindTexture(beaconBeam);
               GL11.glTexParameterf(3553, 10242, 10497.0F);
               GL11.glTexParameterf(3553, 10243, 10497.0F);
               GlStateManager.disableLighting();
               GlStateManager.disableCull();
               GlStateManager.disableBlend();
               GlStateManager.depthMask(true);
               GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
               double d0 = (double)te.getWorld().getTotalWorldTime() + (double)partialTicks;
               double d1 = MathHelper.func_181162_h(-d0 * 0.2 - (double)MathHelper.floor_double(-d0 * 0.1));
               float f1 = tileentitybeacon$beamsegment.getColors()[0];
               float f2 = tileentitybeacon$beamsegment.getColors()[1];
               float f3 = tileentitybeacon$beamsegment.getColors()[2];
               double d2 = d0 * 0.025 * -1.5;
               double d3 = 0.2;
               double d4 = 0.5 + Math.cos(d2 + (Math.PI * 3.0 / 4.0)) * 0.2;
               double d5 = 0.5 + Math.sin(d2 + (Math.PI * 3.0 / 4.0)) * 0.2;
               double d6 = 0.5 + Math.cos(d2 + (Math.PI / 4)) * 0.2;
               double d7 = 0.5 + Math.sin(d2 + (Math.PI / 4)) * 0.2;
               double d8 = 0.5 + Math.cos(d2 + (Math.PI * 5.0 / 4.0)) * 0.2;
               double d9 = 0.5 + Math.sin(d2 + (Math.PI * 5.0 / 4.0)) * 0.2;
               double d10 = 0.5 + Math.cos(d2 + (Math.PI * 7.0 / 4.0)) * 0.2;
               double d11 = 0.5 + Math.sin(d2 + (Math.PI * 7.0 / 4.0)) * 0.2;
               double d14 = -1.0 + d1;
               double d15 = (double)((float)tileentitybeacon$beamsegment.getHeight() * f) * 2.5 + d14;
               worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
               worldrenderer.func_181662_b(x + d4, y + (double)k, z + d5).func_181673_a(1.0, d15).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d4, y + (double)i, z + d5).func_181673_a(1.0, d14).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d6, y + (double)i, z + d7).func_181673_a(0.0, d14).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d6, y + (double)k, z + d7).func_181673_a(0.0, d15).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d10, y + (double)k, z + d11).func_181673_a(1.0, d15).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d10, y + (double)i, z + d11).func_181673_a(1.0, d14).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d8, y + (double)i, z + d9).func_181673_a(0.0, d14).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d8, y + (double)k, z + d9).func_181673_a(0.0, d15).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d6, y + (double)k, z + d7).func_181673_a(1.0, d15).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d6, y + (double)i, z + d7).func_181673_a(1.0, d14).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d10, y + (double)i, z + d11).func_181673_a(0.0, d14).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d10, y + (double)k, z + d11).func_181673_a(0.0, d15).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d8, y + (double)k, z + d9).func_181673_a(1.0, d15).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d8, y + (double)i, z + d9).func_181673_a(1.0, d14).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d4, y + (double)i, z + d5).func_181673_a(0.0, d14).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               worldrenderer.func_181662_b(x + d4, y + (double)k, z + d5).func_181673_a(0.0, d15).func_181666_a(f1, f2, f3, 1.0F).func_181675_d();
               tessellator.draw();
               GlStateManager.enableBlend();
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               GlStateManager.depthMask(false);
               double d12 = -1.0 + d1;
               double d13 = (double)((float)tileentitybeacon$beamsegment.getHeight() * f) + d12;
               worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
               worldrenderer.func_181662_b(x + 0.2, y + (double)k, z + 0.2).func_181673_a(1.0, d13).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.2, y + (double)i, z + 0.2).func_181673_a(1.0, d12).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.8, y + (double)i, z + 0.2).func_181673_a(0.0, d12).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.8, y + (double)k, z + 0.2).func_181673_a(0.0, d13).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.8, y + (double)k, z + 0.8).func_181673_a(1.0, d13).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.8, y + (double)i, z + 0.8).func_181673_a(1.0, d12).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.2, y + (double)i, z + 0.8).func_181673_a(0.0, d12).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.2, y + (double)k, z + 0.8).func_181673_a(0.0, d13).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.8, y + (double)k, z + 0.2).func_181673_a(1.0, d13).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.8, y + (double)i, z + 0.2).func_181673_a(1.0, d12).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.8, y + (double)i, z + 0.8).func_181673_a(0.0, d12).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.8, y + (double)k, z + 0.8).func_181673_a(0.0, d13).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.2, y + (double)k, z + 0.8).func_181673_a(1.0, d13).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.2, y + (double)i, z + 0.8).func_181673_a(1.0, d12).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.2, y + (double)i, z + 0.2).func_181673_a(0.0, d12).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               worldrenderer.func_181662_b(x + 0.2, y + (double)k, z + 0.2).func_181673_a(0.0, d13).func_181666_a(f1, f2, f3, 0.125F).func_181675_d();
               tessellator.draw();
               GlStateManager.enableLighting();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               i = k;
            }

            GlStateManager.enableFog();
         }

         if (Config.isShaders()) {
            Shaders.endBeacon();
         }
      }
   }

   @Override
   public boolean func_181055_a() {
      return true;
   }
}
