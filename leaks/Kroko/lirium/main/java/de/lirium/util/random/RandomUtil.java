package de.lirium.util.random;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static double getGaussian(double average) {
        return random.nextGaussian() + average;
    }

}
