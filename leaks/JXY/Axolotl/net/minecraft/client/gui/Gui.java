package net.minecraft.client.gui;

import font.CFontRenderer;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import static org.lwjgl.opengl.GL11.*;

public class Gui
{
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    protected float zLevel;
    private static final String __OBFID = "CL_00000662";

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

    public static void drawImage(Minecraft mc, int x,int y,int size, ResourceLocation rec) {
        GlStateManager.pushMatrix();
    	GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(rec);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
    	//GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
    }
    public static void drawImage(Minecraft mc, double x,double y,double size, ResourceLocation rec) {
        GlStateManager.pushMatrix();
    	GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(rec);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, (int)size, (int) size, (int) size, (int) size);
    	//GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
    }
    public static void drawImage(Minecraft mc, double x,double y,double size,double size2, ResourceLocation rec) {
        GlStateManager.pushMatrix();
    	GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(rec);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, (int)size, (int)size2, (int)size, (int)size2);
        GlStateManager.color(0.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
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
    public static void drawRect(double left, double d, double right, double bottom, int color)
    {
        double var5;

        if (left < right)
        {
            var5 = left;
            left = right;
            right = var5;
        }

        if (d < bottom)
        {
            var5 = d;
            d = bottom;
            bottom = var5;
        }

        float var11 = (float)(color >> 24 & 255) / 255.0F;
        float var6 = (float)(color >> 16 & 255) / 255.0F;
        float var7 = (float)(color >> 8 & 255) / 255.0F;
        float var8 = (float)(color & 255) / 255.0F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.func_179090_x();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        var10.startDrawingQuads();
        var10.addVertex(left, bottom, 0.0D);
        var10.addVertex(right, bottom, 0.0D);
        var10.addVertex(right, d, 0.0D);
        var10.addVertex(left, d, 0.0D);
        var9.draw();
        GlStateManager.func_179098_w();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float var7 = (float)(startColor >> 24 & 255) / 255.0F;
        float var8 = (float)(startColor >> 16 & 255) / 255.0F;
        float var9 = (float)(startColor >> 8 & 255) / 255.0F;
        float var10 = (float)(startColor & 255) / 255.0F;
        float var11 = (float)(endColor >> 24 & 255) / 255.0F;
        float var12 = (float)(endColor >> 16 & 255) / 255.0F;
        float var13 = (float)(endColor >> 8 & 255) / 255.0F;
        float var14 = (float)(endColor & 255) / 255.0F;
        GlStateManager.func_179090_x();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator var15 = Tessellator.getInstance();
        WorldRenderer var16 = var15.getWorldRenderer();
        var16.startDrawingQuads();
        var16.func_178960_a(var8, var9, var10, var7);
        var16.addVertex(right, top, this.zLevel);
        var16.addVertex(left, top, this.zLevel);
        var16.func_178960_a(var12, var13, var14, var11);
        var16.addVertex(left, bottom, this.zLevel);
        var16.addVertex(right, bottom, this.zLevel);
        var15.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.func_179098_w();
    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    public void drawCenteredString(FontRenderer fontRendererIn, String text, float x, float y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    public void drawCenteredStringCF(CFontRenderer fontRendererIn, String text, float x, float y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
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
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        var10.startDrawingQuads();
        var10.addVertexWithUV(x + 0, y + height, this.zLevel, (float)(textureX + 0) * var7, (float)(textureY + height) * var8);
        var10.addVertexWithUV(x + width, y + height, this.zLevel, (float)(textureX + width) * var7, (float)(textureY + height) * var8);
        var10.addVertexWithUV(x + width, y + 0, this.zLevel, (float)(textureX + width) * var7, (float)(textureY + 0) * var8);
        var10.addVertexWithUV(x + 0, y + 0, this.zLevel, (float)(textureX + 0) * var7, (float)(textureY + 0) * var8);
        var9.draw();
    }

    public void func_175174_a(float p_175174_1_, float p_175174_2_, int p_175174_3_, int p_175174_4_, int p_175174_5_, int p_175174_6_)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        var10.startDrawingQuads();
        var10.addVertexWithUV(p_175174_1_ + 0.0F, p_175174_2_ + (float)p_175174_6_, this.zLevel, (float)(p_175174_3_ + 0) * var7, (float)(p_175174_4_ + p_175174_6_) * var8);
        var10.addVertexWithUV(p_175174_1_ + (float)p_175174_5_, p_175174_2_ + (float)p_175174_6_, this.zLevel, (float)(p_175174_3_ + p_175174_5_) * var7, (float)(p_175174_4_ + p_175174_6_) * var8);
        var10.addVertexWithUV(p_175174_1_ + (float)p_175174_5_, p_175174_2_ + 0.0F, this.zLevel, (float)(p_175174_3_ + p_175174_5_) * var7, (float)(p_175174_4_ + 0) * var8);
        var10.addVertexWithUV(p_175174_1_ + 0.0F, p_175174_2_ + 0.0F, this.zLevel, (float)(p_175174_3_ + 0) * var7, (float)(p_175174_4_ + 0) * var8);
        var9.draw();
    }

    public void func_175175_a(int p_175175_1_, int p_175175_2_, TextureAtlasSprite p_175175_3_, int p_175175_4_, int p_175175_5_)
    {
        Tessellator var6 = Tessellator.getInstance();
        WorldRenderer var7 = var6.getWorldRenderer();
        var7.startDrawingQuads();
        var7.addVertexWithUV(p_175175_1_ + 0, p_175175_2_ + p_175175_5_, this.zLevel, p_175175_3_.getMinU(), p_175175_3_.getMaxV());
        var7.addVertexWithUV(p_175175_1_ + p_175175_4_, p_175175_2_ + p_175175_5_, this.zLevel, p_175175_3_.getMaxU(), p_175175_3_.getMaxV());
        var7.addVertexWithUV(p_175175_1_ + p_175175_4_, p_175175_2_ + 0, this.zLevel, p_175175_3_.getMaxU(), p_175175_3_.getMinV());
        var7.addVertexWithUV(p_175175_1_ + 0, p_175175_2_ + 0, this.zLevel, p_175175_3_.getMinU(), p_175175_3_.getMinV());
        var6.draw();
    }

    /**
     * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height, textureWidth, textureHeight
     */
    public static void drawModalRectWithCustomSizedTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float var8 = 1.0F / textureWidth;
        float var9 = 1.0F / textureHeight;
        Tessellator var10 = Tessellator.getInstance();
        WorldRenderer var11 = var10.getWorldRenderer();
        var11.startDrawingQuads();
        var11.addVertexWithUV(x, y + height, 0.0D, u * var8, (v + (float)height) * var9);
        var11.addVertexWithUV(x + width, y + height, 0.0D, (u + (float)width) * var8, (v + (float)height) * var9);
        var11.addVertexWithUV(x + width, y, 0.0D, (u + (float)width) * var8, v * var9);
        var11.addVertexWithUV(x, y, 0.0D, u * var8, v * var9);
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
        var13.addVertexWithUV(x, y + height, 0.0D, u * var10, (v + (float)vHeight) * var11);
        var13.addVertexWithUV(x + width, y + height, 0.0D, (u + (float)uWidth) * var10, (v + (float)vHeight) * var11);
        var13.addVertexWithUV(x + width, y, 0.0D, (u + (float)uWidth) * var10, v * var11);
        var13.addVertexWithUV(x, y, 0.0D, u * var10, v * var11);
        var12.draw();
    }
}
