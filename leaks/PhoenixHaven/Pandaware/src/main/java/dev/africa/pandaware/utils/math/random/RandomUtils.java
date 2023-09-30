package dev.africa.pandaware.utils.math.random;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class RandomUtils {

    public int nextInt(int min, int max) {
        if (min == max || max - min <= 0D)
            return min;

        return (int) (min + ((max - min) * Math.random()));
    }

    public double nextDouble(double min, double max) {
        if (min == max || max - min <= 0D)
            return min;

        return min + ((max - min) * Math.random());
    }

    public long nextLong(long min, long max) {
        if (min == max || max - min <= 0D)
            return min;

        return (long) (min + ((max - min) * Math.random()));
    }

    public float nextFloat(float min, float max) {
        if (min == max || max - min <= 0D)
            return min;

        return (float) (min + ((max - min) * Math.random()));
    }

    public String randomNumber(int length) {
        return random(length, "123456789");
    }

    public String randomString(int length) {
        return random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_");
    }

    public String random(int length, String chars) {
        return random(length, chars.toCharArray());
    }

    public String random(int length, char[] chars) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++)
            stringBuilder.append(chars[ThreadLocalRandom.current().nextInt(chars.length)]);
        return stringBuilder.toString();
    }
}
