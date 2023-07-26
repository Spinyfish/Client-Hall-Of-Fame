package dev.hera.client.utils;

import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class ColorUtils {

    public static int getRainbow(double seconds, double saturation, float brightness) {
        float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000F);
        return Color.HSBtoRGB(hue, (float) saturation, brightness);
    }
    public static int getRainbow(double seconds, double saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (float)(seconds * 1000F);
        return Color.HSBtoRGB(hue, (float) saturation, brightness);
    }

    public static int setColorOpacity(int color, int alpha) {
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        return (new Color(red, green, blue, alpha / 255.0F)).getRGB();
    }

    public static void color(int color, float alpha) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void color(int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
    }
}