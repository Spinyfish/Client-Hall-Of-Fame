/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.spdy.SpdyHeaderBlockZlibDecoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;

abstract class SpdyHeaderBlockDecoder {
    SpdyHeaderBlockDecoder() {
    }

    static SpdyHeaderBlockDecoder newInstance(SpdyVersion spdyVersion, int maxHeaderSize) {
        return new SpdyHeaderBlockZlibDecoder(spdyVersion, maxHeaderSize);
    }

    abstract void decode(ByteBuf var1, SpdyHeadersFrame var2) throws Exception;

    abstract void endHeaderBlock(SpdyHeadersFrame var1) throws Exception;

    abstract void end();
}

