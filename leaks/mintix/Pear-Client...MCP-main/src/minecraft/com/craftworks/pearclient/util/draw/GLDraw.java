package com.craftworks.pearclient.util.draw;

import org.lwjgl.opengl.GL11;

import com.craftworks.pearclient.util.GuiUtils;
import com.craftworks.pearclient.util.math.MathUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GLDraw {
	
	public static void drawRect(float left, float top, float right, float bottom, final int color) {
        if (left < right) {
            final float i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            final float j = top;
            top = bottom;
            bottom = j;
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GuiUtils.setGlColor(color);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)left, (double)bottom, 0.0).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
	
	public static void drawRectOutline(final float left, final float top, final float right, final float bottom, final int color) {
        final float width = 0.55f;
        drawRect(left - width, top - width, right + width, top, color);
        drawRect(right, top, right + width, bottom, color);
        drawRect(left - width, bottom, right + width, bottom + width, color);
        drawRect(left - width, top, left, bottom, color);
    }
    
    public static void drawRoundedRect(final float nameInt1, final float nameInt2, final float nameInt3, final float nameInt4, final float radius, final int color) {
        final float f1 = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GlStateManager.color(f2, f3, f4, f1);
        drawRoundedRect(nameInt1, nameInt2, nameInt3, nameInt4, radius);
    }
    
    public static void drawRoundedRect(final float nameFloat1, final float nameFloat2, final float nameFloat3, final float nameFloat4, final float nameFloat5) {
        final int i = 18;
        final float f1 = 90.0f / i;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.enableColorMaterial();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glBegin(5);
        GL11.glVertex2f(nameFloat1 + nameFloat5, nameFloat2);
        GL11.glVertex2f(nameFloat1 + nameFloat5, nameFloat4);
        GL11.glVertex2f(nameFloat3 - nameFloat5, nameFloat2);
        GL11.glVertex2f(nameFloat3 - nameFloat5, nameFloat4);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(nameFloat1, nameFloat2 + nameFloat5);
        GL11.glVertex2f(nameFloat1 + nameFloat5, nameFloat2 + nameFloat5);
        GL11.glVertex2f(nameFloat1, nameFloat4 - nameFloat5);
        GL11.glVertex2f(nameFloat1 + nameFloat5, nameFloat4 - nameFloat5);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(nameFloat3, nameFloat2 + nameFloat5);
        GL11.glVertex2f(nameFloat3 - nameFloat5, nameFloat2 + nameFloat5);
        GL11.glVertex2f(nameFloat3, nameFloat4 - nameFloat5);
        GL11.glVertex2f(nameFloat3 - nameFloat5, nameFloat4 - nameFloat5);
        GL11.glEnd();
        GL11.glBegin(6);
        float f2 = nameFloat3 - nameFloat5;
        float f3 = nameFloat2 + nameFloat5;
        GL11.glVertex2f(f2, f3);
        for (int j = 0; j <= i; ++j) {
            final float f4 = j * f1;
            GL11.glVertex2f((float)(f2 + nameFloat5 * Math.cos(Math.toRadians(f4))), (float)(f3 - nameFloat5 * Math.sin(Math.toRadians(f4))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f2 = nameFloat1 + nameFloat5;
        f3 = nameFloat2 + nameFloat5;
        GL11.glVertex2f(f2, f3);
        for (int j = 0; j <= i; ++j) {
            final float f4 = j * f1;
            GL11.glVertex2f((float)(f2 - nameFloat5 * Math.cos(Math.toRadians(f4))), (float)(f3 - nameFloat5 * Math.sin(Math.toRadians(f4))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f2 = nameFloat1 + nameFloat5;
        f3 = nameFloat4 - nameFloat5;
        GL11.glVertex2f(f2, f3);
        for (int j = 0; j <= i; ++j) {
            final float f4 = j * f1;
            GL11.glVertex2f((float)(f2 - nameFloat5 * Math.cos(Math.toRadians(f4))), (float)(f3 + nameFloat5 * Math.sin(Math.toRadians(f4))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f2 = nameFloat3 - nameFloat5;
        f3 = nameFloat4 - nameFloat5;
        GL11.glVertex2f(f2, f3);
        for (int j = 0; j <= i; ++j) {
            final float f4 = j * f1;
            GL11.glVertex2f((float)(f2 + nameFloat5 * Math.cos(Math.toRadians(f4))), (float)(f3 + nameFloat5 * Math.sin(Math.toRadians(f4))));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
    
    public static void drawRoundedOutline(final int x, final int y, final int x2, final int y2, final float radius, final float width, final int color) {
        GuiUtils.setGlColor(color);
        drawRoundedOutline(x, y, x2, y2, radius, width);
    }
    
    public static void drawRoundedOutline(final float x, final float y, final float x2, final float y2, final float radius, final float width, final int color) {
        GuiUtils.setGlColor(color);
        drawRoundedOutline(x, y, x2, y2, radius, width);
    }
    
    public static void drawRoundedOutline(final float x, final float y, final float x2, final float y2, final float radius, final float width) {
        final int i = 18;
        final int j = 90 / i;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.enableColorMaterial();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        if (width != 1.0f) {
            GL11.glLineWidth(width);
        }
        GL11.glBegin(3);
        GL11.glVertex2f(x + radius, y);
        GL11.glVertex2f(x2 - radius, y);
        GL11.glEnd();
        GL11.glBegin(3);
        GL11.glVertex2f(x2, y + radius);
        GL11.glVertex2f(x2, y2 - radius);
        GL11.glEnd();
        GL11.glBegin(3);
        GL11.glVertex2f(x2 - radius, y2 - 0.1f);
        GL11.glVertex2f(x + radius, y2 - 0.1f);
        GL11.glEnd();
        GL11.glBegin(3);
        GL11.glVertex2f(x + 0.1f, y2 - radius);
        GL11.glVertex2f(x + 0.1f, y + radius);
        GL11.glEnd();
        float f1 = x2 - radius;
        float f2 = y + radius;
        GL11.glBegin(3);
        for (int k = 0; k <= i; ++k) {
            final int m = 90 - k * j;
            GL11.glVertex2f((float)(f1 + radius * MathUtil.getRightAngle(m)), (float)(f2 - radius * MathUtil.getAngle(m)));
        }
        GL11.glEnd();
        f1 = x2 - radius;
        f2 = y2 - radius;
        GL11.glBegin(3);
        for (int k = 0; k <= i; ++k) {
            final int m = k * j + 270;
            GL11.glVertex2f((float)(f1 + radius * MathUtil.getRightAngle(m)), (float)(f2 - radius * MathUtil.getAngle(m)));
        }
        GL11.glEnd();
        GL11.glBegin(3);
        f1 = x + radius;
        f2 = y2 - radius;
        for (int k = 0; k <= i; ++k) {
            final int m = k * j + 90;
            GL11.glVertex2f((float)(f1 + radius * MathUtil.getRightAngle(m)), (float)(f2 + radius * MathUtil.getAngle(m)));
        }
        GL11.glEnd();
        GL11.glBegin(3);
        f1 = x + radius;
        f2 = y + radius;
        for (int k = 0; k <= i; ++k) {
            final int m = 270 - k * j;
            GL11.glVertex2f((float)(f1 + radius * MathUtil.getRightAngle(m)), (float)(f2 + radius * MathUtil.getAngle(m)));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        if (width != 1.0f) {
            GL11.glLineWidth(1.0f);
        }
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture2D();
    }

}
