/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.api.util.render;

import java.awt.Color;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.Util;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.impl.module.visual.Accent;

public class ColorUtil
extends Util {
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    public static Color interpolate(Color from, Color to, double value) {
        double progress = value > 1.0 ? 1.0 : (value < 0.0 ? 0.0 : value);
        int redDiff = to.getRed() - from.getRed();
        int greenDiff = to.getGreen() - from.getGreen();
        int blueDiff = to.getBlue() - from.getBlue();
        int alphaDiff = to.getAlpha() - from.getAlpha();
        int newRed = (int)((double)from.getRed() + (double)redDiff * progress);
        int newGreen = (int)((double)from.getGreen() + (double)greenDiff * progress);
        int newBlue = (int)((double)from.getBlue() + (double)blueDiff * progress);
        int newAlpha = (int)((double)from.getAlpha() + (double)alphaDiff * progress);
        return new Color(newRed, newGreen, newBlue, newAlpha);
    }

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)MathUtils.clamp(0.0, 255.0, alpha));
    }

    public static void glColor(int color) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int a = color >> 24 & 0xFF;
        GL11.glColor4f((float)((float)r / 255.0f), (float)((float)g / 255.0f), (float)((float)b / 255.0f), (float)((float)a / 255.0f));
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d((double)red, (double)green, (double)blue, (double)alpha);
    }

    public static void color(Color color) {
        if (color == null) {
            color = Color.white;
        }
        ColorUtil.color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public static int astolfoColors(int yOffset, int yTotal) {
        float hue;
        float speed = 2900.0f;
        for (hue = (float)(System.currentTimeMillis() % (long)((int)speed)) + (float)((yTotal - yOffset) * 9); hue > speed; hue -= speed) {
        }
        if ((double)(hue /= speed) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.HSBtoRGB(hue += 0.5f, 0.5f, 1.0f);
    }

    public static Color astolfoColorsC(int yOffset, int yTotal) {
        float hue;
        float speed = 2900.0f;
        for (hue = (float)(System.currentTimeMillis() % (long)((int)speed)) + (float)((yTotal - yOffset) * 9); hue > speed; hue -= speed) {
        }
        if ((double)(hue /= speed) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return new Color(Color.HSBtoRGB(hue += 0.5f, 0.5f, 1.0f));
    }

    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), 0.8f, 0.7f).getRGB();
    }

    public static Color rainbow(long delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), 0.8f, 0.7f);
    }

    public static Color exhibition(int delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), 0.6f, 1.0f);
    }

    public static Color exhibition(long delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), 0.6f, 1.0f);
    }

    public static int darker(int color, float factor) {
        int r = (int)((float)(color >> 16 & 0xFF) * factor);
        int g = (int)((float)(color >> 8 & 0xFF) * factor);
        int b = (int)((float)(color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF | (a & 0xFF) << 24;
    }

    public static int fadeBetween(int startColor, int endColor, float progress) {
        if (progress > 1.0f) {
            progress = 1.0f - progress % 1.0f;
        }
        return ColorUtil.fadeTo(startColor, endColor, progress);
    }

    public static Color fadeBetween(int speed, int index, Color start, Color end) {
        int tick = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        tick = (tick >= 180 ? 360 - tick : tick) * 2;
        return ColorUtil.interpolate(start, end, (float)tick / 360.0f);
    }

    public static int fadeTo(int startColor, int endColor, float progress) {
        float invert = 1.0f - progress;
        int r = (int)((float)(startColor >> 16 & 0xFF) * invert + (float)(endColor >> 16 & 0xFF) * progress);
        int g = (int)((float)(startColor >> 8 & 0xFF) * invert + (float)(endColor >> 8 & 0xFF) * progress);
        int b = (int)((float)(startColor & 0xFF) * invert + (float)(endColor & 0xFF) * progress);
        int a = (int)((float)(startColor >> 24 & 0xFF) * invert + (float)(endColor >> 24 & 0xFF) * progress);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static Color fadeTo(Color startColor, Color endColor, float progress) {
        float invert = 1.0f - progress;
        int r = (int)((float)(startColor.getRGB() >> 16 & 0xFF) * invert + (float)(endColor.getRGB() >> 16 & 0xFF) * progress);
        int g = (int)((float)(startColor.getRGB() >> 8 & 0xFF) * invert + (float)(endColor.getRGB() >> 8 & 0xFF) * progress);
        int b = (int)((float)(startColor.getRGB() & 0xFF) * invert + (float)(endColor.getRGB() & 0xFF) * progress);
        int a = (int)((float)(startColor.getRGB() >> 24 & 0xFF) * invert + (float)(endColor.getRGB() >> 24 & 0xFF) * progress);
        return new Color(r, g, b, a);
    }

    public static Color[] getClientAccentTheme() {
        Accent.EnumAccents enumeration = Wrapper.getModule(Accent.class).accents.getValue();
        Color customColor1 = Wrapper.getModule(Accent.class).customColor1.getValue();
        Color customColor2 = Wrapper.getModule(Accent.class).customColor2.getValue();
        Color[] clrs = enumeration.getClrs();
        switch (enumeration) {
            case FADE: {
                return new Color[]{customColor1, customColor2};
            }
            case FADE_STATIC: {
                return new Color[]{customColor1, customColor1.darker().darker().darker().darker()};
            }
            case RAINBOW: {
                return new Color[]{ColorUtil.rainbow(0L), ColorUtil.rainbow(500L), ColorUtil.rainbow(1000L), ColorUtil.rainbow(1500L)};
            }
            case EXHIBITION: {
                return new Color[]{ColorUtil.exhibition(0L), ColorUtil.exhibition(500L), ColorUtil.exhibition(1000L), ColorUtil.exhibition(1500L)};
            }
            case STATIC: {
                return new Color[]{customColor1, customColor1};
            }
            case COTTON_CANDY: {
                return new Color[]{new Color(91, 206, 250), new Color(245, 169, 184)};
            }
            case ASTOLFO: {
                return new Color[]{ColorUtil.astolfoColorsC(0, 100), ColorUtil.astolfoColorsC(0, 100)};
            }
        }
        return clrs;
    }

    public static Color[] getClientAccentTheme(int yOffset, int yTotal) {
        Accent.EnumAccents enumeration = Wrapper.getModule(Accent.class).accents.getValue();
        Color customColor1 = Wrapper.getModule(Accent.class).customColor1.getValue();
        Color customColor2 = Wrapper.getModule(Accent.class).customColor2.getValue();
        Color[] clrs = enumeration.getClrs();
        switch (enumeration) {
            case FADE: {
                return new Color[]{ColorUtil.fadeBetween(5, (int)((System.currentTimeMillis() + (long)(yTotal * 3)) % 1500L / 750L), customColor1, customColor2)};
            }
            case FADE_STATIC: {
                return new Color[]{ColorUtil.fadeBetween(5, (int)((System.currentTimeMillis() + (long)(yTotal * 3)) % 1500L / 750L), customColor1, customColor1.darker().darker().darker().darker())};
            }
            case RAINBOW: {
                return new Color[]{ColorUtil.rainbow((long)yTotal * 5L)};
            }
            case EXHIBITION: {
                return new Color[]{ColorUtil.exhibition((long)yTotal * 5L)};
            }
            case STATIC: {
                return new Color[]{customColor1, customColor1};
            }
            case COTTON_CANDY: {
                return new Color[]{ColorUtil.fadeBetween(5, (int)((System.currentTimeMillis() + (long)(yTotal * 3)) % 1500L / 750L), new Color(91, 206, 250), new Color(245, 169, 184))};
            }
            case ASTOLFO: {
                return new Color[]{ColorUtil.astolfoColorsC(yOffset, yTotal)};
            }
            case MONSOON_NEW: 
            case MONSOON_OLD: {
                return new Color[]{ColorUtil.fadeBetween(5, (int)((System.currentTimeMillis() + (long)(yTotal * 3)) % 1500L / 750L), clrs[0], clrs[1])};
            }
        }
        return clrs;
    }

    public static Color[] getAccent() {
        Color[] colorArray = new Color[4];
        colorArray[0] = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[0] : ColorUtil.fadeBetween(10, 270, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        Color color = colorArray[1] = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[1] : ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        Color color2 = ColorUtil.getClientAccentTheme().length > 3 ? (ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0]) : (colorArray[2] = ColorUtil.fadeBetween(10, 180, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
        colorArray[3] = ColorUtil.getClientAccentTheme().length > 3 ? (ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1]) : ColorUtil.fadeBetween(10, 90, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        return colorArray;
    }

    public static Color[] getAccent(float darken) {
        Color[] colours = ColorUtil.getAccent();
        for (int i = 0; i < 3; ++i) {
            colours[i] = new Color(ColorUtil.darker(colours[i].getRGB(), darken));
        }
        return colours;
    }

    public static Color integrateAlpha(Color colour, float alpha) {
        return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), MathHelper.clamp_int((int)alpha, 0, 255));
    }
}

