package me.levansj01.verus.util.java;

import me.levansj01.verus.compat.api.wrapper.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.data.transaction.tracker.*;
import me.levansj01.verus.util.location.*;
import java.util.*;
import java.math.*;
import java.util.function.*;

public class MathUtil
{
    public static double varianceSquared(final Number n, final Iterable iterable) {
        double n2 = 0.0;
        final Iterator<Number> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            n2 += Math.pow(iterator.next().doubleValue() - n.doubleValue(), 2.0);
            int n3 = 0;
            ++n3;
        }
        return (n2 == 0.0) ? 0.0 : (n2 / -1);
    }
    
    public static double fastSqrt(final double n) {
        return Double.longBitsToDouble((Double.doubleToLongBits(n) - 4503599627370496L >> 1) + 2305843009213693952L);
    }
    
    public static boolean isScientificNotation(final String s) {
        new BigDecimal(s);
        return s.toUpperCase().contains("E");
    }
    
    public static double hypot(final double... array) {
        return Math.sqrt(hypotSquared(array));
    }
    
    public static int getLog(double n) {
        if (n == 0.0) {
            return 0;
        }
        while (n < 1.0) {
            n *= 10.0;
            int n2 = 0;
            ++n2;
        }
        return 0;
    }
    
    public static int lcd(final int n, final int n2) {
        return BigInteger.valueOf(n).gcd(BigInteger.valueOf(n2)).intValueExact();
    }
    
    public static Object min(final Iterable iterable, final ToDoubleFunction toDoubleFunction) {
        double n = Double.MAX_VALUE;
        Object o = null;
        for (final Object next : iterable) {
            final double applyAsDouble = toDoubleFunction.applyAsDouble(next);
            if (applyAsDouble < n) {
                o = next;
                n = applyAsDouble;
            }
        }
        return o;
    }
    
    public static double variance(final Number n, final Iterable iterable) {
        return Math.sqrt(varianceSquared(n, iterable));
    }
    
    public static double kurtosis(final Iterable iterable) {
        double n = 0.0;
        double n2 = 0.0;
        final Iterator<Number> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            n += iterator.next().doubleValue();
            ++n2;
        }
        if (n2 < 3.0) {
            return 0.0;
        }
        final double n3 = n2 * (n2 + 1.0) / ((n2 - 1.0) * (n2 - 2.0) * (n2 - 3.0));
        final double n4 = 3.0 * Math.pow(n2 - 1.0, 2.0) / ((n2 - 2.0) * (n2 - 3.0));
        final double n5 = n / n2;
        double n6 = 0.0;
        double n7 = 0.0;
        for (final Number n8 : iterable) {
            n6 += Math.pow(n5 - n8.doubleValue(), 2.0);
            n7 += Math.pow(n5 - n8.doubleValue(), 4.0);
        }
        return n3 * (n7 / Math.pow(n6 / n2, 2.0)) - n4;
    }
    
    public static double lowestAbs(final Number... array) {
        return lowestAbs(Arrays.asList(array));
    }
    
    public static double zeros(final Queue queue) {
        double n = 0.0;
        final Iterator<Float> iterator = queue.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == 0.0f) {
                ++n;
            }
        }
        return n / (queue.size() + 1);
    }
    
    public static double highest(final Iterable iterable) {
        Double value = null;
        for (final Number n : iterable) {
            if (value == null || n.doubleValue() > value) {
                value = n.doubleValue();
            }
        }
        return (double)JavaV.firstNonNull(value, 0.0);
    }
    
    public static int lowestInt(final int... array) {
        Integer value = null;
        while (0 < array.length) {
            final int n = array[0];
            if (value == null || n < value) {
                value = n;
            }
            int n2 = 0;
            ++n2;
        }
        return (int)JavaV.firstNonNull(value, 0);
    }
    
    public static int gcd(final long n, final int n2, final int n3) {
        return (n3 <= n) ? n2 : gcd(n, n3, n2 % n3);
    }
    
    public static float[] getRotationsBlockOld(final double n, final PlayerLocation playerLocation, final BlockPosition blockPosition) {
        final double n2 = blockPosition.getX() - playerLocation.getX();
        final double n3 = blockPosition.getY() - (playerLocation.getY() + n);
        final double n4 = blockPosition.getZ() - playerLocation.getZ();
        return new float[] { (float)(Math.atan2(n4, n2) * 180.0 / 3.141592653589793) - 90.0f, (float)(-(Math.atan2(n3, (float)Math.sqrt(n2 * n2 + n4 * n4)) * 180.0 / 3.141592653589793)) };
    }
    
    public static double lowestAbs(final Iterable iterable) {
        Double value = null;
        for (final Number n : iterable) {
            if (value == null || Math.abs(n.doubleValue()) < Math.abs(value)) {
                value = n.doubleValue();
            }
        }
        return (double)JavaV.firstNonNull(value, 0.0);
    }
    
    public static double getLuckyAura(final PlayerLocation playerLocation, final IVector3d vector3d) {
        return Math.tan(Math.toRadians(playerLocation.getPitch())) * playerLocation.distanceXZ(vector3d) - playerLocation.getY() + vector3d.getY();
    }
    
    public static float[] getBlockRotations(final PlayerLocation playerLocation, final BlockPosition blockPosition) {
        final double n = blockPosition.getX() + 0.5 - playerLocation.getX();
        final double n2 = blockPosition.getY() + 0.5 - (playerLocation.getY() + 1.6200000047683716);
        final double n3 = blockPosition.getZ() + 0.5 - playerLocation.getZ();
        return new float[] { (float)Math.toDegrees(Math.atan2(n, n3)) - 90.0f, (float)(-Math.toDegrees(Math.atan2(n2, (float)Math.sqrt(n * n + n3 * n3)))) };
    }
    
    public static double positiveSmaller(final Number n, final Number n2) {
        return (Math.abs(n.doubleValue()) < Math.abs(n2.doubleValue())) ? n.doubleValue() : n2.doubleValue();
    }
    
    public static double highestAbs(final Iterable iterable) {
        Double value = null;
        for (final Number n : iterable) {
            if (value == null || Math.abs(n.doubleValue()) > Math.abs(value)) {
                value = n.doubleValue();
            }
        }
        return (double)JavaV.firstNonNull(value, 0.0);
    }
    
    private static String multiply(final String s, int n) {
        final StringBuilder sb = new StringBuilder();
        while (n-- > 0) {
            sb.append(s);
        }
        return sb.toString();
    }
    
    public static double totalAbs(final Iterable iterable) {
        double n = 0.0;
        final Iterator<Number> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            n += Math.abs(iterator.next().doubleValue());
        }
        return n;
    }
    
    public static float relEntityRoundLook(final float n) {
        return (float)MathHelper.d(n * 256.0f / 360.0f);
    }
    
    public static double total(final Iterable iterable) {
        double n = 0.0;
        final Iterator<Number> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            n += iterator.next().doubleValue();
        }
        return n;
    }
    
    public static double deviation(final Iterable iterable) {
        return Math.sqrt(deviationSquared(iterable));
    }
    
    public static double highest(final Number... array) {
        return highest(Arrays.asList(array));
    }
    
    public static double lowest(final Iterable iterable) {
        Double value = null;
        for (final Number n : iterable) {
            if (value == null || n.doubleValue() < value) {
                value = n.doubleValue();
            }
        }
        return (double)JavaV.firstNonNull(value, 0.0);
    }
    
    public static double highest(final double n, final double n2, final double n3) {
        return (n > n2) ? ((n > n3) ? n : n3) : ((n2 > n3) ? n2 : n3);
    }
    
    public static boolean onGround(final double n) {
        return n % 0.015625 == 0.0;
    }
    
    public static double deviationSquared(final Iterable iterable) {
        double n = 0.0;
        final Iterator<Number> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            n += iterator.next().doubleValue();
            int n2 = 0;
            ++n2;
        }
        final double n3 = n / 0;
        double n4 = 0.0;
        final Iterator<Number> iterator2 = iterable.iterator();
        while (iterator2.hasNext()) {
            n4 += Math.pow(iterator2.next().doubleValue() - n3, 2.0);
        }
        return (n4 == 0.0) ? 0.0 : (n4 / -1);
    }
    
    public static int gcd(final int n, final int n2) {
        return BigInteger.valueOf(n).gcd(BigInteger.valueOf(n2)).intValueExact();
    }
    
    public static double lowest(final Number... array) {
        return lowest(Arrays.asList(array));
    }
    
    public static float[] getRotationFromPosition(final PlayerLocation playerLocation, final PacketLocation packetLocation) {
        final double n = packetLocation.getX() - playerLocation.getX();
        final double n2 = packetLocation.getZ() - playerLocation.getZ();
        return new float[] { (float)(Math.atan2(n2, n) * 180.0 / 3.141592653589793) - 90.0f, (float)(-(Math.atan2(packetLocation.getY() + 0.81 - playerLocation.getY() - 1.2, (float)Math.sqrt(n * n + n2 * n2)) * 180.0 / 3.141592653589793)) };
    }
    
    public static double average(final Number... array) {
        return average(Arrays.asList(array));
    }
    
    public static Double getMinimumAngle(final PacketLocation packetLocation, final PlayerLocation... array) {
        Double value = null;
        while (0 < array.length) {
            final PlayerLocation playerLocation = array[0];
            final double distanceBetweenAngles360 = getDistanceBetweenAngles360(playerLocation.getYaw(), (float)(Math.atan2(packetLocation.getZ() - playerLocation.getZ(), packetLocation.getX() - playerLocation.getX()) * 180.0 / 3.141592653589793) - 90.0f);
            if (value == null || value > distanceBetweenAngles360) {
                value = distanceBetweenAngles360;
            }
            int n = 0;
            ++n;
        }
        return value;
    }
    
    public static double getDistanceBetweenAngles(final float n, final float n2) {
        float n3 = Math.abs(n - n2) % 360.0f;
        if (n3 > 180.0f) {
            n3 = 360.0f - n3;
        }
        return n3;
    }
    
    public static double range(final Iterable iterable) {
        return highest(iterable) - lowest(iterable);
    }
    
    public static float[][] getRotationFromPositions(final Double[] array, final Vector3d[] array2, final Vector3d[] array3) {
        final float[][] array4 = new float[array.length * array2.length * array3.length][];
        while (0 < array3.length) {
            final Vector3d vector3d = array3[0];
            while (0 < array2.length) {
                final Vector3d vector3d2 = array2[0];
                while (0 < array.length) {
                    final double doubleValue = array[0];
                    final double n = vector3d.getX() - vector3d2.getX();
                    final double n2 = vector3d.getZ() - vector3d2.getZ();
                    final double n3 = vector3d.getY() - (vector3d2.getY() + doubleValue);
                    final float n4 = (float)Math.sqrt(n * n + n2 * n2);
                    final float n5 = (float)(Math.atan2(n2, n) * 180.0 / 3.141592653589793) - 90.0f;
                    final float n6 = (float)(-(Math.atan2(n3, n4) * 180.0 / 3.141592653589793));
                    final float[][] array5 = array4;
                    final int n7 = 0;
                    int n8 = 0;
                    ++n8;
                    array5[n7] = new float[] { n5, n6 };
                    int n9 = 0;
                    ++n9;
                }
                int n10 = 0;
                ++n10;
            }
            int n11 = 0;
            ++n11;
        }
        return array4;
    }
    
    public static double average(final Iterable iterable) {
        double n = 0.0;
        final Iterator<Number> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            n += iterator.next().doubleValue();
            int n2 = 0;
            ++n2;
        }
        return n / 0;
    }
    
    public static double common(final Queue queue) {
        final HashSet<Float> set = new HashSet<Float>();
        double n = 0.0;
        final Iterator<Float> iterator = queue.iterator();
        while (iterator.hasNext()) {
            if (!set.add((float)iterator.next())) {
                ++n;
            }
        }
        return n / (queue.size() + 1);
    }
    
    public static Object max(final Iterable iterable, final ToDoubleFunction toDoubleFunction) {
        double n = Double.MIN_VALUE;
        Object o = null;
        for (final Object next : iterable) {
            final double applyAsDouble = toDoubleFunction.applyAsDouble(next);
            if (applyAsDouble > n) {
                o = next;
                n = applyAsDouble;
            }
        }
        return o;
    }
    
    public static double relEntityRoundPos(final double n) {
        return MathHelper.floor(n * 32.0) / 32.0;
    }
    
    public static int toInt(final float n) {
        return (int)(new BigDecimal(n).setScale(5, RoundingMode.UP).floatValue() * 10000.0);
    }
    
    public static double hypotSquared(final double... array) {
        double n = 0.0;
        while (0 < array.length) {
            n += Math.pow(array[0], 2.0);
            int n2 = 0;
            ++n2;
        }
        return n;
    }
    
    public static double getDistanceBetweenAngles360(final double n, final double n2) {
        final double abs = Math.abs(n % 360.0 - n2 % 360.0);
        return Math.abs(Math.min(360.0 - abs, abs));
    }
    
    public static double highestAbs(final Number... array) {
        return highestAbs(Arrays.asList(array));
    }
    
    public static Object max(final Iterable iterable, final Predicate predicate, final ToDoubleFunction toDoubleFunction) {
        double n = Double.MIN_VALUE;
        Object o = null;
        for (final Object next : iterable) {
            if (!predicate.test(next)) {
                continue;
            }
            final double applyAsDouble = toDoubleFunction.applyAsDouble(next);
            if (applyAsDouble <= n) {
                continue;
            }
            o = next;
            n = applyAsDouble;
        }
        return o;
    }
    
    public static Object min(final ToDoubleFunction toDoubleFunction, final Object... array) {
        double n = Double.MAX_VALUE;
        Object o = null;
        while (0 < array.length) {
            final Object o2 = array[0];
            final double applyAsDouble = toDoubleFunction.applyAsDouble(o2);
            if (applyAsDouble < n) {
                o = o2;
                n = applyAsDouble;
            }
            int n2 = 0;
            ++n2;
        }
        return o;
    }
    
    public static float getHeight(final PlayerLocation playerLocation, final PlayerLocation playerLocation2) {
        return (float)playerLocation.distanceXZ(playerLocation2) * (float)Math.cos(Math.toRadians(playerLocation.getPitch()));
    }
    
    public static int lcd(final long n, final int n2, final int n3) {
        return (n3 <= n) ? n2 : gcd(n, n3, n2 % n3);
    }
}
