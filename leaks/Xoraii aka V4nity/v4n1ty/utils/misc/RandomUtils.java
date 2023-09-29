package v4n1ty.utils.misc;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private static Random random;

    public static int randomNumber(int max, int min) {
        return Math.round((float)min + (float)Math.random() * (float)(max - min));
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f) {
            return startInclusive;
        }
        return (float)((double)startInclusive + (double)(endInclusive - startInclusive) * Math.random());
    }

    public static double getRandomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 1.0);
    }

    public static float randomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
}