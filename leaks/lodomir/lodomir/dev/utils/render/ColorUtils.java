/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.render;

import java.awt.Color;
import lodomir.dev.utils.math.apache.ApacheMath;

public class ColorUtils {
    public static Color rainbow(int delay, float saturation, double speed) {
        double rainbowState = ApacheMath.ceil(((double)System.currentTimeMillis() * speed - (double)delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), saturation, 1.0f);
    }

    public static Color getColorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed) {
        long now = (long)(speed * (double)System.currentTimeMillis() + (double)((long)(-index) * timePerIndex));
        float rd = (float)(firstColor.getRed() - secondColor.getRed()) / time;
        float gd = (float)(firstColor.getGreen() - secondColor.getGreen()) / time;
        float bd = (float)(firstColor.getBlue() - secondColor.getBlue()) / time;
        float rd2 = (float)(secondColor.getRed() - firstColor.getRed()) / time;
        float gd2 = (float)(secondColor.getGreen() - firstColor.getGreen()) / time;
        float bd2 = (float)(secondColor.getBlue() - firstColor.getBlue()) / time;
        int re1 = ApacheMath.round((float)secondColor.getRed() + rd * (float)(now % (long)time));
        int ge1 = ApacheMath.round((float)secondColor.getGreen() + gd * (float)(now % (long)time));
        int be1 = ApacheMath.round((float)secondColor.getBlue() + bd * (float)(now % (long)time));
        int re2 = ApacheMath.round((float)firstColor.getRed() + rd2 * (float)(now % (long)time));
        int ge2 = ApacheMath.round((float)firstColor.getGreen() + gd2 * (float)(now % (long)time));
        int be2 = ApacheMath.round((float)firstColor.getBlue() + bd2 * (float)(now % (long)time));
        if (now % ((long)time * 2L) < (long)time) {
            return new Color(ColorUtils.getColor(255, re2, ge2, be2));
        }
        return new Color(ColorUtils.getColor(255, re1, ge1, be1));
    }

    public static int getColor(int A, int R, int G, int B) {
        return (A & 0xFF) << 24 | (R & 0xFF) << 16 | (G & 0xFF) << 8 | B & 0xFF;
    }

    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0f + (float)index / (float)count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }
}

