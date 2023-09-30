/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.gen.layer;

import com.google.common.collect.Lists;
import java.util.List;

public class IntCache {
    private static int intCacheSize = 256;
    private static List<int[]> freeSmallArrays = Lists.newArrayList();
    private static List<int[]> inUseSmallArrays = Lists.newArrayList();
    private static List<int[]> freeLargeArrays = Lists.newArrayList();
    private static List<int[]> inUseLargeArrays = Lists.newArrayList();

    public static synchronized int[] getIntCache(int p_76445_0_) {
        if (p_76445_0_ <= 256) {
            if (freeSmallArrays.isEmpty()) {
                int[] aint4 = new int[256];
                inUseSmallArrays.add(aint4);
                return aint4;
            }
            int[] aint3 = freeSmallArrays.remove(freeSmallArrays.size() - 1);
            inUseSmallArrays.add(aint3);
            return aint3;
        }
        if (p_76445_0_ > intCacheSize) {
            intCacheSize = p_76445_0_;
            freeLargeArrays.clear();
            inUseLargeArrays.clear();
            int[] aint2 = new int[intCacheSize];
            inUseLargeArrays.add(aint2);
            return aint2;
        }
        if (freeLargeArrays.isEmpty()) {
            int[] aint1 = new int[intCacheSize];
            inUseLargeArrays.add(aint1);
            return aint1;
        }
        int[] aint = freeLargeArrays.remove(freeLargeArrays.size() - 1);
        inUseLargeArrays.add(aint);
        return aint;
    }

    public static synchronized void resetIntCache() {
        if (!freeLargeArrays.isEmpty()) {
            freeLargeArrays.remove(freeLargeArrays.size() - 1);
        }
        if (!freeSmallArrays.isEmpty()) {
            freeSmallArrays.remove(freeSmallArrays.size() - 1);
        }
        freeLargeArrays.addAll(inUseLargeArrays);
        freeSmallArrays.addAll(inUseSmallArrays);
        inUseLargeArrays.clear();
        inUseSmallArrays.clear();
    }

    public static synchronized String getCacheSizes() {
        return "cache: " + freeLargeArrays.size() + ", tcache: " + freeSmallArrays.size() + ", allocated: " + inUseLargeArrays.size() + ", tallocated: " + inUseSmallArrays.size();
    }
}

