package com.craftworks.pearclient.util;

import net.minecraft.client.renderer.vertex.*;
import java.awt.*;
import net.minecraft.client.renderer.*;

public class GuiUtils extends GLUtils
{
    public static final GuiUtils INSTANCE;
    
    public static void drawModalRectWithCustomSizedTexture(final float x, final float y, final float u, final float v, final int width, final int height, final float textureWidth, final float textureHeight) {
        final float f = 1.0f / textureWidth;
        final float f2 = 1.0f / textureHeight;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)x, (double)(y + height), 0.0).tex((double)(u * f), (double)((v + height) * f2)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), 0.0).tex((double)((u + width) * f), (double)((v + height) * f2)).endVertex();
        worldrenderer.pos((double)(x + width), (double)y, 0.0).tex((double)((u + width) * f), (double)(v * f2)).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0).tex((double)(u * f), (double)(v * f2)).endVertex();
        tessellator.draw();
    }
    
    public static void drawScaledCustomSizeModalRect(final float x, final float y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight) {
        final float f = 1.0f / tileWidth;
        final float f2 = 1.0f / tileHeight;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)x, (double)(y + height), 0.0).tex((double)(u * f), (double)((v + vHeight) * f2)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), 0.0).tex((double)((u + uWidth) * f), (double)((v + vHeight) * f2)).endVertex();
        worldrenderer.pos((double)(x + width), (double)y, 0.0).tex((double)((u + uWidth) * f), (double)(v * f2)).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0).tex((double)(u * f), (double)(v * f2)).endVertex();
        tessellator.draw();
    }
    
    public static int glToRGB(final float red, final float green, final float blue, final float alpha) {
        return new Color((int)red * 255, (int)green * 255, (int)blue * 255, (int)alpha * 255).getRGB();
    }
    
    public static float rgbToGl(final int rgb) {
        return rgb / 255.0f;
    }
    
    public static void setGlColor(final int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }
    
    public static void setGlColor(final int color, final float alpha) {
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }
    
    public static int getRGB(final int color, final int alpha) {
        return new Color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, alpha).getRGB();
    }
    
    public static Color getColor(final int color) {
        return new Color(color, true);
    }
    
    public static int getAlpha(final int color) {
        return color >> 24 & 0xFF;
    }
    
    public static int hsvToRgb(int hue, final int saturation, final int value) {
        hue %= 360;
        final float s = saturation / 100.0f;
        final float v = value / 100.0f;
        final float c = v * s;
        final float h = hue / 60.0f;
        final float x = c * (1.0f - Math.abs(h % 2.0f - 1.0f));
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        switch (hue / 60) {
            case 0: {
                r = c;
                g = x;
                b = 0.0f;
                break;
            }
            case 1: {
                r = x;
                g = c;
                b = 0.0f;
                break;
            }
            case 2: {
                r = 0.0f;
                g = c;
                b = x;
                break;
            }
            case 3: {
                r = 0.0f;
                g = x;
                b = c;
                break;
            }
            case 4: {
                r = x;
                g = 0.0f;
                b = c;
                break;
            }
            case 5: {
                r = c;
                g = 0.0f;
                b = x;
                break;
            }
            default: {
                return 0;
            }
        }
        final float m = v - c;
        return (int)((r + m) * 255.0f) << 16 | (int)((g + m) * 255.0f) << 8 | (int)((b + m) * 255.0f);
    }
    
    public static int[] rgbToHsv(final int rgb) {
        final float r = ((rgb & 0xFF0000) >> 16) / 255.0f;
        final float g = ((rgb & 0xFF00) >> 8) / 255.0f;
        final float b = (rgb & 0xFF) / 255.0f;
        final float M = (r > g) ? Math.max(r, b) : Math.max(g, b);
        final float m = (r < g) ? Math.min(r, b) : Math.min(g, b);
        final float c = M - m;
        float h;
        if (M == r) {
            for (h = (g - b) / c; h < 0.0f; h += 6.0f) {}
            h %= 6.0f;
        }
        else if (M == g) {
            h = (b - r) / c + 2.0f;
        }
        else {
            h = (r - g) / c + 4.0f;
        }
        h *= 60.0f;
        final float s = c / M;
        return new int[] { (c == 0.0f) ? -1 : ((int)h), (int)(s * 100.0f), (int)(M * 100.0f) };
    }
    
    public static int getIntermediateColor(final int a, final int b, final float percent) {
        final float avgRed = (a >> 16 & 0xFF) * percent + (b >> 16 & 0xFF) * (1.0f - percent);
        final float avgGreen = (a >> 8 & 0xFF) * percent + (b >> 8 & 0xFF) * (1.0f - percent);
        final float avgBlue = (a >> 0 & 0xFF) * percent + (b >> 0 & 0xFF) * (1.0f - percent);
        final float avgAlpha = (a >> 24 & 0xFF) * percent + (b >> 24 & 0xFF) * (1.0f - percent);
        try {
            return new Color(avgRed / 255.0f, avgGreen / 255.0f, avgBlue / 255.0f, avgAlpha / 255.0f).getRGB();
        }
        catch (IllegalArgumentException e) {
            return Integer.MIN_VALUE;
        }
    }
    
    public static int convertPercentToValue(final float percent) {
        return (int)(percent * 255.0f);
    }
    
    static {
        INSTANCE = new GuiUtils();
    }
}