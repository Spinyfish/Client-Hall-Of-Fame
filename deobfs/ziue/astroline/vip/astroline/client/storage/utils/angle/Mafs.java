/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.storage.utils.angle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

public class Mafs {
    private static final Random rng = new Random();

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public static Double clamp(double number, double min, double max) {
        if (number < min) {
            return min;
        }
        if (!(number > max)) return number;
        return max;
    }

    public static Double getDifference(double num1, double num2) {
        if (!(num1 > num2)) return num2 - num1;
        double tempNum = num1;
        num1 = num2;
        num2 = tempNum;
        return num2 - num1;
    }

    public static float randomSeed(long seed) {
        seed = System.currentTimeMillis() + seed;
        return 0.4f + (float)new Random(seed).nextInt(80000000) / 1.0E9f + 1.45E-9f;
    }

    public static float secRanFloat(float min, float max) {
        SecureRandom rand = new SecureRandom();
        return rand.nextFloat() * (max - min) + min;
    }

    public static int randInt(int min, int max) {
        SecureRandom rand = new SecureRandom();
        return rand.nextInt() * (max - min) + min;
    }

    public static double secRanDouble(double min, double max) {
        SecureRandom rand = new SecureRandom();
        return rand.nextDouble() * (max - min) + min;
    }

    public static float getAngleDifference(float direction, float rotationYaw) {
        float phi = Math.abs(rotationYaw - direction) % 360.0f;
        float distance = phi > 180.0f ? 360.0f - phi : phi;
        return distance;
    }

    public static double getMiddle(double d, double e) {
        return (d + e) / 2.0;
    }

    public static float getMiddle(float i, float i1) {
        return (i + i1) / 2.0f;
    }

    public static double getMiddleint(double d, double e) {
        return (d + e) / 2.0;
    }

    public static int getRandom(int floor, int cap) {
        return floor + rng.nextInt(cap - floor + 1);
    }

    public static double getRandom(double floor, double cap) {
        return floor + (double)rng.nextInt((int)(cap - floor + 1.0));
    }

    public static double getRandomInRange(double min, double max) {
        double shifted;
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        if (!((shifted = scaled + min) > max)) return shifted;
        shifted = max;
        return shifted;
    }

    public static float getRandomInRange(float min, float max) {
        Random random = new Random();
        float range = max - min;
        float scaled = random.nextFloat() * range;
        float shifted = scaled + min;
        return shifted;
    }

    public static int getRandomInRange(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt(max - min + 1) + min;
        return randomNum;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
