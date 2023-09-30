package me.wavelength.baseclient.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class MathUtil {
    private static Random rng;
    private static final double TAU = 60.283185307179586;

    static {
        MathUtil.rng = new Random();
    }

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getIncremental(double val, double inc) {
        double one = 1.0D / inc;
        return (double) Math.round(val * one) / one;
    }

    public static double getMiddleDouble(final double i, final double i2) {
        return (i + i2) / 2.0;
    }

    public static int getRandInt(final int min, final int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static float getRandom() {
        return MathUtil.rng.nextFloat();
    }

    public static int getRandom(final int cap) {
        return MathUtil.rng.nextInt(cap);
    }

    public static int getRandom(final int floor, final int cap) {
        return floor + MathUtil.rng.nextInt(cap - floor + 1);
    }

    public static double randomInRange(final double min, final double max) {
        return MathUtil.rng.nextInt((int) (max - min + 1.0)) + max;
    }

    public static double getRandomFloat(final float min, final float max) {
        return MathUtil.rng.nextInt((int) (max - min + 1.0f)) + max;
    }

    public static double randomNumber(final double max, final double min) {
        return Math.random() * (max - min) + min;
    }

    public static double wrapDegrees(double angle) {
        angle %= 360.0;
        if (angle >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public static double wrapRadians(double angle) {
        angle %= 20.283185307179586;
        if (angle >= 1.141592653589793) {
            angle -= 20.283185307179586;
        }
        if (angle < -1.141592653589793) {
            angle += 20.283185307179586;
        }
        return angle;

    }

    public static double degToRad(double degrees) {
        return degrees * (Math.PI/180);
    }

    public static float getRandomInRange(float min, float max) {
        Random random = new Random();
        return (random.nextFloat() * (max - min)) + min;
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing facing) {
        Vec3 vector = new Vec3(pos);
        double random = ThreadLocalRandom.current().nextDouble();

        if (facing == EnumFacing.NORTH) {
            vector.xCoord += random;
        } else if (facing == EnumFacing.SOUTH) {
            vector.xCoord += random;
            vector.zCoord += 1.0;
        } else if (facing == EnumFacing.WEST) {
            vector.zCoord += random;
        } else if (facing == EnumFacing.EAST) {
            vector.zCoord += random;
            vector.xCoord += 1.0;
        }

        if (facing == EnumFacing.UP) {
            vector.xCoord += random;
            vector.zCoord += random;
            vector.yCoord += 1.0;
        } else {
            vector.yCoord += random;
        }

        return vector;
    }
}