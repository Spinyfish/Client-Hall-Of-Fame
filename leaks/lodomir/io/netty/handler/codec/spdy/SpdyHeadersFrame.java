/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyStreamFrame;

public interface SpdyHeadersFrame
extends SpdyStreamFrame {
    public boolean isInvalid();

    public SpdyHeadersFrame setInvalid();

    public boolean isTruncated();

    public SpdyHeadersFrame setTruncated();

    public SpdyHeaders headers();

    @Override
    public SpdyHeadersFrame setStreamId(int var1);

    @Override
    public SpdyHeadersFrame setLast(boolean var1);
}

