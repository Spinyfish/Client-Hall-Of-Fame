/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import java.util.ArrayList;

public class AdaptiveRecvByteBufAllocator
implements RecvByteBufAllocator {
    static final int DEFAULT_MINIMUM = 64;
    static final int DEFAULT_INITIAL = 1024;
    static final int DEFAULT_MAXIMUM = 65536;
    private static final int INDEX_INCREMENT = 4;
    private static final int INDEX_DECREMENT = 1;
    private static final int[] SIZE_TABLE;
    public static final AdaptiveRecvByteBufAllocator DEFAULT;
    private final int minIndex;
    private final int maxIndex;
    private final int initial;

    private static int getSizeTableIndex(int size) {
        int a;
        int mid;
        int low = 0;
        int high = SIZE_TABLE.length - 1;
        while (true) {
            if (high < low) {
                return low;
            }
            if (high == low) {
                return high;
            }
            mid = low + high >>> 1;
            a = SIZE_TABLE[mid];
            int b = SIZE_TABLE[mid + 1];
            if (size > b) {
                low = mid + 1;
                continue;
            }
            if (size >= a) break;
            high = mid - 1;
        }
        if (size == a) {
            return mid;
        }
        return mid + 1;
    }

    private AdaptiveRecvByteBufAllocator() {
        this(64, 1024, 65536);
    }

    public AdaptiveRecvByteBufAllocator(int minimum, int initial, int maximum) {
        if (minimum <= 0) {
            throw new IllegalArgumentException("minimum: " + minimum);
        }
        if (initial < minimum) {
            throw new IllegalArgumentException("initial: " + initial);
        }
        if (maximum < initial) {
            throw new IllegalArgumentException("maximum: " + maximum);
        }
        int minIndex = AdaptiveRecvByteBufAllocator.getSizeTableIndex(minimum);
        this.minIndex = SIZE_TABLE[minIndex] < minimum ? minIndex + 1 : minIndex;
        int maxIndex = AdaptiveRecvByteBufAllocator.getSizeTableIndex(maximum);
        this.maxIndex = SIZE_TABLE[maxIndex] > maximum ? maxIndex - 1 : maxIndex;
        this.initial = initial;
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl(this.minIndex, this.maxIndex, this.initial);
    }

    static {
        int i;
        ArrayList<Integer> sizeTable = new ArrayList<Integer>();
        for (i = 16; i < 512; i += 16) {
            sizeTable.add(i);
        }
        for (i = 512; i > 0; i <<= 1) {
            sizeTable.add(i);
        }
        SIZE_TABLE = new int[sizeTable.size()];
        for (i = 0; i < SIZE_TABLE.length; ++i) {
            AdaptiveRecvByteBufAllocator.SIZE_TABLE[i] = (Integer)sizeTable.get(i);
        }
        DEFAULT = new AdaptiveRecvByteBufAllocator();
    }

    private static final class HandleImpl
    implements RecvByteBufAllocator.Handle {
        private final int minIndex;
        private final int maxIndex;
        private int index;
        private int nextReceiveBufferSize;
        private boolean decreaseNow;

        HandleImpl(int minIndex, int maxIndex, int initial) {
            this.minIndex = minIndex;
            this.maxIndex = maxIndex;
            this.index = AdaptiveRecvByteBufAllocator.getSizeTableIndex(initial);
            this.nextReceiveBufferSize = SIZE_TABLE[this.index];
        }

        @Override
        public ByteBuf allocate(ByteBufAllocator alloc) {
            return alloc.ioBuffer(this.nextReceiveBufferSize);
        }

        @Override
        public int guess() {
            return this.nextReceiveBufferSize;
        }

        @Override
        public void record(int actualReadBytes) {
            if (actualReadBytes <= SIZE_TABLE[Math.max(0, this.index - 1 - 1)]) {
                if (this.decreaseNow) {
                    this.index = Math.max(this.index - 1, this.minIndex);
                    this.nextReceiveBufferSize = SIZE_TABLE[this.index];
                    this.decreaseNow = false;
                } else {
                    this.decreaseNow = true;
                }
            } else if (actualReadBytes >= this.nextReceiveBufferSize) {
                this.index = Math.min(this.index + 4, this.maxIndex);
                this.nextReceiveBufferSize = SIZE_TABLE[this.index];
                this.decreaseNow = false;
            }
        }
    }
}

