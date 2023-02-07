
package org.crystalpvp.anticheat.api.util;

import java.util.Random;
import java.util.UUID;

public class MathHelper {
    public static final float field_180189_a;
    public static final float PI = 3.1415927f;
    public static final float PI2 = 6.2831855f;
    public static final float PId2 = 1.5707964f;
    public static final float deg2Rad = 0.017453292f;
    private static final float[] SIN_TABLE_FAST;
    public static boolean fastMath;
    private static final float[] SIN_TABLE;
    private static final int[] multiplyDeBruijnBitPosition;
    private static final double field_181163_d;
    private static final double[] field_181164_e;
    private static final double[] field_181165_f;
    public static float sin(float p_76126_0_) {
        return fastMath ? SIN_TABLE_FAST[(int)(p_76126_0_ * 651.8986f) & 4095] : SIN_TABLE[(int)(p_76126_0_ * 10430.378f) & 65535];
    }

    public static float cos(float p_76134_0_) {
        return fastMath ? SIN_TABLE_FAST[(int)((p_76134_0_ + 1.5707964f) * 651.8986f) & 4095] : SIN_TABLE[(int)(p_76134_0_ * 10430.378f + 16384.0f) & 65535];
    }

    public static float sqrt_float(float p_76129_0_) {
        return (float)Math.sqrt(p_76129_0_);
    }

    public static float sqrt_double(double p_76133_0_) {
        return (float)Math.sqrt(p_76133_0_);
    }

    public static int floor_float(float p_76141_0_) {
        int var1 = (int)p_76141_0_;
        return p_76141_0_ < (float)var1 ? var1 - 1 : var1;
    }

    public static int truncateDoubleToInt(double p_76140_0_) {
        return (int)(p_76140_0_ + 1024.0) - 1024;
    }

    public static int floor_double(double p_76128_0_) {
        int var2 = (int)p_76128_0_;
        return p_76128_0_ < (double)var2 ? var2 - 1 : var2;
    }

    public static long floor_double_long(double p_76124_0_) {
        long var2 = (long)p_76124_0_;
        return p_76124_0_ < (double)var2 ? var2 - 1L : var2;
    }

    public static int func_154353_e(double p_154353_0_) {
        return (int)(p_154353_0_ >= 0.0 ? p_154353_0_ : - p_154353_0_ + 1.0);
    }

    public static float abs(float p_76135_0_) {
        return p_76135_0_ >= 0.0f ? p_76135_0_ : - p_76135_0_;
    }

    public static int abs_int(int p_76130_0_) {
        return p_76130_0_ >= 0 ? p_76130_0_ : - p_76130_0_;
    }

    public static int ceiling_float_int(float p_76123_0_) {
        int var1 = (int)p_76123_0_;
        return p_76123_0_ > (float)var1 ? var1 + 1 : var1;
    }

    public static int ceiling_double_int(double p_76143_0_) {
        int var2 = (int)p_76143_0_;
        return p_76143_0_ > (double)var2 ? var2 + 1 : var2;
    }

    public static int clamp_int(int p_76125_0_, int p_76125_1_, int p_76125_2_) {
        return p_76125_0_ < p_76125_1_ ? p_76125_1_ : (p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_);
    }

    public static float clamp_float(float p_76131_0_, float p_76131_1_, float p_76131_2_) {
        return p_76131_0_ < p_76131_1_ ? p_76131_1_ : (p_76131_0_ > p_76131_2_ ? p_76131_2_ : p_76131_0_);
    }

    public static double clamp_double(double p_151237_0_, double p_151237_2_, double p_151237_4_) {
        return p_151237_0_ < p_151237_2_ ? p_151237_2_ : (p_151237_0_ > p_151237_4_ ? p_151237_4_ : p_151237_0_);
    }

    public static double denormalizeClamp(double p_151238_0_, double p_151238_2_, double p_151238_4_) {
        return p_151238_4_ < 0.0 ? p_151238_0_ : (p_151238_4_ > 1.0 ? p_151238_2_ : p_151238_0_ + (p_151238_2_ - p_151238_0_) * p_151238_4_);
    }

    public static double abs_max(double p_76132_0_, double p_76132_2_) {
        if (p_76132_0_ < 0.0) {
            p_76132_0_ = - p_76132_0_;
        }
        if (p_76132_2_ < 0.0) {
            p_76132_2_ = - p_76132_2_;
        }
        return p_76132_0_ > p_76132_2_ ? p_76132_0_ : p_76132_2_;
    }

    public static int bucketInt(int p_76137_0_, int p_76137_1_) {
        return p_76137_0_ < 0 ? - (- p_76137_0_ - 1) / p_76137_1_ - 1 : p_76137_0_ / p_76137_1_;
    }

