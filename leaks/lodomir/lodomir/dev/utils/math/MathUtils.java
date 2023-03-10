/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public final class MathUtils {
    public final SecureRandom RANDOM = new SecureRandom();

    public double lerp(double a, double b, double c) {
        return a + c * (b - a);
    }

    public static float lerp(float a, float b, float c) {
        return a + c * (b - a);
    }

    public boolean roughlyEquals(double alpha, double beta) {
        return Math.abs(alpha - beta) < 1.0E-4;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            double d = min;
            min = max;
            max = d;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public double getVariance(Collection<? extends Number> data) {
        int count = 0;
        double sum = 0.0;
        double variance = 0.0;
        for (Number number : data) {
            sum += number.doubleValue();
            ++count;
        }
        double average = sum / (double)count;
        for (Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }
        return variance;
    }

    public double getStandardDeviation(Collection<? extends Number> data) {
        return Math.sqrt(this.getVariance(data));
    }

    public double getAverage(Collection<? extends Number> data) {
        double sum = 0.0;
        for (Number number : data) {
            sum += number.doubleValue();
        }
        return sum / (double)data.size();
    }

    public static double roundToDecimalPlace(double value, double inc) {
        double halfOfInc = inc / 2.0;
        double floored = Math.floor(value / inc) * inc;
        if (value >= floored + halfOfInc) {
            return new BigDecimal(Math.ceil(value / inc) * inc, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
        }
        return new BigDecimal(floored, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
    }

    public double getCps(Collection<? extends Number> data) {
        return 20.0 * this.getAverage(data);
    }
}

