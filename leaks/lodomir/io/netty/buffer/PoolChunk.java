/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunkList;
import io.netty.buffer.PoolSubpage;
import io.netty.buffer.PooledByteBuf;

final class PoolChunk<T> {
    final PoolArena<T> arena;
    final T memory;
    final boolean unpooled;
    private final byte[] memoryMap;
    private final byte[] depthMap;
    private final PoolSubpage<T>[] subpages;
    private final int subpageOverflowMask;
    private final int pageSize;
    private final int pageShifts;
    private final int maxOrder;
    private final int chunkSize;
    private final int log2ChunkSize;
    private final int maxSubpageAllocs;
    private final byte unusable;
    private int freeBytes;
    PoolChunkList<T> parent;
    PoolChunk<T> prev;
    PoolChunk<T> next;

    PoolChunk(PoolArena<T> arena, T memory, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
        this.unpooled = false;
        this.arena = arena;
        this.memory = memory;
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        this.maxOrder = maxOrder;
        this.chunkSize = chunkSize;
        this.unusable = (byte)(maxOrder + 1);
        this.log2ChunkSize = PoolChunk.log2(chunkSize);
        this.subpageOverflowMask = ~(pageSize - 1);
        this.freeBytes = chunkSize;
        assert (maxOrder < 30) : "maxOrder should be < 30, but is: " + maxOrder;
        this.maxSubpageAllocs = 1 << maxOrder;
        this.memoryMap = new byte[this.maxSubpageAllocs << 1];
        this.depthMap = new byte[this.memoryMap.length];
        int memoryMapIndex = 1;
        for (int d = 0; d <= maxOrder; ++d) {
            int depth = 1 << d;
            for (int p = 0; p < depth; ++p) {
                this.memoryMap[memoryMapIndex] = (byte)d;
                this.depthMap[memoryMapIndex] = (byte)d;
                ++memoryMapIndex;
            }
        }
        this.subpages = this.newSubpageArray(this.maxSubpageAllocs);
    }

    PoolChunk(PoolArena<T> arena, T memory, int size) {
        this.unpooled = true;
        this.arena = arena;
        this.memory = memory;
        this.memoryMap = null;
        this.depthMap = null;
        this.subpages = null;
        this.subpageOverflowMask = 0;
        this.pageSize = 0;
        this.pageShifts = 0;
        this.maxOrder = 0;
        this.unusable = (byte)(this.maxOrder + 1);
        this.chunkSize = size;
        this.log2ChunkSize = PoolChunk.log2(this.chunkSize);
        this.maxSubpageAllocs = 0;
    }

    private PoolSubpage<T>[] newSubpageArray(int size) {
        return new PoolSubpage[size];
    }

    int usage() {
        int freeBytes = this.freeBytes;
        if (freeBytes == 0) {
            return 100;
        }
        int freePercentage = (int)((long)freeBytes * 100L / (long)this.chunkSize);
        if (freePercentage == 0) {
            return 99;
        }
        return 100 - freePercentage;
    }

    long allocate(int normCapacity) {
        if ((normCapacity & this.subpageOverflowMask) != 0) {
            return this.allocateRun(normCapacity);
        }
        return this.allocateSubpage(normCapacity);
    }

    private void updateParentsAlloc(int id) {
        while (id > 1) {
            byte val2;
            int parentId = id >>> 1;
            byte val1 = this.value(id);
            byte val = val1 < (val2 = this.value(id ^ 1)) ? val1 : val2;
            this.setValue(parentId, val);
            id = parentId;
        }
    }

    private void updateParentsFree(int id) {
        int logChild = this.depth(id) + 1;
        while (id > 1) {
            int parentId = id >>> 1;
            byte val1 = this.value(id);
            byte val2 = this.value(id ^ 1);
            if (val1 == --logChild && val2 == logChild) {
                this.setValue(parentId, (byte)(logChild - 1));
            } else {
                byte val = val1 < val2 ? val1 : val2;
                this.setValue(parentId, val);
            }
            id = parentId;
        }
    }

