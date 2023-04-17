package me.levansj01.verus.util.java;

import java.util.Random;
import java.util.UUID;

public class MathHelper {

    public static boolean fastMath;
    private static final float[] SIN_TABLE_FAST;
    private static final double[] field_181164_e;
    private static final float[] SIN_TABLE;
    private static final int[] multiplyDeBruijnBitPosition;
    private static final float radToIndex = 651.8986f;
    public static final float PI2 = (float)Math.PI * 2;
    private static final float degFull = 360.0f;
    private static final int SIN_COUNT;
    public static final float PId2 = 1.5707964f;
    private static final double field_181163_d;
    private static final String __OBFID;
    private static final int SIN_BITS;
    public static final float field_180189_a;
    private static final int SIN_MASK;
    public static final float deg2Rad = (float)Math.PI / 180;
    public static final float PI = (float)Math.PI;
    private static final float degToIndex = 11.377778f;
    private static final double[] field_181165_f;
    private static final float radFull = (float)Math.PI * 2;

    public static float sqrt_float(float f) {
        return (float)Math.sqrt(f);
    }

    private static int calculateLogBaseTwoDeBruijn(int n) {
        n = MathHelper.isPowerOfTwo(n) ? n : MathHelper.roundUpToPowerOfTwo(n);
        return multiplyDeBruijnBitPosition[(int)((long)n * 125613361L >> 27) & 0x1F];
    }

    public static int bucketInt(int n, int n2) {
        return n < 0 ? -((-n - 1) / n2) - 1 : n / n2;
    }

    public static int func_180188_d(int n, int n2) {
        int n3 = (n & 0xFF0000) >> 16;
        int n4 = (n2 & 0xFF0000) >> 16;
        int n5 = (n & 0xFF00) >> 8;
        int n6 = (n2 & 0xFF00) >> 8;
        int n7 = (n & 0xFF) >> 0;
        int n8 = (n2 & 0xFF) >> 0;
        int n9 = (int)((float)(n3 * n4) / 255.0f);
        int n10 = (int)((float)(n5 * n6) / 255.0f);
        int n11 = (int)((float)(n7 * n8) / 255.0f);
        return n & 0xFF000000 | n9 << 16 | n10 << 8 | n11;
    }

    public static int ceiling_float_int(float f) {
        int n = (int)f;
        return f > (float)n ? n + 1 : n;
    }

    public static int abs_int(int n) {
        return n >= 0 ? n : -n;
    }

    public static double clamp_double(double d, double d2, double d3) {
        return d < d2 ? d2 : (d > d3 ? d3 : d);
    }

    public static int ceiling_double_int(double d) {
        int n = (int)d;
        return d > (double)n ? n + 1 : n;
    }

    private static boolean isPowerOfTwo(int n) {
        return n != 0 && (n & n - 1) == 0;
    }

    public static float wrapAngleTo180_float(float f) {
        if ((f %= 360.0f) >= 180.0f) {
            f -= 360.0f;
        }
        if (f < -180.0f) {
            f += 360.0f;
        }
        return f;
    }

    public static float sin(float f) {
        return fastMath ? SIN_TABLE_FAST[(int)(f * 651.8986f) & 0xFFF] : SIN_TABLE[(int)(f * 10430.378f) & 0xFFFF];
    }

    public static int getRandomIntegerInRange(Random random, int n, int n2) {
        return n >= n2 ? n : random.nextInt(n2 - n + 1) + n;
    }

    public static double func_181161_i(double d) {
        double d2 = 0.5 * d;
        long l = Double.doubleToRawLongBits(d);
        l = 6910469410427058090L - (l >> 1);
        d = Double.longBitsToDouble(l);
        d *= 1.5 - d2 * d * d;
        return d;
    }

    public static float randomFloatClamp(Random random, float f, float f2) {
        return f >= f2 ? f : random.nextFloat() * (f2 - f) + f;
    }

    public static int parseIntWithDefault(String string, int n) {
        try {
            return Integer.parseInt(string);
        }
        catch (Throwable throwable) {
            return n;
        }
    }

    public static int func_180181_b(int n, int n2, int n3) {
        int n4 = (n << 8) + n2;
        n4 = (n4 << 8) + n3;
        return n4;
    }

    public static float abs(float f) {
        return f >= 0.0f ? f : -f;
    }

    public static int func_154353_e(double d) {
        return (int)(d >= 0.0 ? d : -d + 1.0);
    }

    public static int func_180184_b(int n, int n2) {
        return (n % n2 + n2) % n2;
    }

    public static double parseDoubleWithDefault(String string, double d) {
        try {
            return Double.parseDouble(string);
        }
        catch (Throwable throwable) {
            return d;
        }
    }

    public static double parseDoubleWithDefaultAndMax(String string, double d, double d2) {
        return Math.max(d2, MathHelper.parseDoubleWithDefault(string, d));
    }

    public static int roundUpToPowerOfTwo(int n) {
        int n2 = n - 1;
        n2 |= n2 >> 1;
        n2 |= n2 >> 2;
        n2 |= n2 >> 4;
        n2 |= n2 >> 8;
        n2 |= n2 >> 16;
        return n2 + 1;
    }

    public static int calculateLogBaseTwo(int n) {
        return MathHelper.calculateLogBaseTwoDeBruijn(n) - (MathHelper.isPowerOfTwo(n) ? 0 : 1);
    }

    public static long floor_double_long(double d) {
        long l = (long)d;
        return d < (double)l ? l - 1L : l;
    }

