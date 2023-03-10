package store.femboy.spring.impl.util;

import net.minecraft.util.MathHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtils {

    public int getRandomIntBetween(int min, int max) {
        Random random = new Random();
        return MathHelper.getRandomIntegerInRange(random, min, max);
    }

    public double getRandomDoubleBetween(double min, double max) {
        Random random = new Random();
        return MathHelper.getRandomDoubleInRange(random, min, max);
    }

    public float getRandomFloatBetween(float min, float max) {
        Random random = new Random();
        return MathHelper.randomFloatClamp(random, min, max);
    }

    public long getRandomLongBetween(long min, long max) {
        Random random = new Random();
        return MathHelper.getRandomLongInRange(random, min, max);
    }

    public double roundToPlace(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public float choseRandomValue(float value1, float value2) {
        if (getRandomIntBetween(0, 1) == 0) {
            return value1;
        } else {
            return value2;
        }
    }

    public double roundToFraction(double x, long fraction) {
        return (double) Math.round(x * fraction) / fraction;
    }

    public double roundToIncrement(double value, double increment) {
        double v = (double) Math.round(value / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * @return returns the percentage of the two given values from 0 to 1
     */
    public double getPercentage(double total, double amount){
        return amount / total;
    }
}
