/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;

public abstract class AbstractDerivedByteBuf
extends AbstractByteBuf {
    protected AbstractDerivedByteBuf(int maxCapacity) {
        super(maxCapacity);
    }

    @Override
    public final int refCnt() {
        return this.unwrap().refCnt();
    }

    @Override
    public final ByteBuf retain() {
        this.unwrap().retain();
        return this;
    }

    @Override
    public final ByteBuf retain(int increment) {
        this.unwrap().retain(increment);
        return this;
    }

    @Override
    public final boolean release() {
        return this.unwrap().release();
    }

    @Override
    public final boolean release(int decrement) {
        return this.unwrap().release(decrement);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.unwrap().nioBuffer(index, length);
    }
}

