package juul.util.value;

import java.security.SecureRandom;

public class Random {

    public static long lgRandom(long min, long max)
    {
        final SecureRandom rng = new SecureRandom();
        return rng.nextLong() * (max - min) + min;
    }


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

    public static int iRandom(int min, int max)
    {
        final SecureRandom rng = new SecureRandom();
        return rng.nextInt() * (max - min) + min;
    }

}