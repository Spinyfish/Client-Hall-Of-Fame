/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class ChunkedStream
implements ChunkedInput<ByteBuf> {
    static final int DEFAULT_CHUNK_SIZE = 8192;
    private final PushbackInputStream in;
    private final int chunkSize;
    private long offset;

    public ChunkedStream(InputStream in) {
        this(in, 8192);
    }

    public ChunkedStream(InputStream in, int chunkSize) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        }
        this.in = in instanceof PushbackInputStream ? (PushbackInputStream)in : new PushbackInputStream(in);
        this.chunkSize = chunkSize;
    }

    public long transferredBytes() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        int b = this.in.read();
        if (b < 0) {
            return true;
        }
        this.in.unread(b);
        return false;
    }

    @Override
    public void close() throws Exception {
        this.in.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        if (this.isEndOfInput()) {
            return null;
        }
        int availableBytes = this.in.available();
        int chunkSize = availableBytes <= 0 ? this.chunkSize : Math.min(this.chunkSize, this.in.available());
        boolean release = true;
        ByteBuf buffer = ctx.alloc().buffer(chunkSize);
        try {
            this.offset += (long)buffer.writeBytes(this.in, chunkSize);
            release = false;
            ByteBuf byteBuf = buffer;
            return byteBuf;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }
}

