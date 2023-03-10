/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.util.internal.PlatformDependent;

public final class UnpooledByteBufAllocator
extends AbstractByteBufAllocator {
    public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator(PlatformDependent.directBufferPreferred());

    public UnpooledByteBufAllocator(boolean preferDirect) {
        super(preferDirect);
    }

    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        return new UnpooledHeapByteBuf((ByteBufAllocator)this, initialCapacity, maxCapacity);
    }

    @Override
    protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
        AbstractReferenceCountedByteBuf buf = PlatformDependent.hasUnsafe() ? new UnpooledUnsafeDirectByteBuf((ByteBufAllocator)this, initialCapacity, maxCapacity) : new UnpooledDirectByteBuf((ByteBufAllocator)this, initialCapacity, maxCapacity);
        return UnpooledByteBufAllocator.toLeakAwareBuffer(buf);
    }

    @Override
    public boolean isDirectBufferPooled() {
        return false;
    }
}