    private int allocateNode(int d) {
        int id = 1;
        int initial = -(1 << d);
        byte val = this.value(id);
        if (val > d) {
            return -1;
        }
        while (val < d || (id & initial) == 0) {
            val = this.value(id <<= 1);
            if (val <= d) continue;
            val = this.value(id ^= 1);
        }
        byte value = this.value(id);
        assert (value == d && (id & initial) == 1 << d) : String.format("val = %d, id & initial = %d, d = %d", value, id & initial, d);
        this.setValue(id, this.unusable);
        this.updateParentsAlloc(id);
        return id;
    }

    private long allocateRun(int normCapacity) {
        int d = this.maxOrder - (PoolChunk.log2(normCapacity) - this.pageShifts);
        int id = this.allocateNode(d);
        if (id < 0) {
            return id;
        }
        this.freeBytes -= this.runLength(id);
        return id;
    }

    private long allocateSubpage(int normCapacity) {
        int d = this.maxOrder;
        int id = this.allocateNode(d);
        if (id < 0) {
            return id;
        }
        PoolSubpage<T>[] subpages = this.subpages;
        int pageSize = this.pageSize;
        this.freeBytes -= pageSize;
        int subpageIdx = this.subpageIdx(id);
        PoolSubpage<T> subpage = subpages[subpageIdx];
        if (subpage == null) {
            subpage = new PoolSubpage(this, id, this.runOffset(id), pageSize, normCapacity);
            subpages[subpageIdx] = subpage;
        } else {
            subpage.init(normCapacity);
        }
        return subpage.allocate();
    }

    void free(long handle) {
        int memoryMapIdx = (int)handle;
        int bitmapIdx = (int)(handle >>> 32);
        if (bitmapIdx != 0) {
            PoolSubpage<T> subpage = this.subpages[this.subpageIdx(memoryMapIdx)];
            assert (subpage != null && subpage.doNotDestroy);
            if (subpage.free(bitmapIdx & 0x3FFFFFFF)) {
                return;
            }
        }
        this.freeBytes += this.runLength(memoryMapIdx);
        this.setValue(memoryMapIdx, this.depth(memoryMapIdx));
        this.updateParentsFree(memoryMapIdx);
    }

    void initBuf(PooledByteBuf<T> buf, long handle, int reqCapacity) {
        int memoryMapIdx = (int)handle;
        int bitmapIdx = (int)(handle >>> 32);
        if (bitmapIdx == 0) {
            byte val = this.value(memoryMapIdx);
            assert (val == this.unusable) : String.valueOf(val);
            buf.init(this, handle, this.runOffset(memoryMapIdx), reqCapacity, this.runLength(memoryMapIdx));
        } else {
            this.initBufWithSubpage(buf, handle, bitmapIdx, reqCapacity);
        }
    }

    void initBufWithSubpage(PooledByteBuf<T> buf, long handle, int reqCapacity) {
        this.initBufWithSubpage(buf, handle, (int)(handle >>> 32), reqCapacity);
    }

    private void initBufWithSubpage(PooledByteBuf<T> buf, long handle, int bitmapIdx, int reqCapacity) {
        assert (bitmapIdx != 0);
        int memoryMapIdx = (int)handle;
        PoolSubpage<T> subpage = this.subpages[this.subpageIdx(memoryMapIdx)];
        assert (subpage.doNotDestroy);
        assert (reqCapacity <= subpage.elemSize);
        buf.init(this, handle, this.runOffset(memoryMapIdx) + (bitmapIdx & 0x3FFFFFFF) * subpage.elemSize, reqCapacity, subpage.elemSize);
    }

    private byte value(int id) {
        return this.memoryMap[id];
    }

    private void setValue(int id, byte val) {
        this.memoryMap[id] = val;
    }

    private byte depth(int id) {
        return this.depthMap[id];
    }

    private static int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }

    private int runLength(int id) {
        return 1 << this.log2ChunkSize - this.depth(id);
    }

    private int runOffset(int id) {
        int shift = id ^ 1 << this.depth(id);
        return shift * this.runLength(id);
    }

    private int subpageIdx(int memoryMapIdx) {
        return memoryMapIdx ^ this.maxSubpageAllocs;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Chunk(");
        buf.append(Integer.toHexString(System.identityHashCode(this)));
        buf.append(": ");
        buf.append(this.usage());
        buf.append("%, ");
        buf.append(this.chunkSize - this.freeBytes);
        buf.append('/');
        buf.append(this.chunkSize);
        buf.append(')');
        return buf.toString();
    }
}

