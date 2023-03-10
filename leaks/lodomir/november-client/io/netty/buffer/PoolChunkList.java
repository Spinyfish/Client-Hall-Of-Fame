/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.internal.StringUtil;

final class PoolChunkList<T> {
    private final PoolArena<T> arena;
    private final PoolChunkList<T> nextList;
    PoolChunkList<T> prevList;
    private final int minUsage;
    private final int maxUsage;
    private PoolChunk<T> head;

    PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage) {
        this.arena = arena;
        this.nextList = nextList;
        this.minUsage = minUsage;
        this.maxUsage = maxUsage;
    }

    boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        long handle;
        if (this.head == null) {
            return false;
        }
        PoolChunk<T> cur = this.head;
        while ((handle = cur.allocate(normCapacity)) < 0L) {
            cur = cur.next;
            if (cur != null) continue;
            return false;
        }
        cur.initBuf(buf, handle, reqCapacity);
        if (cur.usage() >= this.maxUsage) {
            this.remove(cur);
            this.nextList.add(cur);
        }
        return true;
    }

    void free(PoolChunk<T> chunk, long handle) {
        chunk.free(handle);
        if (chunk.usage() < this.minUsage) {
            this.remove(chunk);
            if (this.prevList == null) {
                assert (chunk.usage() == 0);
                this.arena.destroyChunk(chunk);
            } else {
                this.prevList.add(chunk);
            }
        }
    }

    void add(PoolChunk<T> chunk) {
        if (chunk.usage() >= this.maxUsage) {
            this.nextList.add(chunk);
            return;
        }
        chunk.parent = this;
        if (this.head == null) {
            this.head = chunk;
            chunk.prev = null;
            chunk.next = null;
        } else {
            chunk.prev = null;
            chunk.next = this.head;
            this.head.prev = chunk;
            this.head = chunk;
        }
    }

    private void remove(PoolChunk<T> cur) {
        if (cur == this.head) {
            this.head = cur.next;
            if (this.head != null) {
                this.head.prev = null;
            }
        } else {
            PoolChunk next;
            cur.prev.next = next = cur.next;
            if (next != null) {
                next.prev = cur.prev;
            }
        }
    }

    public String toString() {
        if (this.head == null) {
            return "none";
        }
        StringBuilder buf = new StringBuilder();
        PoolChunk<T> cur = this.head;
        while (true) {
            buf.append(cur);
            cur = cur.next;
            if (cur == null) break;
            buf.append(StringUtil.NEWLINE);
        }
        return buf.toString();
    }
}