    public static float cos(float f) {
        return fastMath ? SIN_TABLE_FAST[(int)((f + 1.5707964f) * 651.8986f) & 0xFFF] : SIN_TABLE[(int)(f * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static int truncateDoubleToInt(double d) {
        return (int)(d + 1024.0) - 1024;
    }

    public static int clamp_int(int n, int n2, int n3) {
        return n < n2 ? n2 : (n > n3 ? n3 : n);
    }

    public static float clamp_float(float f, float f2, float f3) {
        return f < f2 ? f2 : (f > f3 ? f3 : f);
    }

    public static int floor_float(float f) {
        int n = (int)f;
        return f < (float)n ? n - 1 : n;
    }

    public static double square(double d) {
        return d * d;
    }

    public static double average(long[] arrl) {
        long l = 0L;
        for (long l2 : arrl) {
            l += l2;
        }
        return l / (long)arrl.length;
    }

    public static double abs_max(double d, double d2) {
        if (d < 0.0) {
            d = -d;
        }
        if (d2 < 0.0) {
            d2 = -d2;
        }
        return d > d2 ? d : d2;
    }

    static {
        int n;
        SIN_BITS = 12;
        SIN_COUNT = 4096;
        __OBFID = "CL_00001496";
        SIN_MASK = 4095;
        field_180189_a = MathHelper.sqrt_float(2.0f);
        SIN_TABLE_FAST = new float[4096];
        fastMath = false;
        SIN_TABLE = new float[65536];
        field_181163_d = Double.longBitsToDouble(4805340802404319232L);
        field_181164_e = new double[257];
        field_181165_f = new double[257];
        for (n = 0; n < 65536; ++n) {
            MathHelper.SIN_TABLE[n] = (float)Math.sin((double)n * Math.PI * 2.0 / 65536.0);
        }
        multiplyDeBruijnBitPosition = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
        for (n = 0; n < 4096; ++n) {
            MathHelper.SIN_TABLE_FAST[n] = (float)Math.sin(((float)n + 0.5f) / 4096.0f * ((float)Math.PI * 2));
        }
        for (n = 0; n < 360; n += 90) {
            MathHelper.SIN_TABLE_FAST[(int)((float)n * 11.377778f) & 4095] = (float)Math.sin((float)n * ((float)Math.PI / 180));
        }
    }

    public static boolean func_180185_a(float f, float f2) {
        return MathHelper.abs(f2 - f) < 1.0E-5f;
    }

    public static double getRandomDoubleInRange(Random random, double d, double d2) {
        return d >= d2 ? d : random.nextDouble() * (d2 - d) + d;
    }

    public static int parseIntWithDefaultAndMax(String string, int n, int n2) {
        return Math.max(n2, MathHelper.parseIntWithDefault(string, n));
    }

    public static UUID func_180182_a(Random random) {
        long l = random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
        long l2 = random.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID(l, l2);
    }

    public static float sqrt_double(double d) {
        return (float)Math.sqrt(d);
    }

    public static long func_180187_c(int n, int n2, int n3) {
        long l = (long)(n * 3129871) ^ (long)n3 * 116129781L ^ (long)n2;
        l = l * l * 42317861L + l * 11L;
        return l;
    }

    public static double atan2(double d, double d2) {
        double d3;
        boolean bl;
        boolean bl2;
        boolean bl3;
        double d4 = d2 * d2 + d * d;
        if (Double.isNaN(d4)) {
            return Double.NaN;
        }
        boolean bl4 = bl3 = d < 0.0;
        if (bl3) {
            d = -d;
        }
        boolean bl5 = bl2 = d2 < 0.0;
        if (bl2) {
            d2 = -d2;
        }
        boolean bl6 = bl = d > d2;
        if (bl) {
            d3 = d2;
            d2 = d;
            d = d3;
        }
        d3 = MathHelper.func_181161_i(d4);
        double d5 = field_181163_d + (d *= d3);
        int n = (int)Double.doubleToRawLongBits(d5);
        double d6 = field_181164_e[n];
        double d7 = field_181165_f[n];
        double d8 = d5 - field_181163_d;
        double d9 = d * d7 - (d2 *= d3) * d8;
        double d10 = (6.0 + d9 * d9) * d9 * 0.16666666666666666;
        double d11 = d6 + d10;
        if (bl) {
            d11 = 1.5707963267948966 - d11;
        }
        if (bl2) {
            d11 = Math.PI - d11;
        }
        if (bl3) {
            d11 = -d11;
        }
        return d11;
    }

    public static int func_154354_b(int n, int n2) {
        int n3;
        if (n2 == 0) {
            return 0;
        }
        if (n == 0) {
            return n2;
        }
        if (n < 0) {
            n2 *= -1;
        }
        return (n3 = n % n2) == 0 ? n : n + n2 - n3;
    }

    public static int floor_double(double d) {
        int n = (int)d;
        return d < (double)n ? n - 1 : n;
    }

    public static double wrapAngleTo180_double(double d) {
        if ((d %= 360.0) >= 180.0) {
            d -= 360.0;
        }
        if (d < -180.0) {
            d += 360.0;
        }
        return d;
    }

    public static int func_180183_b(float f, float f2, float f3) {
        return MathHelper.func_180181_b(MathHelper.floor_float(f * 255.0f), MathHelper.floor_float(f2 * 255.0f), MathHelper.floor_float(f3 * 255.0f));
    }

    public static double denormalizeClamp(double d, double d2, double d3) {
        return d3 < 0.0 ? d : (d3 > 1.0 ? d2 : d + (d2 - d) * d3);
    }
}