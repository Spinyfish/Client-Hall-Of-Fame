package net.optifine.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;

import java.lang.reflect.Array;
import java.util.ArrayDeque;

public class CacheObjectArray {
    private static final ArrayDeque<int[]> arrays = new ArrayDeque();
    private static final int maxCacheSize = 10;

    private static synchronized int[] allocateArray(final int size) {
        int[] aint = arrays.pollLast();

        if (aint == null || aint.length < size) {
            aint = new int[size];
        }

        return aint;
    }

    public static synchronized void freeArray(final int[] ints) {
        if (arrays.size() < maxCacheSize) {
            arrays.add(ints);
        }
    }

    public static void main(final String[] args) throws Exception {
        final int i = 4096;
        final int j = 500000;
        testNew(i, j);
        testClone(i, j);
        testNewObj(i, j);
        testCloneObj(i, j);
        testNewObjDyn(IBlockState.class, i, j);
        final long k = testNew(i, j);
        final long l = testClone(i, j);
        final long i1 = testNewObj(i, j);
        final long j1 = testCloneObj(i, j);
        final long k1 = testNewObjDyn(IBlockState.class, i, j);
        Config.dbg("New: " + k);
        Config.dbg("Clone: " + l);
        Config.dbg("NewObj: " + i1);
        Config.dbg("CloneObj: " + j1);
        Config.dbg("NewObjDyn: " + k1);
    }

    private static long testClone(final int size, final int count) {
        final long i = System.currentTimeMillis();
        final int[] aint = new int[size];

        for (int j = 0; j < count; ++j) {
            final int[] aint1 = aint.clone();
        }

        final long k = System.currentTimeMillis();
        return k - i;
    }

    private static long testNew(final int size, final int count) {
        final long i = System.currentTimeMillis();

        for (int j = 0; j < count; ++j) {
            final int[] aint = (int[]) Array.newInstance(Integer.TYPE, size);
        }

        final long k = System.currentTimeMillis();
        return k - i;
    }

    private static long testCloneObj(final int size, final int count) {
        final long i = System.currentTimeMillis();
        final IBlockState[] aiblockstate = new IBlockState[size];

        for (int j = 0; j < count; ++j) {
            final IBlockState[] aiblockstate1 = aiblockstate.clone();
        }

        final long k = System.currentTimeMillis();
        return k - i;
    }

    private static long testNewObj(final int size, final int count) {
        final long i = System.currentTimeMillis();

        for (int j = 0; j < count; ++j) {
            final IBlockState[] aiblockstate = new IBlockState[size];
        }

        final long k = System.currentTimeMillis();
        return k - i;
    }

    private static long testNewObjDyn(final Class cls, final int size, final int count) {
        final long i = System.currentTimeMillis();

        for (int j = 0; j < count; ++j) {
            final Object[] aobject = (Object[]) Array.newInstance(cls, size);
        }

        final long k = System.currentTimeMillis();
        return k - i;
    }
}
