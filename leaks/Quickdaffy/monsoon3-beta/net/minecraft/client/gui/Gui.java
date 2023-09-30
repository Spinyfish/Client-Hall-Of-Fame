/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import net.minecraft.client.gui.FontRenderer;
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
    protected float zLevel;

    public static void drawHorizontalLine(double startX, double endX, double y, int color) {
        if (endX < startX) {
            double i = startX;
            startX = endX;
            endX = i;
        }
        Gui.drawRect(startX, y, endX + 1.0, y + 1.0, color);
    }

    public static void drawVerticalLine(double x, double startY, double endY, int color) {
        if (endY < startY) {
            double i = startY;
            startY = endY;
            endY = i;
        }
        Gui.drawRect(x, startY + 1.0, x + 1.0, endY, color);
    }

    public static void drawHorizontalLineDefineWidth(double startX, double endX, double y, double width, int color) {
        if (endX < startX) {
            double i = startX;
            startX = endX;
            endX = i;
        }
        Gui.drawRect(startX, y, endX + width, y + width, color);
    }

    public static void drawVerticalLineDefineWidth(double x, double startY, double endY, double width, int color) {
        if (endY < startY) {
            double i = startY;
            startY = endY;
            endY = i;
        }
        Gui.drawRect(x, startY + width, x + width, endY, color);
    }

    public static void drawHorizontalLine(double startX, double endX, double y, int color, double size) {
        if (endX < startX) {
            double i = startX;
            startX = endX;
            endX = i;
        }
        Gui.drawRect(startX, y, endX + size, y + size, color);
    }

    public static void drawVerticalLine(double x, double startY, double endY, int color, double size) {
        if (endY < startY) {
            double i = startY;
            startY = endY;
            endY = i;
        }
        Gui.drawRect(x, startY + size, x + size, endY, color);
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.pos(left, bottom, 0.0).end();
        worldrenderer.pos(right, bottom, 0.0).end();
        worldrenderer.pos(right, top, 0.0).end();
        worldrenderer.pos(left, top, 0.0).end();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, this.zLevel).func_181666_a(f1, f2, f3, f).end();
        worldrenderer.pos(left, top, this.zLevel).func_181666_a(f1, f2, f3, f).end();
        worldrenderer.pos(left, bottom, this.zLevel).func_181666_a(f5, f6, f7, f4).end();
        worldrenderer.pos(right, bottom, this.zLevel).func_181666_a(f5, f6, f7, f4).end();
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
        float f = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(x + 0, y + height, this.zLevel).func_181673_a((float)(textureX + 0) * f, (float)(textureY + height) * f1).end();
        worldrenderer.pos(x + width, y + height, this.zLevel).func_181673_a((float)(textureX + width) * f, (float)(textureY + height) * f1).end();
        worldrenderer.pos(x + width, y + 0, this.zLevel).func_181673_a((float)(textureX + width) * f, (float)(textureY + 0) * f1).end();
        worldrenderer.pos(x + 0, y + 0, this.zLevel).func_181673_a((float)(textureX + 0) * f, (float)(textureY + 0) * f1).end();
        tessellator.draw();
    }

    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
        float f = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(xCoord + 0.0f, yCoord + (float)maxV, this.zLevel).func_181673_a((float)(minU + 0) * f, (float)(minV + maxV) * f1).end();
        worldrenderer.pos(xCoord + (float)maxU, yCoord + (float)maxV, this.zLevel).func_181673_a((float)(minU + maxU) * f, (float)(minV + maxV) * f1).end();
        worldrenderer.pos(xCoord + (float)maxU, yCoord + 0.0f, this.zLevel).func_181673_a((float)(minU + maxU) * f, (float)(minV + 0) * f1).end();
        worldrenderer.pos(xCoord + 0.0f, yCoord + 0.0f, this.zLevel).func_181673_a((float)(minU + 0) * f, (float)(minV + 0) * f1).end();
        tessellator.draw();
    }

    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(xCoord + 0, yCoord + heightIn, this.zLevel).func_181673_a(textureSprite.getMinU(), textureSprite.getMaxV()).end();
        worldrenderer.pos(xCoord + widthIn, yCoord + heightIn, this.zLevel).func_181673_a(textureSprite.getMaxU(), textureSprite.getMaxV()).end();
        worldrenderer.pos(xCoord + widthIn, yCoord + 0, this.zLevel).func_181673_a(textureSprite.getMaxU(), textureSprite.getMinV()).end();
        worldrenderer.pos(xCoord + 0, yCoord + 0, this.zLevel).func_181673_a(textureSprite.getMinU(), textureSprite.getMinV()).end();
        tessellator.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0f / textureWidth;
        float f1 = 1.0f / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(x, y + height, 0.0).func_181673_a(u * f, (v + height) * f1).end();
        worldrenderer.pos(x + width, y + height, 0.0).func_181673_a((u + width) * f, (v + height) * f1).end();
        worldrenderer.pos(x + width, y, 0.0).func_181673_a((u + width) * f, v * f1).end();
        worldrenderer.pos(x, y, 0.0).func_181673_a(u * f, v * f1).end();
        tessellator.draw();
    }

    public static void drawScaledCustomSizeModalRect(float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight) {
        float f = 1.0f / tileWidth;
        float f1 = 1.0f / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(x, y + height, 0.0).func_181673_a(u * f, (v + vHeight) * f1).end();
        worldrenderer.pos(x + width, y + height, 0.0).func_181673_a((u + uWidth) * f, (v + vHeight) * f1).end();
        worldrenderer.pos(x + width, y, 0.0).func_181673_a((u + uWidth) * f, v * f1).end();
        worldrenderer.pos(x, y, 0.0).func_181673_a(u * f, v * f1).end();
        tessellator.draw();
    }
}

