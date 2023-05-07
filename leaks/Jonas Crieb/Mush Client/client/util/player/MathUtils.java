package client.util.player;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.commons.lang3.RandomUtils;

public class MathUtils {
    public static double getIncremental(double val, double inc) {
        double one = 1.0D / inc;
        return Math.round(val * one) / one;
    }

    public static double preciseRound(double value, double precision) {
        double scale = Math.pow(10.0D, precision);
        return Math.round(value * scale) / scale;
    }

    public static double square(double d) {
        return d * d;
    }

    public static double roundToPlace(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static Random rng = new Random();

    public static Random getRNG() {
        return rng;
    }

    public static double radians(double degress) {
        return degress * Math.PI / 180.0D;
    }

    public static int customRandInt(int min, int max) {
        return (new Random()).nextInt(max - min + 1) + min;
    }

    public static double toDecimalLength(double in, int places) {
        return Double.parseDouble(String.format("%." + places + "f", new Object[] { Double.valueOf(in) }));
    }

    public static double roundToDecimalPlace(double value, double inc) {
        double halfOfInc = inc / 2.0D;
        double floored = Math.floor(value / inc) * inc;
        if (value >= floored + halfOfInc)
            return (new BigDecimal(Math.ceil(value / inc) * inc, MathContext.DECIMAL64))
                    .stripTrailingZeros()
                    .doubleValue();
        return (new BigDecimal(floored, MathContext.DECIMAL64))
                .stripTrailingZeros()
                .doubleValue();
    }

    public static int getRandom(int i) {
        return RandomUtils.nextInt(0, i);
    }

    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal bd2 = new BigDecimal(value);
        bd2 = bd2.setScale(places, RoundingMode.HALF_UP);
        return bd2.doubleValue();
    }

    public boolean isEven(int number) {
        double n2 = number / 2.0D;
        int n3 = (int)(number / 2.0D);
        if (n3 - n2 == 0.0D)
            return true;
        return false;
    }

    public static float secRanFloat(float min, float max) {
        SecureRandom rand = new SecureRandom();
        return rand.nextFloat() * (max - min) + min;
    }

    public static double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted;
    }

    public static int getRandom(int floor, int cap) {
        return floor + getRNG().nextInt(cap - floor + 1);
    }

    public static double setRandom(double min, double max) {
        Random random = new Random();
        return min + random.nextDouble() * (max - min);
    }

    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public static int randomNumber(int max, int min) {
        return Math.round(min + (float)Math.random() * (max - min));
    }

    public static float randomFloatValue() {
        return (float)getRandomInRange(2.96219E-7D, 9.13303E-6D);
    }
}
