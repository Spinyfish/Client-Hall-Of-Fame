package net.minecraft.client.gui;

import crispy.fonts.decentfont.MinecraftFontRenderer;
import crispy.fonts.greatfont.TTFFontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class Gui
{
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    public static float zLevel;



    /**
     * Draw a 1 pixel wide horizontal line. Args: x1, x2, y, color
     */
    protected void drawHorizontalLine(int startX, int endX, int y, int color)
    {
        if (endX < startX)
        {
            int var5 = startX;
            startX = endX;
            endX = var5;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    /**
     * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
     */
    protected void drawVerticalLine(int x, int startY, int endY, int color)
    {
        if (endY < startY)
        {
            int var5 = startY;
            startY = endY;
            endY = var5;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }


    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int left, int top, int right, int bottom, int color)
    {
        int var5;

        if (left < right)
        {
            var5 = left;
            left = right;
            right = var5;
        }

        if (top < bottom)
        {
            var5 = top;
            top = bottom;
            bottom = var5;
        }

        float var11 = (float)(color >> 24 & 255) / 255.0F;
        float var6 = (float)(color >> 16 & 255) / 255.0F;
        float var7 = (float)(color >> 8 & 255) / 255.0F;
        float var8 = (float)(color & 255) / 255.0F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        var10.startDrawingQuads();
        var10.addVertex((double)left, (double)bottom, 0.0D);
        var10.addVertex((double)right, (double)bottom, 0.0D);
        var10.addVertex((double)right, (double)top, 0.0D);
        var10.addVertex((double)left, (double)top, 0.0D);
        var9.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void drawRect(double left, double top, double right, double bottom, int color)
    {
        double var5;

        if (left < right)
        {
            var5 = left;
            left = right;
            right = var5;
        }

        if (top < bottom)
        {
            var5 = top;
            top = bottom;
            bottom = var5;
        }

        float var11 = (float)(color >> 24 & 255) / 255.0F;
        float var6 = (float)(color >> 16 & 255) / 255.0F;
        float var7 = (float)(color >> 8 & 255) / 255.0F;
        float var8 = (float)(color & 255) / 255.0F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        var10.startDrawingQuads();
        var10.addVertex((double)left, (double)bottom, 0.0D);
        var10.addVertex((double)right, (double)bottom, 0.0D);
        var10.addVertex((double)right, (double)top, 0.0D);
        var10.addVertex((double)left, (double)top, 0.0D);
        var9.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float var7 = (float)(startColor >> 24 & 255) / 255.0F;
        float var8 = (float)(startColor >> 16 & 255) / 255.0F;
        float var9 = (float)(startColor >> 8 & 255) / 255.0F;
        float var10 = (float)(startColor & 255) / 255.0F;
        float var11 = (float)(endColor >> 24 & 255) / 255.0F;
        float var12 = (float)(endColor >> 16 & 255) / 255.0F;
        float var13 = (float)(endColor >> 8 & 255) / 255.0F;
        float var14 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator var15 = Tessellator.getInstance();
        WorldRenderer var16 = var15.getWorldRenderer();
        var16.startDrawingQuads();
        var16.setColorRGBA_F(var8, var9, var10, var7);
        var16.addVertex((double)right, (double)top, (double)zLevel);
        var16.addVertex((double)left, (double)top, (double)zLevel);
        var16.setColorRGBA_F(var12, var13, var14, var11);
        var16.addVertex((double)left, (double)bottom, (double)zLevel);
        var16.addVertex((double)right, (double)bottom, (double)zLevel);
        var15.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor)
    {
        float var7 = (float)(startColor >> 24 & 255) / 255.0F;
        float var8 = (float)(startColor >> 16 & 255) / 255.0F;
        float var9 = (float)(startColor >> 8 & 255) / 255.0F;
        float var10 = (float)(startColor & 255) / 255.0F;
        float var11 = (float)(endColor >> 24 & 255) / 255.0F;
        float var12 = (float)(endColor >> 16 & 255) / 255.0F;
        float var13 = (float)(endColor >> 8 & 255) / 255.0F;
        float var14 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator var15 = Tessellator.getInstance();
        WorldRenderer var16 = var15.getWorldRenderer();
        var16.startDrawingQuads();
        var16.setColorRGBA_F(var8, var9, var10, var7);
        var16.addVertex((double)right, (double)top, (double)zLevel);
        var16.addVertex((double)left, (double)top, (double)zLevel);
        var16.setColorRGBA_F(var12, var13, var14, var11);
        var16.addVertex((double)left, (double)bottom, (double)zLevel);
        var16.addVertex((double)right, (double)bottom, (double)zLevel);
        var15.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }
    public static void drawCenteredString(MinecraftFontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawString(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }

    public static void drawCenteredString(TTFFontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawString(text, (float)(x - fontRendererIn.getWidth(text) / 2), (float)y, color);
    }

    /**
     * Renders the specified text to the screen. Args : renderer, string, x, y, color
     */
    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)x, (float)y, color);
    }
    public void drawString(TTFFontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawString(text, (float)x, (float)y, color);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        var10.startDrawingQuads();
        var10.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel, (double)((float)(textureX + 0) * var7), (double)((float)(textureY + height) * var8));
        var10.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(textureX + width) * var7), (double)((float)(textureY + height) * var8));
        var10.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(textureX + width) * var7), (double)((float)(textureY + 0) * var8));
        var10.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(textureX + 0) * var7), (double)((float)(textureY + 0) * var8));
        var9.draw();
    }

    /**
     * Draws a textured rectangle using the texture currently bound to the TextureManager
     */
    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        var10.startDrawingQuads();
        var10.addVertexWithUV((double)(xCoord + 0.0F), (double)(yCoord + (float)maxV), (double)this.zLevel, (double)((float)(minU + 0) * var7), (double)((float)(minV + maxV) * var8));
        var10.addVertexWithUV((double)(xCoord + (float)maxU), (double)(yCoord + (float)maxV), (double)this.zLevel, (double)((float)(minU + maxU) * var7), (double)((float)(minV + maxV) * var8));
        var10.addVertexWithUV((double)(xCoord + (float)maxU), (double)(yCoord + 0.0F), (double)this.zLevel, (double)((float)(minU + maxU) * var7), (double)((float)(minV + 0) * var8));
        var10.addVertexWithUV((double)(xCoord + 0.0F), (double)(yCoord + 0.0F), (double)this.zLevel, (double)((float)(minU + 0) * var7), (double)((float)(minV + 0) * var8));
        var9.draw();
    }

    /**
     * Draws a texture rectangle using the texture currently bound to the TextureManager
     */
    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn)
    {
        Tessellator var6 = Tessellator.getInstance();
        WorldRenderer var7 = var6.getWorldRenderer();
        var7.startDrawingQuads();
        var7.addVertexWithUV((double)(xCoord + 0), (double)(yCoord + heightIn), (double)this.zLevel, (double)textureSprite.getMinU(), (double)textureSprite.getMaxV());
        var7.addVertexWithUV((double)(xCoord + widthIn), (double)(yCoord + heightIn), (double)this.zLevel, (double)textureSprite.getMaxU(), (double)textureSprite.getMaxV());
        var7.addVertexWithUV((double)(xCoord + widthIn), (double)(yCoord + 0), (double)this.zLevel, (double)textureSprite.getMaxU(), (double)textureSprite.getMinV());
        var7.addVertexWithUV((double)(xCoord + 0), (double)(yCoord + 0), (double)this.zLevel, (double)textureSprite.getMinU(), (double)textureSprite.getMinV());
        var6.draw();
    }

    /**
     * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height, textureWidth, textureHeight
     */
    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float var8 = 1.0F / textureWidth;
        float var9 = 1.0F / textureHeight;
        Tessellator var10 = Tessellator.getInstance();
        WorldRenderer var11 = var10.getWorldRenderer();
        var11.startDrawingQuads();
        var11.addVertexWithUV((double)x, (double)(y + height), 0.0D, (double)(u * var8), (double)((v + (float)height) * var9));
        var11.addVertexWithUV((double)(x + width), (double)(y + height), 0.0D, (double)((u + (float)width) * var8), (double)((v + (float)height) * var9));
        var11.addVertexWithUV((double)(x + width), (double)y, 0.0D, (double)((u + (float)width) * var8), (double)(v * var9));
        var11.addVertexWithUV((double)x, (double)y, 0.0D, (double)(u * var8), (double)(v * var9));
        var10.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float var8 = 1.0F / textureWidth;
        float var9 = 1.0F / textureHeight;
        Tessellator var10 = Tessellator.getInstance();
        WorldRenderer var11 = var10.getWorldRenderer();
        var11.startDrawingQuads();
        var11.addVertexWithUV((double)x, (double)(y + height), 0.0D, (double)(u * var8), (double)((v + (float)height) * var9));
        var11.addVertexWithUV((double)(x + width), (double)(y + height), 0.0D, (double)((u + (float)width) * var8), (double)((v + (float)height) * var9));
        var11.addVertexWithUV((double)(x + width), (double)y, 0.0D, (double)((u + (float)width) * var8), (double)(v * var9));
        var11.addVertexWithUV((double)x, (double)y, 0.0D, (double)(u * var8), (double)(v * var9));
        var10.draw();
    }

    /**
     * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used anywhere in vanilla code.
     */
    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight)
    {
        float var10 = 1.0F / tileWidth;
        float var11 = 1.0F / tileHeight;
        Tessellator var12 = Tessellator.getInstance();
        WorldRenderer var13 = var12.getWorldRenderer();
        var13.startDrawingQuads();
        var13.addVertexWithUV((double)x, (double)(y + height), 0.0D, (double)(u * var10), (double)((v + (float)vHeight) * var11));
        var13.addVertexWithUV((double)(x + width), (double)(y + height), 0.0D, (double)((u + (float)uWidth) * var10), (double)((v + (float)vHeight) * var11));
        var13.addVertexWithUV((double)(x + width), (double)y, 0.0D, (double)((u + (float)uWidth) * var10), (double)(v * var11));
        var13.addVertexWithUV((double)x, (double)y, 0.0D, (double)(u * var10), (double)(v * var11));
        var12.draw();
    }
}
