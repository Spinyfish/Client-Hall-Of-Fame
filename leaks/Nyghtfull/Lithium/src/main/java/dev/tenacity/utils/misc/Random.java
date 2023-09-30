package dev.tenacity.utils.misc;


public class Random {

    public static int nextInt(final int startInclusive, final int endExclusive) {
        if (endExclusive - startInclusive <= 0)
            return startInclusive;

        return startInclusive + new java.util.Random().nextInt(endExclusive - startInclusive);
    }

    public static double nextDouble(final double startInclusive, final double endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0D)
            return startInclusive;

        return startInclusive + ((endInclusive - startInclusive) * Math.random());
    }

}