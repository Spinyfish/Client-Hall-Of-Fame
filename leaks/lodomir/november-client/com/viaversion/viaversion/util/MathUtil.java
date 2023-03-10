/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.util;

public final class MathUtil {
    public static int ceilLog2(int i) {
        return i > 0 ? 32 - Integer.numberOfLeadingZeros(i - 1) : 0;
    }

    public static int clamp(int i, int min, int max) {
        if (i < min) {
            return min;
        }
        return i > max ? max : i;
    }
}

