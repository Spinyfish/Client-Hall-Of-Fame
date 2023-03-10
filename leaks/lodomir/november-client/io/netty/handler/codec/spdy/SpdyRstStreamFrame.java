/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;

public interface SpdyRstStreamFrame
extends SpdyStreamFrame {
    public SpdyStreamStatus status();

    public SpdyRstStreamFrame setStatus(SpdyStreamStatus var1);

    @Override
    public SpdyRstStreamFrame setStreamId(int var1);

    @Override
    public SpdyRstStreamFrame setLast(boolean var1);
}