    public static int getRandomIntegerInRange(Random p_76136_0_, int p_76136_1_, int p_76136_2_) {
        return p_76136_1_ >= p_76136_2_ ? p_76136_1_ : p_76136_0_.nextInt(p_76136_2_ - p_76136_1_ + 1) + p_76136_1_;
    }

    public static float randomFloatClamp(Random p_151240_0_, float p_151240_1_, float p_151240_2_) {
        return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
    }

    public static double getRandomDoubleInRange(Random p_82716_0_, double p_82716_1_, double p_82716_3_) {
        return p_82716_1_ >= p_82716_3_ ? p_82716_1_ : p_82716_0_.nextDouble() * (p_82716_3_ - p_82716_1_) + p_82716_1_;
    }

    public static double average(long[] p_76127_0_) {
        long var1 = 0L;
        long[] var3 = p_76127_0_;
        int var4 = p_76127_0_.length;
        for (int var5 = 0; var5 < var4; ++var5) {
            long var6 = var3[var5];
            var1 += var6;
        }
        return var1 / (long)p_76127_0_.length;
    }

    public static boolean func_180185_a(float p_180185_0_, float p_180185_1_) {
        return MathHelper.abs(p_180185_1_ - p_180185_0_) < 1.0E-5f;
    }

    public static int func_180184_b(int p_180184_0_, int p_180184_1_) {
        return (p_180184_0_ % p_180184_1_ + p_180184_1_) % p_180184_1_;
    }

    public static float wrapAngleTo180_float(float p_76142_0_) {
        if ((p_76142_0_ %= 360.0f) >= 180.0f) {
            p_76142_0_ -= 360.0f;
        }
        if (p_76142_0_ < -180.0f) {
            p_76142_0_ += 360.0f;
        }
        return p_76142_0_;
    }

    public static double wrapAngleTo180_double(double p_76138_0_) {
        if ((p_76138_0_ %= 360.0) >= 180.0) {
            p_76138_0_ -= 360.0;
        }
        if (p_76138_0_ < -180.0) {
            p_76138_0_ += 360.0;
        }
        return p_76138_0_;
    }

    public static int parseIntWithDefault(String p_82715_0_, int p_82715_1_) {
        try {
            return Integer.parseInt(p_82715_0_);
        }
        catch (Throwable var3) {
            return p_82715_1_;
        }
    }

    public static int parseIntWithDefaultAndMax(String p_82714_0_, int p_82714_1_, int p_82714_2_) {
        return Math.max(p_82714_2_, MathHelper.parseIntWithDefault(p_82714_0_, p_82714_1_));
    }

    public static double parseDoubleWithDefault(String p_82712_0_, double p_82712_1_) {
        try {
            return Double.parseDouble(p_82712_0_);
        }
        catch (Throwable var4) {
            return p_82712_1_;
        }
    }

    public static double parseDoubleWithDefaultAndMax(String p_82713_0_, double p_82713_1_, double p_82713_3_) {
        return Math.max(p_82713_3_, MathHelper.parseDoubleWithDefault(p_82713_0_, p_82713_1_));
    }

    public static int roundUpToPowerOfTwo(int p_151236_0_) {
        int var1 = p_151236_0_ - 1;
        var1 |= var1 >> 1;
        var1 |= var1 >> 2;
        var1 |= var1 >> 4;
        var1 |= var1 >> 8;
        var1 |= var1 >> 16;
        return var1 + 1;
    }

    private static boolean isPowerOfTwo(int p_151235_0_) {
        return p_151235_0_ != 0 && (p_151235_0_ & p_151235_0_ - 1) == 0;
    }

    private static int calculateLogBaseTwoDeBruijn(int p_151241_0_) {
        p_151241_0_ = MathHelper.isPowerOfTwo(p_151241_0_) ? p_151241_0_ : MathHelper.roundUpToPowerOfTwo(p_151241_0_);
        return multiplyDeBruijnBitPosition[(int)((long)p_151241_0_ * 125613361L >> 27) & 31];
    }

    public static int calculateLogBaseTwo(int p_151239_0_) {
        return MathHelper.calculateLogBaseTwoDeBruijn(p_151239_0_) - (MathHelper.isPowerOfTwo(p_151239_0_) ? 0 : 1);
    }

    public static int func_154354_b(int p_154354_0_, int p_154354_1_) {
        int var2;
        if (p_154354_1_ == 0) {
            return 0;
        }
        if (p_154354_0_ == 0) {
            return p_154354_1_;
        }
        if (p_154354_0_ < 0) {
            p_154354_1_ *= -1;
        }
        return (var2 = p_154354_0_ % p_154354_1_) == 0 ? p_154354_0_ : p_154354_0_ + p_154354_1_ - var2;
    }

