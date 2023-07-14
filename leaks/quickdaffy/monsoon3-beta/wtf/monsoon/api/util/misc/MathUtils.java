/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.misc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public static double square(double motionX) {
        return motionX * motionX;
    }

    public static double round(double num, double increment) {
        if (increment < 0.0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(num).setScale((int)increment, RoundingMode.HALF_UP).doubleValue();
    }

    public static float gaussian(float x, float sigma) {
        float pow = x / sigma;
        return (float)((double)(1.0f / (Math.abs(sigma) * 2.5066283f)) * Math.exp(-0.5f * pow * pow));
    }

    public static boolean within(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static <T extends Comparable<T>> T coerceIn(T value, T min, T max) {
        if (value.compareTo(min) < 0) {
            return min;
        }
        if (value.compareTo(max) > 0) {
            return max;
        }
        return value;
    }

    public static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double clamp(double min, double max, double n) {
        return Math.max(min, Math.min(max, n));
    }
}

