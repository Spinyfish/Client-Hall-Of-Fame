/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;

public interface SpdyWindowUpdateFrame
extends SpdyFrame {
    public int streamId();

    public SpdyWindowUpdateFrame setStreamId(int var1);

    public int deltaWindowSize();

    public SpdyWindowUpdateFrame setDeltaWindowSize(int var1);
}

