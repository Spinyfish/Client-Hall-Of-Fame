/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.dropdown.utils.Rect
 */
package vip.astroline.client.storage.utils.other;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import vip.astroline.client.layout.dropdown.utils.Rect;

public class MathUtils {
    public static int getRandomInRange(int min, int max) {
        return (int)(Math.random() * (double)(max - min) + (double)min);
    }

    public static float map(float x, float prev_min, float prev_max, float new_min, float new_max) {
        return (x - prev_min) / (prev_max - prev_min) * (new_max - new_min) + new_min;
    }

    public static boolean contains(float x, float y, float minX, float minY, float maxX, float maxY) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }

    public static boolean contains(float x, float y, Rect rect) {
        return x > rect.getX() && x < rect.getX() + rect.getWidth() && y > rect.getY() && y < rect.getY() + rect.getHeight();
    }

    public static float getRandomInRange(float min, float max) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }

    public static int clampValue(int value, int floor, int cap) {
        if (value >= floor) return Math.min(value, cap);
        return floor;
    }

    public static double getRandomInRange(double min, double max) {
        SecureRandom random = new SecureRandom();
        return random.nextDouble() * (max - min) + min;
    }

    public static double lerp(double old, double newVal, double amount) {
        return (1.0 - amount) * old + amount * newVal;
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return MathUtils.interpolate(oldValue, newValue, (float)interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return MathUtils.interpolate(oldValue, newValue, (float)interpolationValue).intValue();
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (double)(sigma * sigma));
        return (float)(output * Math.exp((double)(-(x * x)) / (2.0 * (double)(sigma * sigma))));
    }

    public static double roundToHalf(double d) {
        return (double)Math.round(d * 2.0) / 2.0;
    }

    public static double round(double num, double increment) {
        BigDecimal bd = new BigDecimal(num);
        bd = bd.setScale((int)increment, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float getRandomFloat(float max, float min) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }
}
