/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.udt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

public final class UdtMessage
extends DefaultByteBufHolder {
    public UdtMessage(ByteBuf data) {
        super(data);
    }

    @Override
    public UdtMessage copy() {
        return new UdtMessage(this.content().copy());
    }

    @Override
    public UdtMessage duplicate() {
        return new UdtMessage(this.content().duplicate());
    }

    @Override
    public UdtMessage retain() {
        super.retain();
        return this;
    }

    @Override
    public UdtMessage retain(int increment) {
        super.retain(increment);
        return this;
    }
}

