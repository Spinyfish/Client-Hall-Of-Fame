package club.pulsive.impl.util.math;

import club.pulsive.impl.util.math.apache.ApacheMath;
import lombok.experimental.UtilityClass;
import net.minecraft.util.MathHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@UtilityClass
public final class MathUtil {


    public float lerp(final float a, final float b, final float c) {
        return a + c * (b - a);
    }
    public double getDistance(double srcX, double srcZ, double dstX, double dstZ) {
        double xDiff = dstX - srcX;
        double zDiff = dstZ - srcZ;
        return MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
    }

    public float calculateGaussianValue(float x, float sigma) {
        double PI = ApacheMath.PI;
        double output = 1.0 / ApacheMath.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * ApacheMath.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public double tryParseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDifference(double base, double yaw) {
        final double bigger;
        if (base >= yaw)
            bigger = base - yaw;
        else
            bigger = yaw - base;
        return bigger;
    }

    public double tryParseFloat(String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean tryParseBoolean(String value, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    public double round(final double value, final double inc) {
        if (inc == 0.0) return value;
        else if (inc == 1.0) return Math.round(value);
        else {
            final double halfOfInc = inc / 2.0;
            final double floored = Math.floor(value / inc) * inc;

            if (value >= floored + halfOfInc)
                return new BigDecimal(Math.ceil(value / inc) * inc)
                        .doubleValue();
            else return new BigDecimal(floored)
                    .doubleValue();
        }
    }

    public double round(double value, int places, double increment) {
        if (places < 0)
            throw new IllegalArgumentException();

        final double flooredValue = ApacheMath.floor(value / increment) * increment;
        final double ceiledValue = ApacheMath.ceil(value / increment) * increment;
        final boolean aboveHalfIncrement = value >= flooredValue + (increment / 2.0);
        return new BigDecimal(aboveHalfIncrement ? ceiledValue : flooredValue)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public float round(float value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        return new BigDecimal(value)
                .setScale(places, RoundingMode.HALF_UP)
                .floatValue();
    }

    public float round(float value, int places, float increment) {
        if (places < 0)
            throw new IllegalArgumentException();
        final double flooredValue = ApacheMath.floor(value / increment) * increment;
        final double ceiledValue = ApacheMath.ceil(value / increment) * increment;
        final boolean aboveHalfIncrement = value >= flooredValue + (increment / 2.0);
        return new BigDecimal(aboveHalfIncrement ? ceiledValue : flooredValue)
                .setScale(places, RoundingMode.HALF_UP)
                .floatValue();
    }

    public double randomDouble(double min, double max) {
        if(min > max) return min;
        return new Random().nextDouble() * (max - min) + min;
    }

    public float randomFloat(float min, float max) {
        if(min > max) return min;
        return new Random().nextFloat() * (max - min) + min;
    }

    public long randomLong(long min, long max) {
        if(min > max) return min;
        return new Random().nextLong() * (max - min) + min;
    }

    public int randomInt(int min, int max) {
        if(min > max) return min;
        return new Random().nextInt(max) + min;
    }
    public byte randomByte(byte min, byte max) {
        if(min > max) return min;
        return (byte) (new Random().nextInt(max) + min);
    }
    public boolean randomBoolean() {
        return randomBoolean(1, 0.5);
    }
    public boolean randomBoolean(double range, double value) {
        return randomDouble(0, range) > value;
    }

    public byte[] randomBytes(int minSize, int maxSize, byte min, byte max) {
        int size = randomInt(minSize, maxSize);
        final byte[] out = new byte[size];
        for (int i = 0; i < size; i++) {
            out[i] = randomByte(min, max);
        }
        return out;
    }

}
