package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui {
   public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
   protected static float zLevel;

   public static void drawHollowRect(double x, double y, double width, double height, double lineWidth, int color) {
      drawRect2(x, y, x + lineWidth, y + height, color);
      drawRect2(x + width - lineWidth, y, x + width, y + height, color);
      drawRect2(x, y, x + width, y + lineWidth, color);
      drawRect2(x, y + height - lineWidth, x + width, y + height, color);
   }

   public static void drawRect2(double x1, double y1, double x2, double y2, int argbColor) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (x1 < x2) {
         double temp = x1;
         x1 = x2;
         x2 = temp;
      }

      if (y1 < y2) {
         double temp1 = y1;
         y1 = y2;
         y2 = temp1;
      }

      float a = (float)(argbColor >> 24 & 0xFF) / 255.0F;
      float r = (float)(argbColor >> 16 & 0xFF) / 255.0F;
      float g = (float)(argbColor >> 8 & 0xFF) / 255.0F;
      float b = (float)(argbColor & 0xFF) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(r, g, b, a);
      worldrenderer.begin(7, DefaultVertexFormats.POSITION);
      worldrenderer.pos(x1, y2, 0.0).endVertex();
      worldrenderer.pos(x2, y2, 0.0).endVertex();
      worldrenderer.pos(x2, y1, 0.0).endVertex();
      worldrenderer.pos(x1, y1, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
      float f = (float)(startColor >> 24 & 0xFF) / 255.0F;
      float f1 = (float)(startColor >> 16 & 0xFF) / 255.0F;
      float f2 = (float)(startColor >> 8 & 0xFF) / 255.0F;
      float f3 = (float)(startColor & 0xFF) / 255.0F;
      float f4 = (float)(endColor >> 24 & 0xFF) / 255.0F;
      float f5 = (float)(endColor >> 16 & 0xFF) / 255.0F;
      float f6 = (float)(endColor >> 8 & 0xFF) / 255.0F;
      float f7 = (float)(endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldrenderer.pos(right, top, (double)zLevel).color(f1, f2, f3, f).endVertex();
      worldrenderer.pos(left, top, (double)zLevel).color(f1, f2, f3, f).endVertex();
      worldrenderer.pos(left, bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
      worldrenderer.pos(right, bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   protected void drawHorizontalLine(int startX, int endX, int y, int color) {
      if (endX < startX) {
         int i = startX;
         startX = endX;
         endX = i;
      }

      drawRect(startX, y, endX + 1, y + 1, color);
   }

   protected void drawVerticalLine(int x, int startY, int endY, int color) {
      if (endY < startY) {
         int i = startY;
         startY = endY;
         endY = i;
      }

      drawRect(x, startY + 1, x + 1, endY, color);
   }

   public static void drawRect(int left, int top, int right, int bottom, int color) {
      if (left < right) {
         int i = left;
         left = right;
         right = i;
      }

      if (top < bottom) {
         int j = top;
         top = bottom;
         bottom = j;
      }

      float f3 = (float)(color >> 24 & 0xFF) / 255.0F;
      float f = (float)(color >> 16 & 0xFF) / 255.0F;
      float f1 = (float)(color >> 8 & 0xFF) / 255.0F;
      float f2 = (float)(color & 0xFF) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(f, f1, f2, f3);
      worldrenderer.begin(7, DefaultVertexFormats.POSITION);
      worldrenderer.pos((double)left, (double)bottom, 0.0).endVertex();
      worldrenderer.pos((double)right, (double)bottom, 0.0).endVertex();
      worldrenderer.pos((double)right, (double)top, 0.0).endVertex();
      worldrenderer.pos((double)left, (double)top, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float f = (float)(startColor >> 24 & 0xFF) / 255.0F;
      float f1 = (float)(startColor >> 16 & 0xFF) / 255.0F;
      float f2 = (float)(startColor >> 8 & 0xFF) / 255.0F;
      float f3 = (float)(startColor & 0xFF) / 255.0F;
      float f4 = (float)(endColor >> 24 & 0xFF) / 255.0F;
      float f5 = (float)(endColor >> 16 & 0xFF) / 255.0F;
      float f6 = (float)(endColor >> 8 & 0xFF) / 255.0F;
      float f7 = (float)(endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldrenderer.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
      worldrenderer.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
      worldrenderer.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
      worldrenderer.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
   }

   public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, (float)x, (float)y, color);
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos((double)(x + 0), (double)(y + height), (double)zLevel)
         .tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1))
         .endVertex();
      worldrenderer.pos((double)(x + width), (double)(y + height), (double)zLevel)
         .tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1))
         .endVertex();
      worldrenderer.pos((double)(x + width), (double)(y + 0), (double)zLevel)
         .tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1))
         .endVertex();
      worldrenderer.pos((double)(x + 0), (double)(y + 0), (double)zLevel)
         .tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1))
         .endVertex();
      tessellator.draw();
   }

   public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos((double)(xCoord + 0.0F), (double)(yCoord + (float)maxV), (double)zLevel)
         .tex((double)((float)(minU + 0) * f), (double)((float)(minV + maxV) * f1))
         .endVertex();
      worldrenderer.pos((double)(xCoord + (float)maxU), (double)(yCoord + (float)maxV), (double)zLevel)
         .tex((double)((float)(minU + maxU) * f), (double)((float)(minV + maxV) * f1))
         .endVertex();
      worldrenderer.pos((double)(xCoord + (float)maxU), (double)(yCoord + 0.0F), (double)zLevel)
         .tex((double)((float)(minU + maxU) * f), (double)((float)(minV + 0) * f1))
         .endVertex();
      worldrenderer.pos((double)(xCoord + 0.0F), (double)(yCoord + 0.0F), (double)zLevel)
         .tex((double)((float)(minU + 0) * f), (double)((float)(minV + 0) * f1))
         .endVertex();
      tessellator.draw();
   }

   public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos((double)(xCoord + 0), (double)(yCoord + heightIn), (double)zLevel)
         .tex((double)textureSprite.getMinU(), (double)textureSprite.getMaxV())
         .endVertex();
      worldrenderer.pos((double)(xCoord + widthIn), (double)(yCoord + heightIn), (double)zLevel)
         .tex((double)textureSprite.getMaxU(), (double)textureSprite.getMaxV())
         .endVertex();
      worldrenderer.pos((double)(xCoord + widthIn), (double)(yCoord + 0), (double)zLevel)
         .tex((double)textureSprite.getMaxU(), (double)textureSprite.getMinV())
         .endVertex();
      worldrenderer.pos((double)(xCoord + 0), (double)(yCoord + 0), (double)zLevel)
         .tex((double)textureSprite.getMinU(), (double)textureSprite.getMinV())
         .endVertex();
      tessellator.draw();
   }

   public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
      float f = 1.0F / textureWidth;
      float f1 = 1.0F / textureHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos((double)x, (double)(y + height), 0.0).tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex();
      worldrenderer.pos((double)(x + width), (double)(y + height), 0.0).tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex();
      worldrenderer.pos((double)(x + width), (double)y, 0.0).tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex();
      worldrenderer.pos((double)x, (double)y, 0.0).tex((double)(u * f), (double)(v * f1)).endVertex();
      tessellator.draw();
   }

   public static void drawScaledCustomSizeModalRect(
      int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight
   ) {
      float f = 1.0F / tileWidth;
      float f1 = 1.0F / tileHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos((double)x, (double)(y + height), 0.0).tex((double)(u * f), (double)((v + (float)vHeight) * f1)).endVertex();
      worldrenderer.pos((double)(x + width), (double)(y + height), 0.0)
         .tex((double)((u + (float)uWidth) * f), (double)((v + (float)vHeight) * f1))
         .endVertex();
      worldrenderer.pos((double)(x + width), (double)y, 0.0).tex((double)((u + (float)uWidth) * f), (double)(v * f1)).endVertex();
      worldrenderer.pos((double)x, (double)y, 0.0).tex((double)(u * f), (double)(v * f1)).endVertex();
      tessellator.draw();
   }
}
