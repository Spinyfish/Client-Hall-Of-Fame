/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolChunkList;
import io.netty.buffer.PoolSubpage;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledDirectByteBuf;
import io.netty.buffer.PooledHeapByteBuf;
import io.netty.buffer.PooledUnsafeDirectByteBuf;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;

abstract class PoolArena<T> {
    static final int numTinySubpagePools = 32;
    final PooledByteBufAllocator parent;
    private final int maxOrder;
    final int pageSize;
    final int pageShifts;
    final int chunkSize;
    final int subpageOverflowMask;
    final int numSmallSubpagePools;
    private final PoolSubpage<T>[] tinySubpagePools;
    private final PoolSubpage<T>[] smallSubpagePools;
    private final PoolChunkList<T> q050;
    private final PoolChunkList<T> q025;
    private final PoolChunkList<T> q000;
    private final PoolChunkList<T> qInit;
    private final PoolChunkList<T> q075;
    private final PoolChunkList<T> q100;

    protected PoolArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
        int i;
        this.parent = parent;
        this.pageSize = pageSize;
        this.maxOrder = maxOrder;
        this.pageShifts = pageShifts;
        this.chunkSize = chunkSize;
        this.subpageOverflowMask = ~(pageSize - 1);
        this.tinySubpagePools = this.newSubpagePoolArray(32);
        for (i = 0; i < this.tinySubpagePools.length; ++i) {
            this.tinySubpagePools[i] = this.newSubpagePoolHead(pageSize);
        }
        this.numSmallSubpagePools = pageShifts - 9;
        this.smallSubpagePools = this.newSubpagePoolArray(this.numSmallSubpagePools);
        for (i = 0; i < this.smallSubpagePools.length; ++i) {
            this.smallSubpagePools[i] = this.newSubpagePoolHead(pageSize);
        }
        this.q100 = new PoolChunkList(this, null, 100, Integer.MAX_VALUE);
        this.q075 = new PoolChunkList<T>(this, this.q100, 75, 100);
        this.q050 = new PoolChunkList<T>(this, this.q075, 50, 100);
        this.q025 = new PoolChunkList<T>(this, this.q050, 25, 75);
        this.q000 = new PoolChunkList<T>(this, this.q025, 1, 50);
        this.qInit = new PoolChunkList<T>(this, this.q000, Integer.MIN_VALUE, 25);
        this.q100.prevList = this.q075;
        this.q075.prevList = this.q050;
        this.q050.prevList = this.q025;
        this.q025.prevList = this.q000;
        this.q000.prevList = null;
        this.qInit.prevList = this.qInit;
    }

    private PoolSubpage<T> newSubpagePoolHead(int pageSize) {
        PoolSubpage head = new PoolSubpage(pageSize);
        head.prev = head;
        head.next = head;
        return head;
    }

    private PoolSubpage<T>[] newSubpagePoolArray(int size) {
        return new PoolSubpage[size];
    }

    abstract boolean isDirect();

    PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
        PooledByteBuf<T> buf = this.newByteBuf(maxCapacity);
        this.allocate(cache, buf, reqCapacity);
        return buf;
    }

    static int tinyIdx(int normCapacity) {
        return normCapacity >>> 4;
    }

    static int smallIdx(int normCapacity) {
        int tableIdx = 0;
        int i = normCapacity >>> 10;
        while (i != 0) {
            i >>>= 1;
            ++tableIdx;
        }
        return tableIdx;
    }

    boolean isTinyOrSmall(int normCapacity) {
        return (normCapacity & this.subpageOverflowMask) == 0;
    }

    static boolean isTiny(int normCapacity) {
        return (normCapacity & 0xFFFFFE00) == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity) {
        int normCapacity = this.normalizeCapacity(reqCapacity);
        if (this.isTinyOrSmall(normCapacity)) {
            PoolSubpage<T>[] table;
            int tableIdx;
            if (PoolArena.isTiny(normCapacity)) {
                if (cache.allocateTiny(this, buf, reqCapacity, normCapacity)) {
                    return;
                }
                tableIdx = PoolArena.tinyIdx(normCapacity);
                table = this.tinySubpagePools;
            } else {
                if (cache.allocateSmall(this, buf, reqCapacity, normCapacity)) {
                    return;
                }
                tableIdx = PoolArena.smallIdx(normCapacity);
                table = this.smallSubpagePools;
            }
            PoolArena poolArena = this;
            synchronized (poolArena) {
                PoolSubpage<T> head = table[tableIdx];
                PoolSubpage s = head.next;
                if (s != head) {
                    assert (s.doNotDestroy && s.elemSize == normCapacity);
                    long handle = s.allocate();
                    assert (handle >= 0L);
                    s.chunk.initBufWithSubpage(buf, handle, reqCapacity);
                    return;
                }
            }
        } else if (normCapacity <= this.chunkSize) {
            if (cache.allocateNormal(this, buf, reqCapacity, normCapacity)) {
                return;
            }
        } else {
            this.allocateHuge(buf, reqCapacity);
            return;
        }
        this.allocateNormal(buf, reqCapacity, normCapacity);
    }

    private synchronized void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        if (this.q050.allocate(buf, reqCapacity, normCapacity) || this.q025.allocate(buf, reqCapacity, normCapacity) || this.q000.allocate(buf, reqCapacity, normCapacity) || this.qInit.allocate(buf, reqCapacity, normCapacity) || this.q075.allocate(buf, reqCapacity, normCapacity) || this.q100.allocate(buf, reqCapacity, normCapacity)) {
            return;
        }
        PoolChunk<T> c = this.newChunk(this.pageSize, this.maxOrder, this.pageShifts, this.chunkSize);
        long handle = c.allocate(normCapacity);
        assert (handle > 0L);
        c.initBuf(buf, handle, reqCapacity);
        this.qInit.add(c);
    }

    private void allocateHuge(PooledByteBuf<T> buf, int reqCapacity) {
        buf.initUnpooled(this.newUnpooledChunk(reqCapacity), reqCapacity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void free(PoolChunk<T> chunk, long handle, int normCapacity) {
        if (chunk.unpooled) {
            this.destroyChunk(chunk);
        } else {
            PoolThreadCache cache = (PoolThreadCache)this.parent.threadCache.get();
            if (cache.add(this, chunk, handle, normCapacity)) {
                return;
            }
            PoolArena poolArena = this;
            synchronized (poolArena) {
                chunk.parent.free(chunk, handle);
            }
        }
    }

    PoolSubpage<T> findSubpagePoolHead(int elemSize) {
        PoolSubpage<T>[] table;
        int tableIdx;
        if (PoolArena.isTiny(elemSize)) {
            tableIdx = elemSize >>> 4;
            table = this.tinySubpagePools;
        } else {
            tableIdx = 0;
            elemSize >>>= 10;
            while (elemSize != 0) {
                elemSize >>>= 1;
                ++tableIdx;
            }
            table = this.smallSubpagePools;
        }
        return table[tableIdx];
    }

    int normalizeCapacity(int reqCapacity) {
        if (reqCapacity < 0) {
            throw new IllegalArgumentException("capacity: " + reqCapacity + " (expected: 0+)");
        }
        if (reqCapacity >= this.chunkSize) {
            return reqCapacity;
        }
        if (!PoolArena.isTiny(reqCapacity)) {
            int normalizedCapacity = reqCapacity;
            --normalizedCapacity;
            normalizedCapacity |= normalizedCapacity >>> 1;
            normalizedCapacity |= normalizedCapacity >>> 2;
            normalizedCapacity |= normalizedCapacity >>> 4;
            normalizedCapacity |= normalizedCapacity >>> 8;
            normalizedCapacity |= normalizedCapacity >>> 16;
            if (++normalizedCapacity < 0) {
                normalizedCapacity >>>= 1;
            }
            return normalizedCapacity;
        }
        if ((reqCapacity & 0xF) == 0) {
            return reqCapacity;
        }
        return (reqCapacity & 0xFFFFFFF0) + 16;
    }

    void reallocate(PooledByteBuf<T> buf, int newCapacity, boolean freeOldMemory) {
        if (newCapacity < 0 || newCapacity > buf.maxCapacity()) {
            throw new IllegalArgumentException("newCapacity: " + newCapacity);
        }
        int oldCapacity = buf.length;
        if (oldCapacity == newCapacity) {
            return;
        }
        PoolChunk oldChunk = buf.chunk;
        long oldHandle = buf.handle;
        Object oldMemory = buf.memory;
        int oldOffset = buf.offset;
        int oldMaxLength = buf.maxLength;
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        this.allocate((PoolThreadCache)this.parent.threadCache.get(), buf, newCapacity);
        if (newCapacity > oldCapacity) {
            this.memoryCopy(oldMemory, oldOffset, buf.memory, buf.offset, oldCapacity);
        } else if (newCapacity < oldCapacity) {
            if (readerIndex < newCapacity) {
                if (writerIndex > newCapacity) {
                    writerIndex = newCapacity;
                }
                this.memoryCopy(oldMemory, oldOffset + readerIndex, buf.memory, buf.offset + readerIndex, writerIndex - readerIndex);
            } else {
                readerIndex = writerIndex = newCapacity;
            }
        }
        buf.setIndex(readerIndex, writerIndex);
        if (freeOldMemory) {
            this.free(oldChunk, oldHandle, oldMaxLength);
        }
    }

    protected abstract PoolChunk<T> newChunk(int var1, int var2, int var3, int var4);

    protected abstract PoolChunk<T> newUnpooledChunk(int var1);

    protected abstract PooledByteBuf<T> newByteBuf(int var1);

    protected abstract void memoryCopy(T var1, int var2, T var3, int var4, int var5);

    protected abstract void destroyChunk(PoolChunk<T> var1);

    public synchronized String toString() {
        PoolSubpage s;
        PoolSubpage<T> head;
        int i;
        StringBuilder buf = new StringBuilder();
        buf.append("Chunk(s) at 0~25%:");
        buf.append(StringUtil.NEWLINE);
        buf.append(this.qInit);
        buf.append(StringUtil.NEWLINE);
        buf.append("Chunk(s) at 0~50%:");
        buf.append(StringUtil.NEWLINE);
        buf.append(this.q000);
        buf.append(StringUtil.NEWLINE);
        buf.append("Chunk(s) at 25~75%:");
        buf.append(StringUtil.NEWLINE);
        buf.append(this.q025);
        buf.append(StringUtil.NEWLINE);
        buf.append("Chunk(s) at 50~100%:");
        buf.append(StringUtil.NEWLINE);
        buf.append(this.q050);
        buf.append(StringUtil.NEWLINE);
        buf.append("Chunk(s) at 75~100%:");
        buf.append(StringUtil.NEWLINE);
        buf.append(this.q075);
        buf.append(StringUtil.NEWLINE);
        buf.append("Chunk(s) at 100%:");
        buf.append(StringUtil.NEWLINE);
        buf.append(this.q100);
        buf.append(StringUtil.NEWLINE);
        buf.append("tiny subpages:");
        for (i = 1; i < this.tinySubpagePools.length; ++i) {
            head = this.tinySubpagePools[i];
            if (head.next == head) continue;
            buf.append(StringUtil.NEWLINE);
            buf.append(i);
            buf.append(": ");
            s = head.next;
            do {
                buf.append(s);
            } while ((s = s.next) != head);
        }
        buf.append(StringUtil.NEWLINE);
        buf.append("small subpages:");
        for (i = 1; i < this.smallSubpagePools.length; ++i) {
            head = this.smallSubpagePools[i];
            if (head.next == head) continue;
            buf.append(StringUtil.NEWLINE);
            buf.append(i);
            buf.append(": ");
            s = head.next;
            do {
                buf.append(s);
            } while ((s = s.next) != head);
        }
        buf.append(StringUtil.NEWLINE);
        return buf.toString();
    }

    static final class DirectArena
    extends PoolArena<ByteBuffer> {
        private static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();

        DirectArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
            super(parent, pageSize, maxOrder, pageShifts, chunkSize);
        }

        @Override
        boolean isDirect() {
            return true;
        }

        @Override
        protected PoolChunk<ByteBuffer> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
            return new PoolChunk<ByteBuffer>(this, ByteBuffer.allocateDirect(chunkSize), pageSize, maxOrder, pageShifts, chunkSize);
        }

        @Override
        protected PoolChunk<ByteBuffer> newUnpooledChunk(int capacity) {
            return new PoolChunk<ByteBuffer>(this, ByteBuffer.allocateDirect(capacity), capacity);
        }

        @Override
        protected void destroyChunk(PoolChunk<ByteBuffer> chunk) {
            PlatformDependent.freeDirectBuffer((ByteBuffer)chunk.memory);
        }

        @Override
        protected PooledByteBuf<ByteBuffer> newByteBuf(int maxCapacity) {
            if (HAS_UNSAFE) {
                return PooledUnsafeDirectByteBuf.newInstance(maxCapacity);
            }
            return PooledDirectByteBuf.newInstance(maxCapacity);
        }

        @Override
        protected void memoryCopy(ByteBuffer src, int srcOffset, ByteBuffer dst, int dstOffset, int length) {
            if (length == 0) {
                return;
            }
            if (HAS_UNSAFE) {
                PlatformDependent.copyMemory(PlatformDependent.directBufferAddress(src) + (long)srcOffset, PlatformDependent.directBufferAddress(dst) + (long)dstOffset, length);
            } else {
                src = src.duplicate();
                dst = dst.duplicate();
                src.position(srcOffset).limit(srcOffset + length);
                dst.position(dstOffset);
                dst.put(src);
            }
        }
    }

    static final class HeapArena
    extends PoolArena<byte[]> {
        HeapArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
            super(parent, pageSize, maxOrder, pageShifts, chunkSize);
        }

        @Override
        boolean isDirect() {
            return false;
        }

        @Override
        protected PoolChunk<byte[]> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
            return new PoolChunk<byte[]>(this, new byte[chunkSize], pageSize, maxOrder, pageShifts, chunkSize);
        }

        @Override
        protected PoolChunk<byte[]> newUnpooledChunk(int capacity) {
            return new PoolChunk<byte[]>(this, new byte[capacity], capacity);
        }

        @Override
        protected void destroyChunk(PoolChunk<byte[]> chunk) {
        }

        @Override
        protected PooledByteBuf<byte[]> newByteBuf(int maxCapacity) {
            return PooledHeapByteBuf.newInstance(maxCapacity);
        }

        @Override
        protected void memoryCopy(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) {
            if (length == 0) {
                return;
            }
            System.arraycopy(src, srcOffset, dst, dstOffset, length);
        }
    }
}

