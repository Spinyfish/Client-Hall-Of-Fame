package intent.AquaDev.aqua.utils;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.cape.GIF;
import intent.AquaDev.aqua.modules.visual.HUD;
import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
   private GuiScreen customcape;
   static Minecraft mc = Minecraft.getMinecraft();

   public static void drawBlockESP(BlockPos blockPos, float red, float green, float blue, float alpha, float lineAlpha, float lineWidth) {
      GlStateManager.color(red, green, blue, alpha);
      float x = (float)((double)blockPos.getX() - mc.getRenderManager().getRenderPosX());
      float y = (float)((double)blockPos.getY() - mc.getRenderManager().getRenderPosY());
      float z = (float)((double)blockPos.getZ() - mc.getRenderManager().getRenderPosZ());
      Block block = mc.theWorld.getBlockState(blockPos).getBlock();
      drawBoundingBox(
         new AxisAlignedBB(
            (double)x,
            (double)y,
            (double)z,
            (double)x + block.getBlockBoundsMaxX(),
            (double)y + block.getBlockBoundsMaxY(),
            (double)z + block.getBlockBoundsMaxZ()
         )
      );
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawBoundingBox(AxisAlignedBB a) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION);
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.minY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.minY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.maxY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.maxY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.minY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.minY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.maxY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.maxY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.minY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.minY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.maxY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.maxY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.minY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.minY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.maxY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.maxY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.minY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.minY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.minY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.minY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.maxY), (double)((float)a.minZ)).endVertex();
      worldrenderer.pos((double)((float)a.minX), (double)((float)a.maxY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.maxY), (double)((float)a.maxZ)).endVertex();
      worldrenderer.pos((double)((float)a.maxX), (double)((float)a.maxY), (double)((float)a.minZ)).endVertex();
      worldrenderer.endVertex();
      tessellator.draw();
   }

   public static void polygon(double x, double y, double sideLength, double amountOfSides, boolean filled, Color color) {
      sideLength /= 2.0;
      start();
      if (color != null) {
         setGLColor(color);
      }

      if (!filled) {
         GL11.glLineWidth(2.0F);
      }

      GL11.glEnable(2848);
      begin(filled ? 6 : 3);

      for(double i = 0.0; i <= amountOfSides / 4.0; ++i) {
         double angle = i * 4.0 * (Math.PI * 2) / 360.0;
         vertex(x + sideLength * Math.cos(angle) + sideLength, y + sideLength * Math.sin(angle) + sideLength);
      }

      end();
      GL11.glDisable(2848);
      stop();
   }

   public static void start() {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3553);
      GL11.glDisable(2884);
      GlStateManager.disableAlpha();
      GlStateManager.disableDepth();
   }

   public void circle(double x, double y, double radius, boolean filled, Color color) {
      polygon(x, y, radius, 360.0, filled, color);
   }

   public void circle(double x, double y, double radius, boolean filled) {
      this.polygon(x, y, radius, 360, filled);
   }

   public static void circle(double x, double y, double radius, Color color) {
      polygon(x, y, radius, 360, color);
   }

   public static void stop() {
      GlStateManager.enableAlpha();
      GlStateManager.enableDepth();
      GL11.glEnable(2884);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      color(Color.white);
   }

   public static void vertex(double x, double y) {
      GL11.glVertex2d(x, y);
   }

   public static void begin(int glMode) {
      GL11.glBegin(glMode);
   }

   public static void end() {
      GL11.glEnd();
   }

   public void polygon(double x, double y, double sideLength, int amountOfSides, boolean filled) {
      polygon(x, y, sideLength, (double)amountOfSides, filled, null);
   }

   public static void polygon(double x, double y, double sideLength, int amountOfSides, Color color) {
      polygon(x, y, sideLength, (double)amountOfSides, true, color);
   }

   public void polygon(double x, double y, double sideLength, int amountOfSides) {
      polygon(x, y, sideLength, (double)amountOfSides, true, null);
   }

   public static void drawEntityServerESP(Entity entity, float red, float green, float blue, float alpha, float lineAlpha, float lineWidth) {
      double d0 = (double)entity.serverPosX / 32.0;
      double d1 = (double)entity.serverPosY / 32.0;
      double d2 = (double)entity.serverPosZ / 32.0;
      if (entity instanceof EntityLivingBase) {
         EntityLivingBase livingBase = (EntityLivingBase)entity;
         d0 = livingBase.realPos.xCoord;
         d1 = livingBase.realPos.yCoord;
         d2 = livingBase.realPos.zCoord;
      }

      float x = (float)(d0 - mc.getRenderManager().getRenderPosX());
      float y = (float)(d1 - mc.getRenderManager().getRenderPosY());
      float z = (float)(d2 - mc.getRenderManager().getRenderPosZ());
      GL11.glColor4f(red, green, blue, alpha);
      otherDrawBoundingBox(entity, x, y, z, (double)(entity.width - 0.2F), (double)(entity.height + 0.1F));
      if (lineWidth > 0.0F) {
         GL11.glLineWidth(lineWidth);
         GL11.glColor4f(red, green, blue, lineAlpha);
         otherDrawOutlinedBoundingBox(entity, x, y, z, (double)(entity.width - 0.2F), (double)(entity.height + 0.1F));
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void otherDrawOutlinedBoundingBox(Entity entity, float x, float y, float z, double width, double height) {
      width *= 1.5;
      float yaw1 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 45.0F;
      float newYaw1;
      if (yaw1 < 0.0F) {
         newYaw1 = 0.0F;
         newYaw1 += 360.0F - Math.abs(yaw1);
      } else {
         newYaw1 = yaw1;
      }

      newYaw1 *= -1.0F;
      newYaw1 = (float)((double)newYaw1 * (Math.PI / 180.0));
      float yaw2 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 135.0F;
      float newYaw2;
      if (yaw2 < 0.0F) {
         newYaw2 = 0.0F;
         newYaw2 += 360.0F - Math.abs(yaw2);
      } else {
         newYaw2 = yaw2;
      }

      newYaw2 *= -1.0F;
      newYaw2 = (float)((double)newYaw2 * (Math.PI / 180.0));
      float yaw3 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 225.0F;
      float newYaw3;
      if (yaw3 < 0.0F) {
         newYaw3 = 0.0F;
         newYaw3 += 360.0F - Math.abs(yaw3);
      } else {
         newYaw3 = yaw3;
      }

      newYaw3 *= -1.0F;
      newYaw3 = (float)((double)newYaw3 * (Math.PI / 180.0));
      float yaw4 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 315.0F;
      float newYaw4;
      if (yaw4 < 0.0F) {
         newYaw4 = 0.0F;
         newYaw4 += 360.0F - Math.abs(yaw4);
      } else {
         newYaw4 = yaw4;
      }

      newYaw4 *= -1.0F;
      newYaw4 = (float)((double)newYaw4 * (Math.PI / 180.0));
      float x1 = (float)(Math.sin((double)newYaw1) * width + (double)x);
      float z1 = (float)(Math.cos((double)newYaw1) * width + (double)z);
      float x2 = (float)(Math.sin((double)newYaw2) * width + (double)x);
      float z2 = (float)(Math.cos((double)newYaw2) * width + (double)z);
      float x3 = (float)(Math.sin((double)newYaw3) * width + (double)x);
      float z3 = (float)(Math.cos((double)newYaw3) * width + (double)z);
      float x4 = (float)(Math.sin((double)newYaw4) * width + (double)x);
      float z4 = (float)(Math.cos((double)newYaw4) * width + (double)z);
      float y2 = (float)((double)y + height);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(3, DefaultVertexFormats.POSITION);
      worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
      worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
      worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
      worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
      worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
      worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
      worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
      worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
      worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
      worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
      worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
      worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
      worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
      worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
      worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
      worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
      worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
      worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
      worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
      worldrenderer.endVertex();
      tessellator.draw();
   }

   public static void otherDrawBoundingBox(Entity entity, float x, float y, float z, double width, double height) {
      width *= 1.5;
      float yaw1 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 45.0F;
      float newYaw1;
      if (yaw1 < 0.0F) {
         newYaw1 = 0.0F;
         newYaw1 += 360.0F - Math.abs(yaw1);
      } else {
         newYaw1 = yaw1;
      }

      newYaw1 *= -1.0F;
      newYaw1 = (float)((double)newYaw1 * (Math.PI / 180.0));
      float yaw2 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 135.0F;
      float newYaw2;
      if (yaw2 < 0.0F) {
         newYaw2 = 0.0F;
         newYaw2 += 360.0F - Math.abs(yaw2);
      } else {
         newYaw2 = yaw2;
      }

      newYaw2 *= -1.0F;
      newYaw2 = (float)((double)newYaw2 * (Math.PI / 180.0));
      float yaw3 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 225.0F;
      float newYaw3;
      if (yaw3 < 0.0F) {
         newYaw3 = 0.0F;
         newYaw3 += 360.0F - Math.abs(yaw3);
      } else {
         newYaw3 = yaw3;
      }

      newYaw3 *= -1.0F;
      newYaw3 = (float)((double)newYaw3 * (Math.PI / 180.0));
      float yaw4 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 315.0F;
      float newYaw4;
      if (yaw4 < 0.0F) {
         newYaw4 = 0.0F;
         newYaw4 += 360.0F - Math.abs(yaw4);
      } else {
         newYaw4 = yaw4;
      }

      newYaw4 *= -1.0F;
      newYaw4 = (float)((double)newYaw4 * (Math.PI / 180.0));
      float x1 = (float)(Math.sin((double)newYaw1) * width + (double)x);
      float z1 = (float)(Math.cos((double)newYaw1) * width + (double)z);
      float x2 = (float)(Math.sin((double)newYaw2) * width + (double)x);
      float z2 = (float)(Math.cos((double)newYaw2) * width + (double)z);
      float x3 = (float)(Math.sin((double)newYaw3) * width + (double)x);
      float z3 = (float)(Math.cos((double)newYaw3) * width + (double)z);
      float x4 = (float)(Math.sin((double)newYaw4) * width + (double)x);
      float z4 = (float)(Math.cos((double)newYaw4) * width + (double)z);
      float y2 = (float)((double)y + height);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION);
      worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
      worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
      worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
      worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
      worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
      worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
      worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
      worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
      worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
      worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
      worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
      worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
      worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
      worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
      worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
      worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
      worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
      worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
      worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
      worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
      worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
      worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
      worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
      worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
      worldrenderer.endVertex();
      tessellator.draw();
   }

   public static void drawTriangleFilled(float x, float y, float width, float height, int color) {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glEnable(2848);
      GL11.glLineWidth(1.0F);
      GL11.glBegin(1);
      glColor(color);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      GL11.glVertex2d((double)(x + width * 2.0F), (double)y);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glEnd();
      GL11.glDisable(2848);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public static void drawTriangleFilledReversed(float x, float y, float width, float height, int color) {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glEnable(2848);
      GL11.glLineWidth(1.0F);
      GL11.glBegin(1);
      glColor(color);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glVertex2d((double)(x + width), (double)(y - height));
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex2d((double)(x + width), (double)(y - height));
      GL11.glVertex2d((double)(x + width * 2.0F), (double)y);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glEnd();
      GL11.glDisable(2848);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public static void drawTriangleFilled2(float x, float y, float width, float height, int color) {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glLineWidth(1.0F);
      glColor(color);
      GL11.glBegin(9);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      GL11.glVertex2d((double)(x + width * 2.0F), (double)y);
      GL11.glVertex2d((double)(x + width * 2.0F), (double)y);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glEnd();
      GL11.glDisable(2848);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public static double interpolate(double current, double old, double scale) {
      return old + (current - old) * scale;
   }

   public static void drawImage(int x, int y, int width, int height, ResourceLocation resourceLocation) {
      GlStateManager.enableAlpha();
      mc.getTextureManager().bindTexture(resourceLocation);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, (float)width, (float)height);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawGif(int x, int y, int width, int height, String getGif) {
      GlStateManager.enableAlpha();
      GIF gif = Aqua.INSTANCE.GIFmgr.getGIFByName(getGif);
      mc.getTextureManager().bindTexture(gif.getNext().getLocation());
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, (float)width, (float)height);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawImageDarker(int x, int y, int width, int height, ResourceLocation resourceLocation) {
      mc.getTextureManager().bindTexture(resourceLocation);
      GL11.glColor4f(0.19607843F, 0.19607843F, 0.19607843F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, (float)width, (float)height);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawImageHUDColor(int x, int y, int width, int height, ResourceLocation resourceLocation) {
      mc.getTextureManager().bindTexture(resourceLocation);
      Color color = new Color(Aqua.setmgr.getSetting("HUDColor").getColor());
      GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, (float)width, (float)height);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawImageColored(int x, int y, int width, int height, Color color, ResourceLocation resourceLocation) {
      mc.getTextureManager().bindTexture(resourceLocation);
      GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, (float)width, (float)height);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawRoundedRect2(double x, double y, double width, double height, double cornerRadius, int color) {
      drawRoundedRect2(x, y, width, height, cornerRadius, true, true, true, true, color);
   }

   public static void drawRoundedRect(double x, double y, double width, double height, double cornerRadius, int color) {
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glBlendFunc(770, 771);
      setGLColor(color);
      GL11.glBegin(9);
      double cornerX = x + width - cornerRadius;
      double cornerY = y + height - cornerRadius;

      for(int i = 0; i <= 90; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + width - cornerRadius;
      cornerY = y + cornerRadius;

      for(int i = 90; i <= 180; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + cornerRadius;
      cornerY = y + cornerRadius;

      for(int i = 180; i <= 270; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + cornerRadius;
      cornerY = y + height - cornerRadius;

      for(int i = 270; i <= 360; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
      setGLColor(Color.white);
   }

   public static void drawRoundedRect2(
      double x,
      double y,
      double width,
      double height,
      double cornerRadius,
      boolean leftTop,
      boolean rightTop,
      boolean rightBottom,
      boolean leftBottom,
      int color
   ) {
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glEnable(3042);
      GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
      setGLColor(color);
      GL11.glBegin(9);
      double cornerX = x + width - cornerRadius;
      double cornerY = y + height - cornerRadius;
      if (rightBottom) {
         for(int i = 0; i <= 90; ++i) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
         }
      } else {
         GL11.glVertex2d(x + width, y + height);
      }

      if (rightTop) {
         cornerX = x + width - cornerRadius;
         cornerY = y + cornerRadius;

         for(int i = 90; i <= 180; ++i) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
         }
      } else {
         GL11.glVertex2d(x + width, y);
      }

      if (leftTop) {
         cornerX = x + cornerRadius;
         cornerY = y + cornerRadius;

         for(int i = 180; i <= 270; ++i) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
         }
      } else {
         GL11.glVertex2d(x, y);
      }

      if (leftBottom) {
         cornerX = x + cornerRadius;
         cornerY = y + height - cornerRadius;

         for(int i = 270; i <= 360; ++i) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
         }
      } else {
         GL11.glVertex2d(x, y + height);
      }

      GL11.glEnd();
      setGLColor(new Color(255, 255, 255, 255));
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void setGLColor(Color color) {
      float r = (float)color.getRed() / 255.0F;
      float g = (float)color.getGreen() / 255.0F;
      float b = (float)color.getBlue() / 255.0F;
      float a = (float)color.getAlpha() / 255.0F;
      GL11.glColor4f(r, g, b, a);
   }

   public static void setGLColor(int color) {
      setGLColor(new Color(color));
   }

   public static void drawRoundedRectGradient(double x, double y, double width, double height, double cornerRadius, Color start, Color end) {
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glShadeModel(7425);
      color(start);
      GL11.glBegin(9);
      double cornerX = x + width - cornerRadius;
      double cornerY = y + height - cornerRadius;

      for(int i = 0; i <= 90; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + width - cornerRadius;
      cornerY = y + cornerRadius;

      for(int i = 90; i <= 180; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      color(end);
      cornerX = x + cornerRadius;
      cornerY = y + cornerRadius;

      for(int i = 180; i <= 270; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + cornerRadius;
      cornerY = y + height - cornerRadius;

      for(int i = 270; i <= 360; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      GL11.glEnd();
      GL11.glShadeModel(7424);
      color(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void drawGradientRectHorizontal(double x, double y, double width, double height, int startColor, int endColor) {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glShadeModel(7425);
      GL11.glPushMatrix();
      GL11.glBegin(7);
      glColor(startColor);
      GL11.glVertex2d(x, y);
      GL11.glVertex2d(x, y + height);
      glColor(endColor);
      GL11.glVertex2d(x + width, y + height);
      GL11.glVertex2d(x + width, y);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glShadeModel(7424);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public static void glColor(int hex) {
      float alpha = (float)(hex >> 24 & 0xFF) / 255.0F;
      float red = (float)(hex >> 16 & 0xFF) / 255.0F;
      float green = (float)(hex >> 8 & 0xFF) / 255.0F;
      float blue = (float)(hex & 0xFF) / 255.0F;
      GL11.glColor4f(red, green, blue, alpha);
   }

   public static void color(float red, float green, float blue, float alpha) {
      GL11.glColor4f(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
   }

   public static void color(Color color) {
      color((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getAlpha());
   }

   public static void drawRoundedRect3(
      double x,
      double y,
      double width,
      double height,
      double cornerRadius,
      boolean leftTop,
      boolean rightTop,
      boolean rightBottom,
      boolean leftBottom,
      Color color
   ) {
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      setGLColor(color);
      GL11.glBegin(9);
      double cornerX = x + width - cornerRadius;
      double cornerY = y + height - cornerRadius;
      if (rightBottom) {
         for(int i = 0; i <= 90; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
         }
      } else {
         GL11.glVertex2d(x + width, y + height);
      }

      if (rightTop) {
         cornerX = x + width - cornerRadius;
         cornerY = y + cornerRadius;

         for(int i = 90; i <= 180; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
         }
      } else {
         GL11.glVertex2d(x + width, y);
      }

      if (leftTop) {
         cornerX = x + cornerRadius;
         cornerY = y + cornerRadius;

         for(int i = 180; i <= 270; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
         }
      } else {
         GL11.glVertex2d(x, y);
      }

      if (leftBottom) {
         cornerX = x + cornerRadius;
         cornerY = y + height - cornerRadius;

         for(int i = 270; i <= 360; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
         }
      } else {
         GL11.glVertex2d(x, y + height);
      }

      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
      setGLColor(Color.white);
   }

   public static void drawRoundedRect2Alpha(double x, double y, double width, double height, double cornerRadius, Color color) {
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      color(color);
      GL11.glBegin(9);
      double cornerX = x + width - cornerRadius;
      double cornerY = y + height - cornerRadius;

      for(int i = 0; i <= 90; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + width - cornerRadius;
      cornerY = y + cornerRadius;

      for(int i = 90; i <= 180; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + cornerRadius;
      cornerY = y + cornerRadius;

      for(int i = 180; i <= 270; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + cornerRadius;
      cornerY = y + height - cornerRadius;

      for(int i = 270; i <= 360; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      GL11.glEnd();
      GlStateManager.resetColor();
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void drawRoundedRectSmooth(double x, double y, double width, double height, double cornerRadius, Color color) {
      GL11.glPushMatrix();
      GL11.glDisable(3008);
      GL11.glEnable(2881);
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      color(color);
      GL11.glBegin(9);
      double cornerX = x + width - cornerRadius;
      double cornerY = y + height - cornerRadius;

      for(int i = 0; i <= 90; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + width - cornerRadius;
      cornerY = y + cornerRadius;

      for(int i = 90; i <= 180; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + cornerRadius;
      cornerY = y + cornerRadius;

      for(int i = 180; i <= 270; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      cornerX = x + cornerRadius;
      cornerY = y + height - cornerRadius;

      for(int i = 270; i <= 360; i += 30) {
         GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
      }

      GL11.glEnd();
      color(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(2881);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void drawRoundRectTest(
      float x1, float y1, float x2, float y2, float radius, int steps, boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom, Color color
   ) {
      GL11.glPushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      color(color);
      GL11.glBegin(9);
      radius = Math.min(Math.min(Math.abs(y2 - y1) / 2.0F, Math.abs(x2 - x1) / 2.0F), radius);
      steps = Math.max(1, steps - steps % 3);
      if (rightBottom) {
         for(int i = 0; i <= 90; i += steps) {
            GL11.glVertex2d(
               (double)(x2 - radius) + Math.sin(Math.toRadians((double)i)) * (double)radius,
               (double)(y2 - radius) + Math.cos(Math.toRadians((double)i)) * (double)radius
            );
         }
      } else {
         GL11.glVertex2d((double)x2, (double)y2);
      }

      if (rightTop) {
         for(int i = 90; i <= 180; i += steps) {
            GL11.glVertex2d(
               (double)(x2 - radius) + Math.sin(Math.toRadians((double)i)) * (double)radius,
               (double)(y1 + radius) + Math.cos(Math.toRadians((double)i)) * (double)radius
            );
         }
      } else {
         GL11.glVertex2d((double)x2, (double)y1);
      }

      if (leftTop) {
         for(int i = 180; i <= 270; i += steps) {
            GL11.glVertex2d(
               (double)(x1 + radius) + Math.sin(Math.toRadians((double)i)) * (double)radius,
               (double)(y1 + radius) + Math.cos(Math.toRadians((double)i)) * (double)radius
            );
         }
      } else {
         GL11.glVertex2d((double)x1, (double)y1);
      }

      if (leftBottom) {
         for(int i = 270; i <= 360; i += steps) {
            GL11.glVertex2d(
               (double)(x1 + radius) + Math.sin(Math.toRadians((double)i)) * (double)radius,
               (double)(y2 - radius) + Math.cos(Math.toRadians((double)i)) * (double)radius
            );
         }
      } else {
         GL11.glVertex2d((double)x1, (double)y2);
      }

      GL11.glEnd();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      color(Color.white);
      GL11.glPopMatrix();
   }

   public static void setupViewBobbing(float partialTicks) {
      if (mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)mc.getRenderViewEntity();
         float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
         float f1 = -(entityplayer.distanceWalkedModified + f * partialTicks);
         float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
         float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
         GlStateManager.translate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2), 0.0F);
         GlStateManager.rotate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
      }
   }

   public static boolean isSliderHovered(float x1, float y1, float x2, float y2, int mouseX, int mouseY) {
      return (float)mouseX >= x1 && (float)mouseX <= x2 && (float)mouseY >= y1 && (float)mouseY <= y2;
   }

   public static Color getColorAlpha(int color, int alpha) {
      return new Color(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), alpha);
   }

   public static void drawCircle(double x, double y, double radius, int color) {
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glDisable(2929);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glShadeModel(7425);
      GL11.glBegin(3);

      for(int i = 0; i < 360; ++i) {
         setGLColor(color);
         GL11.glVertex2d(x, y);
         double sin = Math.sin(Math.toRadians((double)i)) * radius;
         double cos = Math.cos(Math.toRadians((double)i)) * radius;
         GL11.glVertex2d(x + sin, y + cos);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glShadeModel(7424);
      GL11.glEnable(2929);
      GL11.glPopMatrix();
   }

   public static void scissor(double x, double y, double width, double height) {
      ScaledResolution sr = new ScaledResolution(mc);
      double scale = (double)sr.getScaleFactor();
      y = (double)sr.getScaledHeight() - y;
      x *= scale;
      y *= scale;
      width *= scale;
      height *= scale;
      GL11.glScissor((int)x, (int)(y - height), (int)width, (int)height);
   }

   public static void drawRGBLineHorizontal(double x, double y, double width, float linewidth, float colors, boolean reverse) {
      GlStateManager.shadeModel(7425);
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GL11.glLineWidth(linewidth);
      GL11.glBegin(3);
      colors = (float)((double)colors * width);
      double steps = width / (double)colors;
      double cX = x;
      double cX2 = x + steps;
      if (reverse) {
         for(float i = colors; i > 0.0F; --i) {
            int argbColor = HUD.rainbow((int)(i * 10.0F));
            float a = (float)(argbColor >> 24 & 0xFF) / 255.0F;
            float r = (float)(argbColor >> 16 & 0xFF) / 255.0F;
            float g = (float)(argbColor >> 8 & 0xFF) / 255.0F;
            float b = (float)(argbColor & 0xFF) / 255.0F;
            GlStateManager.color(r, g, b, a);
            GL11.glVertex2d(cX, y);
            GL11.glVertex2d(cX2, y);
            cX = cX2;
            cX2 += steps;
         }
      } else {
         for(int i = 0; (float)i < colors; ++i) {
            int argbColor = HUD.rainbow(i * 10);
            float a = (float)(argbColor >> 24 & 0xFF) / 255.0F;
            float r = (float)(argbColor >> 16 & 0xFF) / 255.0F;
            float g = (float)(argbColor >> 8 & 0xFF) / 255.0F;
            float b = (float)(argbColor & 0xFF) / 255.0F;
            GlStateManager.color(r, g, b, a);
            GL11.glVertex2d(cX, y);
            GL11.glVertex2d(cX2, y);
            cX = cX2;
            cX2 += steps;
         }
      }

      GL11.glEnd();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }
}
