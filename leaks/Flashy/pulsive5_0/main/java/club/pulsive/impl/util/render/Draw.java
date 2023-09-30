package club.pulsive.impl.util.render;

import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.render.secondary.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;


import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import java.awt.Color;

/**
 * @author antja03
 */
public class Draw {

    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void dropShadow(int loops, float x, float y, float width, float height, float opacity, float edgeRadius) {
        for (int margin = 1; margin <= loops; ++margin) {
            final double mariner = margin * 2;
            Draw.drawRoundedRect(x - mariner / 2f, y - mariner / 2f,
                    width + mariner, height + mariner, (int) edgeRadius,
                    new Color(0, 0, 0, (int) ApacheMath.max(0.5f, (opacity - mariner * 1.2) / 5.5f)).hashCode());
        }
    }

    public static void drawRectangle(double left, double top, double right, double bottom, int color)
    {
        if (left < right)
        {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedRect(double x, double y, double width, double height, float cornerRadius) {
        final int slices = 10;

        drawFillRectangle(x + cornerRadius, y, width - 2 * cornerRadius, height);
        drawFillRectangle(x, y + cornerRadius, cornerRadius, height - 2 * cornerRadius);
        drawFillRectangle(x + width - cornerRadius, y + cornerRadius, cornerRadius, height - 2 * cornerRadius);

        drawCirclePart(x + cornerRadius, y + cornerRadius, -MathHelper.PI, -MathHelper.PId2, cornerRadius, slices);
        drawCirclePart(x + cornerRadius, y + height - cornerRadius, -MathHelper.PId2, 0.0F, cornerRadius, slices);

        drawCirclePart(x + width - cornerRadius, y + cornerRadius, MathHelper.PId2, MathHelper.PI, cornerRadius, slices);
        drawCirclePart(x + width - cornerRadius, y + height - cornerRadius, 0, MathHelper.PId2, cornerRadius, slices);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GlStateManager.disableBlend();

        //GlStateManager.enableAlpha();
        //GlStateManager.alphaFunc(GL11.GL_NOTEQUAL, 0);
    }

    public static void drawCirclePart(double x, double y, float fromAngle, float toAngle, float radius, int slices) {
        GlStateManager.enableBlend();
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(x, y);
        final float increment = (toAngle - fromAngle) / slices;

        for (int i = 0; i <= slices; i++) {
            final float angle = fromAngle + i * increment;

            final float dX = MathHelper.sin(angle);
            final float dY = MathHelper.cos(angle);

            GL11.glVertex2d(x + dX * radius, y + dY * radius);
        }
        GL11.glEnd();
    }

    public static void drawFillRectangle(double x, double y, double width, double height) {
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
    }
    public static void drawSmoothCircleESP(double x, double y, double width, double height, float radius) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(radius);
        GL11.glBegin(GL11.GL_POINTS);
        RenderUtils.color(new Color(255,255,255,255).getRGB());
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glPointSize(radius * 1.75f);
        GL11.glBegin(GL11.GL_POINTS);
        RenderUtils.color(new Color(255,255,255,155).getRGB());
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glPointSize((float) (radius * 2.5));
        GL11.glBegin(GL11.GL_POINTS);
        RenderUtils.color(new Color(255,255,255,55).getRGB());
        GL11.glVertex2d(x, y);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawSmoothCircle(double x, double y, double width, double height, float radius, int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(radius);
        GL11.glBegin(GL11.GL_POINTS);
        RenderUtils.color(new Color(255,255,255,255).getRGB());
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glPointSize(radius * 1.75f);
        GL11.glBegin(GL11.GL_POINTS);
        RenderUtils.color(new Color(255,255,255,155).getRGB());
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glPointSize((float) (radius * 2.5));
        GL11.glBegin(GL11.GL_POINTS);
        RenderUtils.color(new Color(255,255,255,55).getRGB());
        GL11.glVertex2d(x, y);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    public static void drawRoundedRect(double d, double e, double f, double g, int borderC, int insideC) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        drawVLine(d *= 2.0f, (e *= 2.0f) + 1.0f, (g *= 2.0f) - 2.0f, borderC);
        drawVLine((f *= 2.0f) - 1.0f, e + 1.0f, g - 2.0f, borderC);
        drawHLine(d + 2.0f, f - 3.0f, e, borderC);
        drawHLine(d + 2.0f, f - 3.0f, g - 1.0f, borderC);
        drawHLine(d + 1.0f, d + 1.0f, e + 1.0f, borderC);
        drawHLine(f - 2.0f, f - 2.0f, e + 1.0f, borderC);
        drawHLine(f - 2.0f, f - 2.0f, g - 2.0f, borderC);
        drawHLine(d + 1.0f, d + 1.0f, g - 2.0f, borderC);
        drawRectangle(d + 1.0f, e + 1.0f, f - 1.0f, g - 1.0f, insideC);
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawHLine(double d, double e, double f, int y1) {
        if (e < d) {
            double var5 = d;
            d = e;
            e = var5;
        }
        drawRectangle(d, f, e + 1.0f, f + 1.0f, y1);
    }

    public static void drawVLine(double d, double e, double f, int y1) {
        if (f < e) {
            double var5 = e;
            e = f;
            f = var5;
        }
        drawRectangle(d, e + 1.0f, d + 1.0f, f, y1);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 0.0D).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, 0.0D).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    public static void drawBorderedRectangle(double left, double top, double right, double bottom, double borderWidth, int insideColor, int borderColor, boolean borderIncludedInBounds) {
        drawRectangle(left - (!borderIncludedInBounds ? borderWidth : 0), top - (!borderIncludedInBounds ? borderWidth : 0), right + (!borderIncludedInBounds ? borderWidth : 0), bottom + (!borderIncludedInBounds ? borderWidth : 0), borderColor);
        drawRectangle(left + (borderIncludedInBounds ? borderWidth : 0), top + (borderIncludedInBounds ? borderWidth : 0), right - ((borderIncludedInBounds ? borderWidth : 0)), bottom - ((borderIncludedInBounds ? borderWidth : 0)), insideColor);
    }

    public static void drawBorderedCircle(double x, double y, float radius, int outsideC, int insideC) {
        GL11.glPushMatrix();
       // GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
      //  GL11.glEnable(2848);
        float scale = 0.1F;
        GL11.glScalef(scale, scale, scale);
        x = (int) (x * (1.0F / scale));
        y = (int) (y * (1.0F / scale));
        radius *= 1.0F / scale;
        drawCircle(x, y, radius, insideC);
        drawUnfilledCircle(x, y, radius, 1.0F, outsideC);
        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glEnable(3553);
        //GL11.glDisable(3042);
      // GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawUnfilledCircle(double x, double y, float radius, float lineWidth, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glBegin(2);
        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + ApacheMath.sin(i * 3.141526D / 180.0D) * radius, y + ApacheMath.cos(i * 3.141526D / 180.0D) * radius);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
    }

    public static void drawCircle(double x, double y, float radius, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);

        GL11.glBegin(9);
        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + ApacheMath.sin(i * 3.141526D / 180.0D) * radius, y + ApacheMath.cos(i * 3.141526D / 180.0D) * radius);
        }
        GL11.glEnd();
    }
    public static void drawColoredShadow( int posX, int posY, int width, int height, int color, float alpha) {

        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/arraylistshadow2.png"));
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        RenderUtils.colorAlpha(color, alpha);
        Gui.drawModalRectWithCustomSizedTexture(posX, posY, 0, 0, width,height, width, height);

    }
    public static void drawImg(ResourceLocation loc, double posX, double posY, double width, double height) {

        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
        float f = 1.0F / (float) width;
        float f1 = 1.0F / (float) height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(posX, (posY + height), 0.0D).tex((double)(0 * f), (double)((0 + (float)height) * f1)).endVertex();
        worldrenderer.pos((posX + width), (posY + height), 0.0D).tex((double)((0 + (float)width) * f), (double)((0 + (float)height) * f1)).endVertex();
        worldrenderer.pos((posX + width), posY, 0.0D).tex((double)((0 + (float)width) * f), (double)(0 * f1)).endVertex();
        worldrenderer.pos(posX, posY, 0.0D).tex((double)(0 * f), (double)(0 * f1)).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569F * redRGB;
        float green = 0.003921569F * greenRGB;
        float blue = 0.003921569F * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }


}
