package com.craftworks.pearclient.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;

public class GLUtils extends GLRectUtils
{
	private static Minecraft mc = Minecraft.getMinecraft();

    public static void scissor(float x, float y, float width, float height) {
        final int scaleFactor = getScaleFactor();
        GL11.glScissor((int)(x * scaleFactor), (int)(mc.displayHeight - (y + height) * scaleFactor), (int)(((x + width) - x) * scaleFactor), (int)(((y + height) - y) * scaleFactor));
    }
    
    
    public static int getScaleFactor() {
        int scaleFactor = 1;
        final boolean isUnicode = mc.isUnicode();
        int guiScale = mc.gameSettings.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }
        
        while (scaleFactor < guiScale && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }
        return scaleFactor;
    }
    
    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, (float) (limit * .01));
    }
    
    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }
    
    public static void bindTexture(int texture) {
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    }
    
    public static void startScale(float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-x, -y, 0);
    }
    
    public static void startScale(float x, float y, float width, float height, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((x + (x + width)) / 2, (y + (y + height)) / 2, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-(x + (x + width)) / 2, -(y + (y + height)) / 2, 0);
    }

    public static void stopScale() {
        GlStateManager.popMatrix();
    }
    
    public static void startTranslate(float x, float y) {
    	GlStateManager.pushMatrix();
    	GlStateManager.translate(x, y, 0);
    }
    
    public static void stopTranslate() {
    	GlStateManager.popMatrix();
    }

    public static void fixEnchantment() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
    
    public static void drawDot(final float x, final float y, final float size, final int color) {
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GuiUtils.setGlColor(color);
        GL11.glPointSize(size);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        GL11.glBegin(0);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawPartialCircle(final float x, final float y, final float radius, int startAngle, int endAngle, final float thickness, final int color, final boolean smooth) {
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        if (startAngle > endAngle) {
            final int temp = startAngle;
            startAngle = endAngle;
            endAngle = temp;
        }
        if (startAngle < 0) {
            startAngle = 0;
        }
        if (endAngle > 360) {
            endAngle = 360;
        }
        if (smooth) {
            GL11.glEnable(2848);
        }
        else {
            GL11.glDisable(2848);
        }
        GL11.glLineWidth(thickness);
        GuiUtils.setGlColor(color);
        GL11.glBegin(3);
        final float ratio = 0.017453292f;
        for (int i = startAngle; i <= endAngle; ++i) {
            final float radians = (i - 90) * ratio;
            GL11.glVertex2f(x + (float)Math.cos(radians) * radius, y + (float)Math.sin(radians) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public static void drawTorus(final int x, final int y, final float innerRadius, final float outerRadius, final int color, final boolean smooth) {
        if (smooth) {
            GL11.glEnable(2848);
        }
        else {
            GL11.glDisable(2848);
        }
        final float ratio = 0.017453292f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer bufferBuilder = tessellator.getWorldRenderer();
        bufferBuilder.begin(1, DefaultVertexFormats.POSITION);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        GuiUtils.setGlColor(color);
        for (int i = 0; i <= 360; ++i) {
            final float radians = (i - 90) * 0.017453292f;
            bufferBuilder.pos((double)(x + (float)Math.cos(radians) * innerRadius), (double)(y + (float)Math.sin(radians) * innerRadius), 0.0).endVertex();
            bufferBuilder.pos((double)(x + (float)Math.cos(radians) * outerRadius), (double)(y + (float)Math.sin(radians) * outerRadius), 0.0).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public void drawLine(final float x, final float x1, final float y, final float thickness, final int colour, final boolean smooth) {
        drawLines(new float[] { x, y, x1, y }, thickness, colour, smooth);
    }
    
    public void drawVerticalLine(final float x, final float y, final float y1, final float thickness, final int colour, final boolean smooth) {
        drawLines(new float[] { x, y, x, y1 }, thickness, colour, smooth);
    }
    
    public static void drawLines(final float[] points, final float thickness, final int color, final boolean smooth) {
        if (smooth) {
            GL11.glEnable(2848);
        }
        else {
            GL11.glDisable(2848);
        }
        GL11.glLineWidth(thickness);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer bufferBuilder = tessellator.getWorldRenderer();
        bufferBuilder.begin(1, DefaultVertexFormats.POSITION);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        GuiUtils.setGlColor(color);
        for (int i = 0; i < points.length; i += 2) {
            bufferBuilder.pos((double)points[i], (double)points[i + 1], 0.0).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawFilledShape(final float[] points, final int color, final boolean smooth) {
        if (smooth) {
            GL11.glEnable(2848);
        }
        else {
            GL11.glDisable(2848);
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer bufferBuilder = tessellator.getWorldRenderer();
        bufferBuilder.begin(9, DefaultVertexFormats.POSITION);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        GuiUtils.setGlColor(color);
        for (int i = 0; i < points.length; i += 2) {
            bufferBuilder.pos((double)points[i], (double)points[i + 1], 0.0).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawFilledRectangle(final float x1, final float y1, final float x2, final float y2, final int color, final boolean smooth) {
        final float[] points = { x1, y1, x1, y2, x2, y2, x2, y1 };
        drawFilledShape(points, color, smooth);
    }
}