package org.crystalpvp.anticheat.api.util;

import org.crystalpvp.anticheat.data.location.CrystalLocation;
import net.minecraft.server.v1_8_R3.MathHelper;

import java.text.DecimalFormat;

public class MathUtil {

    public static final float PI = (float) Math.PI;
    private static final float[] SIN_TABLE_FAST = new float[4096];

    public static double hypot(double ... values) {
        return Math.sqrt(MathUtil.hypotSquared(values));
    }
    public static double getDistanceBetweenAngles360(double angle1, double angle2) {
        double distance = Math.abs(angle1 % 360.0 - angle2 % 360.0);
        distance = Math.min(360.0 - distance, distance);
        return Math.abs(distance);
    }

    public static  double hypotSquared(double ... values) {
        double total = 0.0;
        double[] var3 = values;
        int var4 = values.length;
        for (int var5 = 0; var5 < var4; ++var5) {
            double value = var3[var5];
            total += Math.pow(value, 2.0);
        }
        return total;
    }

    public static double average(Iterable<? extends Number> numbers) {
        double total = 0.0;
        int i = 0;
        for (Number number : numbers) {
            total += number.doubleValue();
            ++i;
        }
        return total / (double)i;
    }
    public static double varianceSquared(Number value, Iterable<? extends Number> numbers) {
        double variance = 0.0;
        int i = 0;
        for (Number number : numbers) {
            variance += Math.pow(number.doubleValue() - value.doubleValue(), 2.0);
            ++i;
        }
        return variance / (double)(i - 1);
    }

    public static double variance(Number value, Iterable<? extends Number> numbers) {
        return Math.sqrt(MathUtil.varianceSquared(value, numbers));
    }

    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < (double) var2 ? var2 - 1 : var2;
    }
    public static double deviation(Iterable<? extends Number> numbers) {
        return Math.sqrt(MathUtil.deviationSquared(numbers));
    }
    public static double deviationSquared(Iterable<? extends Number> numbers) {
        double total = 0.0;
        int i = 0;
        for (Number number : numbers) {
            total += number.doubleValue();
            ++i;
        }
        double average = total / (double)i;
        double deviation = 0.0;
        for (Number number : numbers) {
            deviation += Math.pow(number.doubleValue() - average, 2.0);
        }
        return deviation / (double)(i - 1);
    }
    public static float sin(float par0) {
        return SIN_TABLE_FAST[(int) (par0 * 651.8986F) & 4095];
    }

    public static float cos(float par0) {
        return SIN_TABLE_FAST[(int) ((par0 + ((float) Math.PI / 2F)) * 651.8986F) & 4095];
    }

    public static float toRadians(float angdeg) {
        return angdeg / 180.0F * PI;
    }

    static {
        int i;

        for (i = 0; i < 4096; i++) {
            SIN_TABLE_FAST[i] = (float) Math.sin((double) (((float) i + 0.5F) / 4096.0F * ((float) Math.PI * 2F)));
        }

        for (i = 0; i < 360; i += 90) {
            SIN_TABLE_FAST[(int) ((float) i * 11.377778F) & 4095] = (float) Math.sin((double) ((float) i * 0.017453292F));
        }
    }

    public static float[] getRotationFromPosition(final CrystalLocation playerCrystalLocation, final CrystalLocation targetCrystalLocation) {
        final double xDiff = targetCrystalLocation.getX() - playerCrystalLocation.getX();
        final double zDiff = targetCrystalLocation.getZ() - playerCrystalLocation.getZ();
        final double yDiff = targetCrystalLocation.getY() - (playerCrystalLocation.getY() + 0.12);
        final double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[] { yaw, pitch };
    }
    
    public static int pingFormula(final long ping) {
        return (int) Math.ceil(ping / 2L / 50.0) + 2;
    }
    
    public static float getDistanceBetweenAngles(final float angle1, final float angle2) {
        float distance = Math.abs(angle1 - angle2) % 360.0f;

        if (distance > 180.0f) {
            distance = 360.0f - distance;
        }

        return distance;
    }


    public static double doubleDecimal(double i, int decimal) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(decimal);
        df.setMinimumFractionDigits(0);
        df.setDecimalSeparatorAlwaysShown(false);
        return Double.parseDouble(df.format(i).replace(",", "."));
    }


    public static float floatDecimal(double i, int decimal) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(decimal);
        df.setMinimumFractionDigits(0);
        df.setDecimalSeparatorAlwaysShown(false);
        return Float.parseFloat(df.format(i).replace(",", "."));
    }

}
