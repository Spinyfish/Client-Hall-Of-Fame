package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui
{
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    protected float zLevel;

    /**
     * Draw a 1 pixel wide horizontal line. Args: x1, x2, y, color
     */
    public static void drawHorizontalLine(float x, float w, float y, int color) // were integers, protected and not static
    {
        if (w < x)
        {
            float i = x;
            x = w;
            w = i;
        }

        drawRect(x, y, w + 1, y + 1, color);
    }

    /**
     * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
     */
    public static void drawVerticalLine(float x, float startY, float endY, int color) // were integers, protected and not static
    {
        if (endY < startY)
        {
        	float i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void drawRect(double left, double top, double right, double d, int color) // arguments were integers
    {
        if (left < right)
        {
        	double i = left;
            left = right;
            right = i;
        }

        if (top < d)
        {
            double j = top;
            top = d;
            d = j;
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
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.func_181662_b((double)left, (double)d, 0.0D).func_181675_d();
        worldrenderer.func_181662_b((double)right, (double)d, 0.0D).func_181675_d();
        worldrenderer.func_181662_b((double)right, (double)top, 0.0D).func_181675_d();
        worldrenderer.func_181662_b((double)left, (double)top, 0.0D).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
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
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        worldrenderer.func_181662_b((double)right, (double)top, (double)this.zLevel).func_181666_a(f1, f2, f3, f).func_181675_d();
        worldrenderer.func_181662_b((double)left, (double)top, (double)this.zLevel).func_181666_a(f1, f2, f3, f).func_181675_d();
        worldrenderer.func_181662_b((double)left, (double)bottom, (double)this.zLevel).func_181666_a(f5, f6, f7, f4).func_181675_d();
        worldrenderer.func_181662_b((double)right, (double)bottom, (double)this.zLevel).func_181666_a(f5, f6, f7, f4).func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }

    /**
     * Renders the specified text to the screen. Args : renderer, string, x, y, color
     */
    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)x, (float)y, color);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.func_181662_b((double)(x + 0), (double)(y + height), (double)this.zLevel).func_181673_a((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(x + width), (double)(y + height), (double)this.zLevel).func_181673_a((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(x + width), (double)(y + 0), (double)this.zLevel).func_181673_a((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(x + 0), (double)(y + 0), (double)this.zLevel).func_181673_a((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).func_181675_d();
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle using the texture currently bound to the TextureManager
     */
    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.func_181662_b((double)(xCoord + 0.0F), (double)(yCoord + (float)maxV), (double)this.zLevel).func_181673_a((double)((float)(minU + 0) * f), (double)((float)(minV + maxV) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(xCoord + (float)maxU), (double)(yCoord + (float)maxV), (double)this.zLevel).func_181673_a((double)((float)(minU + maxU) * f), (double)((float)(minV + maxV) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(xCoord + (float)maxU), (double)(yCoord + 0.0F), (double)this.zLevel).func_181673_a((double)((float)(minU + maxU) * f), (double)((float)(minV + 0) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(xCoord + 0.0F), (double)(yCoord + 0.0F), (double)this.zLevel).func_181673_a((double)((float)(minU + 0) * f), (double)((float)(minV + 0) * f1)).func_181675_d();
        tessellator.draw();
    }

    /**
     * Draws a texture rectangle using the texture currently bound to the TextureManager
     */
    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.func_181662_b((double)(xCoord + 0), (double)(yCoord + heightIn), (double)this.zLevel).func_181673_a((double)textureSprite.getMinU(), (double)textureSprite.getMaxV()).func_181675_d();
        worldrenderer.func_181662_b((double)(xCoord + widthIn), (double)(yCoord + heightIn), (double)this.zLevel).func_181673_a((double)textureSprite.getMaxU(), (double)textureSprite.getMaxV()).func_181675_d();
        worldrenderer.func_181662_b((double)(xCoord + widthIn), (double)(yCoord + 0), (double)this.zLevel).func_181673_a((double)textureSprite.getMaxU(), (double)textureSprite.getMinV()).func_181675_d();
        worldrenderer.func_181662_b((double)(xCoord + 0), (double)(yCoord + 0), (double)this.zLevel).func_181673_a((double)textureSprite.getMinU(), (double)textureSprite.getMinV()).func_181675_d();
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height, textureWidth, textureHeight
     */
    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.func_181662_b((double)x, (double)(y + height), 0.0D).func_181673_a((double)(u * f), (double)((v + (float)height) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(x + width), (double)(y + height), 0.0D).func_181673_a((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(x + width), (double)y, 0.0D).func_181673_a((double)((u + (float)width) * f), (double)(v * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)x, (double)y, 0.0D).func_181673_a((double)(u * f), (double)(v * f1)).func_181675_d();
        tessellator.draw();
    }

    /**
     * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used anywhere in vanilla code.
     */
    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight)
    {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.func_181662_b((double)x, (double)(y + height), 0.0D).func_181673_a((double)(u * f), (double)((v + (float)vHeight) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(x + width), (double)(y + height), 0.0D).func_181673_a((double)((u + (float)uWidth) * f), (double)((v + (float)vHeight) * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)(x + width), (double)y, 0.0D).func_181673_a((double)((u + (float)uWidth) * f), (double)(v * f1)).func_181675_d();
        worldrenderer.func_181662_b((double)x, (double)y, 0.0D).func_181673_a((double)(u * f), (double)(v * f1)).func_181675_d();
        tessellator.draw();
    }
}
