package me.uncodable.srt.impl.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

public class RenderUtils {
   private static final Minecraft MC = Minecraft.getMinecraft();

   public static void renderBox(Entity entityIn, double p_85094_2_, double p_85094_4_, double p_85094_6_) {
      GlStateManager.depthMask(false);
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      float f = entityIn.width / 2.0F;
      AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
      AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(
         axisalignedbb.minX - entityIn.posX + p_85094_2_,
         axisalignedbb.minY - entityIn.posY + p_85094_4_,
         axisalignedbb.minZ - entityIn.posZ + p_85094_6_,
         axisalignedbb.maxX - entityIn.posX + p_85094_2_,
         axisalignedbb.maxY - entityIn.posY + p_85094_4_,
         axisalignedbb.maxZ - entityIn.posZ + p_85094_6_
      );
      RenderGlobal.func_181563_a(axisalignedbb1, 255, 255, 255, 255);
      if (entityIn instanceof EntityLivingBase) {
         RenderGlobal.func_181563_a(
            new AxisAlignedBB(
               p_85094_2_ - (double)f,
               p_85094_4_ + (double)entityIn.getEyeHeight() - 0.01F,
               p_85094_6_ - (double)f,
               p_85094_2_ + (double)f,
               p_85094_4_ + (double)entityIn.getEyeHeight() + 0.01F,
               p_85094_6_ + (double)f
            ),
            255,
            0,
            0,
            255
         );
      }

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      worldrenderer.func_181662_b(p_85094_2_, p_85094_4_ + (double)entityIn.getEyeHeight(), p_85094_6_).func_181669_b(0, 0, 255, 255).func_181675_d();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.enableLighting();
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   public static void drawTextWithScale(String text, double x, double y, float scale, int color) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, 0.0);
      GlStateManager.scale(scale, scale, scale);
      MC.fontRendererObj.drawStringWithShadow(text, 0.0F, 0.0F, color);
      GlStateManager.popMatrix();
   }

   public static void drawCustomImage(int x, int y, int width, int height, int textureWidth, int textureHeight, ResourceLocation image) {
      GlStateManager.pushMatrix();
      MC.getTextureManager().bindTexture(image);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, (float)textureWidth, (float)textureHeight);
      GlStateManager.popMatrix();
   }

   public static void drawHorizontalLine(int startX, int endX, int y, int color) {
      if (endX < startX) {
         int i = startX;
         startX = endX;
         endX = i;
      }

      Gui.drawRect(startX, y, endX + 1, y + 1, color);
   }

   public static void drawVerticalLine(int x, int startY, int endY, int color) {
      if (endY < startY) {
         int i = startY;
         startY = endY;
         endY = i;
      }

      Gui.drawRect(x, startY + 1, x + 1, endY, color);
   }

   public static String getFPSLevel() {
      if (MathUtils.isValueInRange(Minecraft.debugFPS, RenderUtils.FPSLevel.VERY_LOW.getMinimum(), RenderUtils.FPSLevel.VERY_LOW.getMaximum())) {
         return "Very Low";
      } else if (MathUtils.isValueInRange(Minecraft.debugFPS, RenderUtils.FPSLevel.LOW.getMinimum(), RenderUtils.FPSLevel.LOW.getMaximum())) {
         return "Low";
      } else if (MathUtils.isValueInRange(Minecraft.debugFPS, RenderUtils.FPSLevel.MEDIUM.getMinimum(), RenderUtils.FPSLevel.MEDIUM.getMaximum())) {
         return "Medium";
      } else {
         return MathUtils.isValueInRange(Minecraft.debugFPS, RenderUtils.FPSLevel.HIGH.getMinimum(), RenderUtils.FPSLevel.HIGH.getMaximum())
            ? "High"
            : "No Matches.";
      }
   }

   public static enum FPSLevel {
      VERY_LOW(0, 60),
      LOW(60, 250),
      MEDIUM(250, 650),
      HIGH(650, Integer.MAX_VALUE);

      private final int minimum;
      private final int maximum;

      private FPSLevel(int minimum, int maximum) {
         this.minimum = minimum;
         this.maximum = maximum;
      }

      public int getMinimum() {
         return this.minimum;
      }

      public int getMaximum() {
         return this.maximum;
      }
   }
}
