/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  vip.astroline.client.layout.hud.HUD$RainbowDirection
 *  vip.astroline.client.service.module.impl.render.Hud
 */
package vip.astroline.client.storage.utils.render;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import vip.astroline.client.layout.hud.HUD;
import vip.astroline.client.service.module.impl.render.Hud;

public enum ColorUtils {
    BLACK(-16711423),
    BLUE(-12028161),
    DARKBLUE(-12621684),
    GREEN(-9830551),
    DARKGREEN(-9320847),
    WHITE(-65794),
    AQUA(-7820064),
    DARKAQUA(-12621684),
    GREY(-9868951),
    DARKGREY(-14342875),
    RED(-65536),
    DARKRED(-8388608),
    ORANGE(-29696),
    DARKORANGE(-2263808),
    YELLOW(-256),
    DARKYELLOW(-2702025),
    MAGENTA(-18751),
    DARKMAGENTA(-2252579);

    public int c;

    private ColorUtils(int co) {
        this.c = co;
    }

    public static long drawGradientRect(int left, int top, int right, int bottom, int size) {
        int size2 = 1;
        long topCol = ColorUtils.renderGradientRect(left, top - size2, right, top, 2.0, 10L, 0L, HUD.RainbowDirection.RIGHT);
        long downCol = ColorUtils.renderGradientRect(left - size2, top - size, left, bottom + size2, 2.0, 10L, 0L, HUD.RainbowDirection.DOWN);
        ColorUtils.renderGradientRect(right, top - size2, right + size2, bottom + size2, 2.0, 10L, topCol, HUD.RainbowDirection.DOWN);
        return topCol;
    }

    public static long renderGradientRect(int left, int top, int right, int bottom, double time, long difference, long delay, HUD.RainbowDirection rainbowDirection) {
        int i;
        long endDelay = 0L;
        if (rainbowDirection == HUD.RainbowDirection.RIGHT) {
            for (i = 0; i < right - left; ++i) {
                Gui.drawRect((int)(left + i), (int)top, (int)right, (int)bottom, (int)ColorUtils.interpolateColorsBackAndForth(Hud.arrayListSpeed.getValue().intValue(), i, Hud.hudColor1.getColor(), Hud.hudColor2.getColor(), false).getRGB());
            }
        }
        if (rainbowDirection == HUD.RainbowDirection.LEFT) {
            for (i = 0; i < right - left; ++i) {
                Gui.drawRect((int)(left + i), (int)top, (int)right, (int)bottom, (int)ColorUtils.interpolateColorsBackAndForth(Hud.arrayListSpeed.getValue().intValue(), i, Hud.hudColor1.getColor(), Hud.hudColor2.getColor(), false).getRGB());
            }
        }
        if (rainbowDirection == null) {
            for (i = 0; i < bottom - top; ++i) {
                Gui.drawRect((int)left, (int)(top + i), (int)right, (int)bottom, (int)ColorUtils.interpolateColorsBackAndForth(Hud.arrayListSpeed.getValue().intValue(), i, Hud.hudColor1.getColor(), Hud.hudColor2.getColor(), false).getRGB());
            }
        }
        if (rainbowDirection != HUD.RainbowDirection.UP) return endDelay;
        i = 0;
        while (i < bottom - top) {
            Gui.drawRect((int)left, (int)(top + i), (int)right, (int)bottom, (int)ColorUtils.interpolateColorsBackAndForth(Hud.arrayListSpeed.getValue().intValue(), i, Hud.hudColor1.getColor(), Hud.hudColor2.getColor(), false).getRGB());
            ++i;
        }
        return endDelay;
    }

    public static int getColor(Color color) {
        return ColorUtils.getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int brightness) {
        return ColorUtils.getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return ColorUtils.getColor(brightness, brightness, brightness, alpha);
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? ColorUtils.interpolateColorHue(start, end, (float)angle / 360.0f) : ColorUtils.interpolateColorC(start, end, (float)angle / 360.0f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        return new Color(ColorUtils.interpolateInt(color1.getRed(), color2.getRed(), amount), ColorUtils.interpolateInt(color1.getGreen(), color2.getGreen(), amount), ColorUtils.interpolateInt(color1.getBlue(), color2.getBlue(), amount), ColorUtils.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        Color resultColor = Color.getHSBColor(ColorUtils.interpolateFloat(color1HSB[0], color2HSB[0], amount), ColorUtils.interpolateFloat(color1HSB[1], color2HSB[1], amount), ColorUtils.interpolateFloat(color1HSB[2], color2HSB[2], amount));
        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(), ColorUtils.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return ColorUtils.interpolate(oldValue, newValue, (float)interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return ColorUtils.interpolate(oldValue, newValue, (float)interpolationValue).intValue();
    }

    public static int getColor(int red, int green, int blue) {
        return ColorUtils.getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        int color1 = color | alpha << 24;
        color1 |= red << 16;
        color1 |= green << 8;
        return color1 |= blue;
    }

    public static int blendColours(int[] colours, double progress) {
        int size = colours.length;
        if (progress == 1.0) {
            return colours[0];
        }
        if (progress == 0.0) {
            return colours[size - 1];
        }
        double mulProgress = Math.max(0.0, (1.0 - progress) * (double)(size - 1));
        int index = (int)mulProgress;
        return ColorUtils.fadeBetween(colours[index], colours[index + 1], mulProgress - (double)index);
    }

    public static int fadeBetween(int startColour, int endColour, double progress) {
        if (!(progress > 1.0)) return ColorUtils.fadeTo(startColour, endColour, progress);
        progress = 1.0 - progress % 1.0;
        return ColorUtils.fadeTo(startColour, endColour, progress);
    }

    public static int fadeBetween(int startColour, int endColour, long offset) {
        return ColorUtils.fadeBetween(startColour, endColour, (double)((System.currentTimeMillis() + offset) % 2000L) / 1000.0);
    }

    public static int fadeBetween(int startColour, int endColour) {
        return ColorUtils.fadeBetween(startColour, endColour, 0L);
    }

    public static int fadeTo(int startColour, int endColour, double progress) {
        double invert = 1.0 - progress;
        int r = (int)((double)(startColour >> 16 & 0xFF) * invert + (double)(endColour >> 16 & 0xFF) * progress);
        int g = (int)((double)(startColour >> 8 & 0xFF) * invert + (double)(endColour >> 8 & 0xFF) * progress);
        int b = (int)((double)(startColour & 0xFF) * invert + (double)(endColour & 0xFF) * progress);
        int a = (int)((double)(startColour >> 24 & 0xFF) * invert + (double)(endColour >> 24 & 0xFF) * progress);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }
}
