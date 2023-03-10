/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyStreamFrame;

public abstract class DefaultSpdyStreamFrame
implements SpdyStreamFrame {
    private int streamId;
    private boolean last;

    protected DefaultSpdyStreamFrame(int streamId) {
        this.setStreamId(streamId);
    }

    @Override
    public int streamId() {
        return this.streamId;
    }

    @Override
    public SpdyStreamFrame setStreamId(int streamId) {
        if (streamId <= 0) {
            throw new IllegalArgumentException("Stream-ID must be positive: " + streamId);
        }
        this.streamId = streamId;
        return this;
    }

    @Override
    public boolean isLast() {
        return this.last;
    }

    @Override
    public SpdyStreamFrame setLast(boolean last) {
        this.last = last;
        return this;
    }
}

