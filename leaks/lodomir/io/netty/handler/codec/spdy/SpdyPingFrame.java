/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;

public interface SpdyPingFrame
extends SpdyFrame {
    public int id();

    public SpdyPingFrame setId(int var1);
}

