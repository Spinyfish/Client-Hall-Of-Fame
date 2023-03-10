/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;

public interface ByteBufAllocator {
    public static final ByteBufAllocator DEFAULT = ByteBufUtil.DEFAULT_ALLOCATOR;

    public ByteBuf buffer();

    public ByteBuf buffer(int var1);

    public ByteBuf buffer(int var1, int var2);

    public ByteBuf ioBuffer();

    public ByteBuf ioBuffer(int var1);

    public ByteBuf ioBuffer(int var1, int var2);

    public ByteBuf heapBuffer();

    public ByteBuf heapBuffer(int var1);

    public ByteBuf heapBuffer(int var1, int var2);

    public ByteBuf directBuffer();

    public ByteBuf directBuffer(int var1);

    public ByteBuf directBuffer(int var1, int var2);

    public CompositeByteBuf compositeBuffer();

    public CompositeByteBuf compositeBuffer(int var1);

    public CompositeByteBuf compositeHeapBuffer();

    public CompositeByteBuf compositeHeapBuffer(int var1);

    public CompositeByteBuf compositeDirectBuffer();

    public CompositeByteBuf compositeDirectBuffer(int var1);

    public boolean isDirectBufferPooled();
}

