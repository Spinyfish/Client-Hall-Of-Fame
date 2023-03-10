/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.util;

import com.viaversion.viaversion.util.BiIntConsumer;
import java.util.function.IntToLongFunction;

public class CompactArrayUtil {
    private static final int[] MAGIC = new int[]{-1, -1, 0, Integer.MIN_VALUE, 0, 0, 0x55555555, 0x55555555, 0, Integer.MIN_VALUE, 0, 1, 0x33333333, 0x33333333, 0, 0x2AAAAAAA, 0x2AAAAAAA, 0, 0x24924924, 0x24924924, 0, Integer.MIN_VALUE, 0, 2, 0x1C71C71C, 0x1C71C71C, 0, 0x19999999, 0x19999999, 0, 390451572, 390451572, 0, 0x15555555, 0x15555555, 0, 0x13B13B13, 0x13B13B13, 0, 306783378, 306783378, 0, 0x11111111, 0x11111111, 0, Integer.MIN_VALUE, 0, 3, 0xF0F0F0F, 0xF0F0F0F, 0, 0xE38E38E, 0xE38E38E, 0, 226050910, 226050910, 0, 0xCCCCCCC, 0xCCCCCCC, 0, 0xC30C30C, 0xC30C30C, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 0xAAAAAAA, 0xAAAAAAA, 0, 171798691, 171798691, 0, 0x9D89D89, 0x9D89D89, 0, 159072862, 159072862, 0, 0x9249249, 0x9249249, 0, 148102320, 148102320, 0, 0x8888888, 0x8888888, 0, 138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 0x7878787, 0x7878787, 0, 0x7507507, 0x7507507, 0, 0x71C71C7, 0x71C71C7, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 0x6906906, 0x6906906, 0, 0x6666666, 0x6666666, 0, 104755299, 104755299, 0, 0x6186186, 0x6186186, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 0x5B05B05, 0x5B05B05, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 0x5555555, 0x5555555, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 0x5050505, 0x5050505, 0, 0x4EC4EC4, 0x4EC4EC4, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 0x4924924, 0x4924924, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 0x4444444, 0x4444444, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 0x4104104, 0x4104104, 0, Integer.MIN_VALUE, 0, 5};

    private CompactArrayUtil() {
        throw new AssertionError();
    }

    public static long[] createCompactArrayWithPadding(int bitsPerEntry, int entries, IntToLongFunction valueGetter) {
        long maxEntryValue = (1L << bitsPerEntry) - 1L;
        char valuesPerLong = (char)(64 / bitsPerEntry);
        int magicIndex = 3 * (valuesPerLong - '\u0001');
        long divideMul = Integer.toUnsignedLong(MAGIC[magicIndex]);
        long divideAdd = Integer.toUnsignedLong(MAGIC[magicIndex + 1]);
        int divideShift = MAGIC[magicIndex + 2];
        int size = (entries + valuesPerLong - 1) / valuesPerLong;
        long[] data = new long[size];
        for (int i = 0; i < entries; ++i) {
            long value = valueGetter.applyAsLong(i);
            int cellIndex = (int)((long)i * divideMul + divideAdd >> 32 >> divideShift);
            int bitIndex = (i - cellIndex * valuesPerLong) * bitsPerEntry;
            data[cellIndex] = data[cellIndex] & (maxEntryValue << bitIndex ^ 0xFFFFFFFFFFFFFFFFL) | (value & maxEntryValue) << bitIndex;
        }
        return data;
    }

    public static void iterateCompactArrayWithPadding(int bitsPerEntry, int entries, long[] data, BiIntConsumer consumer) {
        long maxEntryValue = (1L << bitsPerEntry) - 1L;
        char valuesPerLong = (char)(64 / bitsPerEntry);
        int magicIndex = 3 * (valuesPerLong - '\u0001');
        long divideMul = Integer.toUnsignedLong(MAGIC[magicIndex]);
        long divideAdd = Integer.toUnsignedLong(MAGIC[magicIndex + 1]);
        int divideShift = MAGIC[magicIndex + 2];
        for (int i = 0; i < entries; ++i) {
            int cellIndex = (int)((long)i * divideMul + divideAdd >> 32 >> divideShift);
            int bitIndex = (i - cellIndex * valuesPerLong) * bitsPerEntry;
            int value = (int)(data[cellIndex] >> bitIndex & maxEntryValue);
            consumer.consume(i, value);
        }
    }

    public static long[] createCompactArray(int bitsPerEntry, int entries, IntToLongFunction valueGetter) {
        long maxEntryValue = (1L << bitsPerEntry) - 1L;
        long[] data = new long[(int)Math.ceil((double)(entries * bitsPerEntry) / 64.0)];
        for (int i = 0; i < entries; ++i) {
            long value = valueGetter.applyAsLong(i);
            int bitIndex = i * bitsPerEntry;
            int startIndex = bitIndex / 64;
            int endIndex = ((i + 1) * bitsPerEntry - 1) / 64;
            int startBitSubIndex = bitIndex % 64;
            data[startIndex] = data[startIndex] & (maxEntryValue << startBitSubIndex ^ 0xFFFFFFFFFFFFFFFFL) | (value & maxEntryValue) << startBitSubIndex;
            if (startIndex == endIndex) continue;
            int endBitSubIndex = 64 - startBitSubIndex;
            data[endIndex] = data[endIndex] >>> endBitSubIndex << endBitSubIndex | (value & maxEntryValue) >> endBitSubIndex;
        }
        return data;
    }

    public static void iterateCompactArray(int bitsPerEntry, int entries, long[] data, BiIntConsumer consumer) {
        long maxEntryValue = (1L << bitsPerEntry) - 1L;
        for (int i = 0; i < entries; ++i) {
            int value;
            int bitIndex = i * bitsPerEntry;
            int startIndex = bitIndex / 64;
            int endIndex = ((i + 1) * bitsPerEntry - 1) / 64;
            int startBitSubIndex = bitIndex % 64;
            if (startIndex == endIndex) {
                value = (int)(data[startIndex] >>> startBitSubIndex & maxEntryValue);
            } else {
                int endBitSubIndex = 64 - startBitSubIndex;
                value = (int)((data[startIndex] >>> startBitSubIndex | data[endIndex] << endBitSubIndex) & maxEntryValue);
            }
            consumer.consume(i, value);
        }
    }
}

