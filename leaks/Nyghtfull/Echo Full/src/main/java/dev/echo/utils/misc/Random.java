package dev.echo.utils.misc;

import java.security.SecureRandom;

public class Random {

    public static double dbRandom(double min, double max)
    {
        final SecureRandom rng = new SecureRandom();
        return rng.nextDouble() * (max - min) + min;
    }

    public static float flRandom(float min, float max)
    {
        final SecureRandom rng = new SecureRandom();
        return rng.nextFloat() * (max - min) + min;
    }

}