    public static int func_180183_b(float p_180183_0_, float p_180183_1_, float p_180183_2_) {
        return MathHelper.func_180181_b(MathHelper.floor_float(p_180183_0_ * 255.0f), MathHelper.floor_float(p_180183_1_ * 255.0f), MathHelper.floor_float(p_180183_2_ * 255.0f));
    }

    public static int func_180181_b(int p_180181_0_, int p_180181_1_, int p_180181_2_) {
        int var3 = (p_180181_0_ << 8) + p_180181_1_;
        var3 = (var3 << 8) + p_180181_2_;
        return var3;
    }

    public static int func_180188_d(int p_180188_0_, int p_180188_1_) {
        int var2 = (p_180188_0_ & 16711680) >> 16;
        int var3 = (p_180188_1_ & 16711680) >> 16;
        int var4 = (p_180188_0_ & 65280) >> 8;
        int var5 = (p_180188_1_ & 65280) >> 8;
        int var6 = (p_180188_0_ & 255) >> 0;
        int var7 = (p_180188_1_ & 255) >> 0;
        int var8 = (int)((float)(var2 * var3) / 255.0f);
        int var9 = (int)((float)(var4 * var5) / 255.0f);
        int var10 = (int)((float)(var6 * var7) / 255.0f);
        return p_180188_0_ & -16777216 | var8 << 16 | var9 << 8 | var10;
    }

    public static long func_180187_c(int x, int y, int z) {
        long var3 = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
        var3 = var3 * var3 * 42317861L + var3 * 11L;
        return var3;
    }

    public static UUID func_180182_a(Random p_180182_0_) {
        long var1 = p_180182_0_.nextLong() & -61441L | 16384L;
        long var2 = p_180182_0_.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID(var1, var2);
    }

    public static double square(double in) {
        return in * in;
    }

    public static double atan2(double p_181159_0_, double p_181159_2_) {
        boolean flag3;
        double d3;
        boolean flag;
        boolean flag2;
        double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
        if (Double.isNaN(d0)) {
            return Double.NaN;
        }
        flag = p_181159_0_ < 0.0;
        if (flag) {
            p_181159_0_ = - p_181159_0_;
        }
        flag2 = p_181159_2_ < 0.0;
        if (flag2) {
            p_181159_2_ = - p_181159_2_;
        }
        flag3 = p_181159_0_ > p_181159_2_;
        if (flag3) {
            d3 = p_181159_2_;
            p_181159_2_ = p_181159_0_;
            p_181159_0_ = d3;
        }
        d3 = MathHelper.func_181161_i(d0);
        double d4 = field_181163_d + (p_181159_0_ *= d3);
        int i = (int)Double.doubleToRawLongBits(d4);
        double d5 = field_181164_e[i];
        double d6 = field_181165_f[i];
        double d7 = d4 - field_181163_d;
        double d8 = p_181159_0_ * d6 - (p_181159_2_ *= d3) * d7;
        double d9 = (6.0 + d8 * d8) * d8 * 0.16666666666666666;
        double d10 = d5 + d9;
        if (flag3) {
            d10 = 1.5707963267948966 - d10;
        }
        if (flag2) {
            d10 = 3.141592653589793 - d10;
        }
        if (flag) {
            d10 = - d10;
        }
        return d10;
    }

    public static double func_181161_i(double p_181161_0_) {
        double d0 = 0.5 * p_181161_0_;
        long i = Double.doubleToRawLongBits(p_181161_0_);
        i = 6910469410427058090L - (i >> 1);
        p_181161_0_ = Double.longBitsToDouble(i);
        p_181161_0_ *= 1.5 - d0 * p_181161_0_ * p_181161_0_;
        return p_181161_0_;
    }

    static {
        int i;
        field_180189_a = MathHelper.sqrt_float(2.0f);
        SIN_TABLE_FAST = new float[4096];
        fastMath = false;
        SIN_TABLE = new float[65536];
        field_181163_d = Double.longBitsToDouble(4805340802404319232L);
        field_181164_e = new double[257];
        field_181165_f = new double[257];
        for (i = 0; i < 65536; ++i) {
            MathHelper.SIN_TABLE[i] = (float)Math.sin((double)i * 3.141592653589793 * 2.0 / 65536.0);
        }
        multiplyDeBruijnBitPosition = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
        for (i = 0; i < 4096; ++i) {
            MathHelper.SIN_TABLE_FAST[i] = (float)Math.sin(((float)i + 0.5f) / 4096.0f * 6.2831855f);
        }
        for (i = 0; i < 360; i += 90) {
            MathHelper.SIN_TABLE_FAST[(int)((float)i * 11.377778f) & 4095] = (float)Math.sin((float)i * 0.017453292f);
        }
    }
}
