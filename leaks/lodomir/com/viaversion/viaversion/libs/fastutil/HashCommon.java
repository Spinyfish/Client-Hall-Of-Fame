/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

public class HashCommon {
    private static final int INT_PHI = -1640531527;
    private static final int INV_INT_PHI = 340573321;
    private static final long LONG_PHI = -7046029254386353131L;
    private static final long INV_LONG_PHI = -1018231460777725123L;

    protected HashCommon() {
    }

    public static int murmurHash3(int x) {
        x ^= x >>> 16;
        x *= -2048144789;
        x ^= x >>> 13;
        x *= -1028477387;
        x ^= x >>> 16;
        return x;
    }

    public static long murmurHash3(long x) {
        x ^= x >>> 33;
        x *= -49064778989728563L;
        x ^= x >>> 33;
        x *= -4265267296055464877L;
        x ^= x >>> 33;
        return x;
    }

    public static int mix(int x) {
        int h = x * -1640531527;
        return h ^ h >>> 16;
    }

    public static int invMix(int x) {
        return (x ^ x >>> 16) * 340573321;
    }

    public static long mix(long x) {
        long h = x * -7046029254386353131L;
        h ^= h >>> 32;
        return h ^ h >>> 16;
    }

    public static long invMix(long x) {
        x ^= x >>> 32;
        x ^= x >>> 16;
        return (x ^ x >>> 32) * -1018231460777725123L;
    }

    public static int float2int(float f) {
        return Float.floatToRawIntBits(f);
    }

    public static int double2int(double d) {
        long l = Double.doubleToRawLongBits(d);
        return (int)(l ^ l >>> 32);
    }

    public static int long2int(long l) {
        return (int)(l ^ l >>> 32);
    }

    public static int nextPowerOfTwo(int x) {
        if (x == 0) {
            return 1;
        }
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        return (x | x >> 16) + 1;
    }

    public static long nextPowerOfTwo(long x) {
        if (x == 0L) {
            return 1L;
        }
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return (x | x >> 32) + 1L;
    }

    public static int maxFill(int n, float f) {
        return Math.min((int)Math.ceil((float)n * f), n - 1);
    }

    public static long maxFill(long n, float f) {
        return Math.min((long)Math.ceil((float)n * f), n - 1L);
    }

    public static int arraySize(int expected, float f) {
        long s = Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)expected / f)));
        if (s > 0x40000000L) {
            throw new IllegalArgumentException("Too large (" + expected + " expected elements with load factor " + f + ")");
        }
        return (int)s;
    }

    public static long bigArraySize(long expected, float f) {
        return HashCommon.nextPowerOfTwo((long)Math.ceil((float)expected / f));
    }
}

