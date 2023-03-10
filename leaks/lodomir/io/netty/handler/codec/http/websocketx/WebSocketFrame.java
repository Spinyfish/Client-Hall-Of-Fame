/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.StringUtil;

public abstract class WebSocketFrame
extends DefaultByteBufHolder {
    private final boolean finalFragment;
    private final int rsv;

    protected WebSocketFrame(ByteBuf binaryData) {
        this(true, 0, binaryData);
    }

    protected WebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super(binaryData);
        this.finalFragment = finalFragment;
        this.rsv = rsv;
    }

    public boolean isFinalFragment() {
        return this.finalFragment;
    }

    public int rsv() {
        return this.rsv;
    }

    @Override
    public abstract WebSocketFrame copy();

    @Override
    public abstract WebSocketFrame duplicate();

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(data: " + this.content().toString() + ')';
    }

    @Override
    public WebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public WebSocketFrame retain(int increment) {
        super.retain(increment);
        return this;
    }
}

