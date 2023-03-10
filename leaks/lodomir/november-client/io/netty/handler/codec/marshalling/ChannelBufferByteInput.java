/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.marshalling.ByteInput
 */
package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.jboss.marshalling.ByteInput;

class ChannelBufferByteInput
implements ByteInput {
    private final ByteBuf buffer;

    ChannelBufferByteInput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public void close() throws IOException {
    }

    public int available() throws IOException {
        return this.buffer.readableBytes();
    }

    public int read() throws IOException {
        if (this.buffer.isReadable()) {
            return this.buffer.readByte() & 0xFF;
        }
        return -1;
    }

    public int read(byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }

    public int read(byte[] dst, int dstIndex, int length) throws IOException {
        int available = this.available();
        if (available == 0) {
            return -1;
        }
        length = Math.min(available, length);
        this.buffer.readBytes(dst, dstIndex, length);
        return length;
    }

    public long skip(long bytes) throws IOException {
        int readable = this.buffer.readableBytes();
        if ((long)readable < bytes) {
            bytes = readable;
        }
        this.buffer.readerIndex((int)((long)this.buffer.readerIndex() + bytes));
        return bytes;
    }
}

