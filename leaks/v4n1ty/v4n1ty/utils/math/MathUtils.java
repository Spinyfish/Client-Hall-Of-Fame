package v4n1ty.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class MathUtils {
    public static final float TO_DEGREES = (float) (180.0F / Math.PI);

    public static double round(double num, double increment) {
        BigDecimal bd = new BigDecimal(num);
        bd = (bd.setScale((int) increment, RoundingMode.HALF_UP));
        return bd.doubleValue();
    }

    public static double calculatePercentage(double currentValue, double maxValue) {
        if (maxValue == 0) {
            throw new IllegalArgumentException("maxValue cannot be zero.");
        }

        return (currentValue / maxValue);
    }

    public static double square(double in) {
        return in * in;
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static double clamp(double min, double max, double n) {
        return Math.max(min, Math.min(max, n));
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double output = 1.0 / Math.sqrt(2.0 * Math.PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public static double[] yawPos(double value) {
        return yawPos(Minecraft.getMinecraft().thePlayer.rotationYaw * MathHelper.deg2Rad, value);
    }

    public static double randomDouble(double min, double max) {
        if(min > max) return min;
        return new Random().nextDouble() * (max - min) + min;
    }


    public static double[] yawPos(float yaw, double value) {
        return new double[]{-MathHelper.sin(yaw) * value, MathHelper.cos(yaw) * value};
    }
    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    public static int getRandomInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static float getRandomInRange(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static float getRandomFloat(float max, float min) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static double round(double value, int places, double increment) {
        final double flooredValue = Math.floor(value / increment) * increment;
        final double ceiledValue = Math.ceil(value / increment) * increment;
        final boolean aboveHalfIncrement = value >= flooredValue + (increment / 2);
        return BigDecimal.valueOf(aboveHalfIncrement ? ceiledValue : flooredValue)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }
}