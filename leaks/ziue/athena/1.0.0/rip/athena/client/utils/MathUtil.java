package rip.athena.client.utils;

import java.util.concurrent.*;
import java.math.*;
import net.minecraft.util.*;

public final class MathUtil
{
    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            final double d = min;
            min = max;
            max = d;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
    
    public static double round(final double value, final int places) {
        final BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static double round(final double value, final int scale, final double inc) {
        final double halfOfInc = inc / 2.0;
        final double floored = Math.floor(value / inc) * inc;
        if (value >= floored + halfOfInc) {
            return new BigDecimal(Math.ceil(value / inc) * inc).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        }
        return new BigDecimal(floored).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static double roundWithSteps(final double value, final double steps) {
        double a = Math.round(value / steps) * steps;
        a *= 1000.0;
        a = (int)a;
        a /= 1000.0;
        return a;
    }
    
    public static double lerp(final double a, final double b, final double c) {
        return a + c * (b - a);
    }
    
    public static float lerp(final float a, final float b, final float c) {
        return a + c * (b - a);
    }
    
    public static double getDistance(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        final double d0 = x2 - x1;
        final double d2 = y2 - y1;
        final double d3 = z2 - z1;
        return MathHelper.sqrt_double(d0 * d0 + d2 * d2 + d3 * d3);
    }
    
    public static double clamp(final double min, final double max, final double n) {
        return Math.max(min, Math.min(max, n));
    }
    
    private MathUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
