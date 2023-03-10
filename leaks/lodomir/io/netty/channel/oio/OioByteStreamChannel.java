/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.FileRegion;
import io.netty.channel.oio.AbstractOioByteChannel;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.WritableByteChannel;

public abstract class OioByteStreamChannel
extends AbstractOioByteChannel {
    private static final InputStream CLOSED_IN = new InputStream(){

        @Override
        public int read() {
            return -1;
        }
    };
    private static final OutputStream CLOSED_OUT = new OutputStream(){

        @Override
        public void write(int b) throws IOException {
            throw new ClosedChannelException();
        }
    };
    private InputStream is;
    private OutputStream os;
    private WritableByteChannel outChannel;

    protected OioByteStreamChannel(Channel parent) {
        super(parent);
    }

    protected final void activate(InputStream is, OutputStream os) {
        if (this.is != null) {
            throw new IllegalStateException("input was set already");
        }
        if (this.os != null) {
            throw new IllegalStateException("output was set already");
        }
        if (is == null) {
            throw new NullPointerException("is");
        }
        if (os == null) {
            throw new NullPointerException("os");
        }
        this.is = is;
        this.os = os;
    }

    @Override
    public boolean isActive() {
        InputStream is = this.is;
        if (is == null || is == CLOSED_IN) {
            return false;
        }
        OutputStream os = this.os;
        return os != null && os != CLOSED_OUT;
    }

    @Override
    protected int available() {
        try {
            return this.is.available();
        }
        catch (IOException ignored) {
            return 0;
        }
    }

    @Override
    protected int doReadBytes(ByteBuf buf) throws Exception {
        int length = Math.max(1, Math.min(this.available(), buf.maxWritableBytes()));
        return buf.writeBytes(this.is, length);
    }

    @Override
    protected void doWriteBytes(ByteBuf buf) throws Exception {
        OutputStream os = this.os;
        if (os == null) {
            throw new NotYetConnectedException();
        }
        buf.readBytes(os, buf.readableBytes());
    }

    @Override
    protected void doWriteFileRegion(FileRegion region) throws Exception {
        long localWritten;
        OutputStream os = this.os;
        if (os == null) {
            throw new NotYetConnectedException();
        }
        if (this.outChannel == null) {
            this.outChannel = Channels.newChannel(os);
        }
        long written = 0L;
        do {
            if ((localWritten = region.transferTo(this.outChannel, written)) != -1L) continue;
            OioByteStreamChannel.checkEOF(region);
            return;
        } while ((written += localWritten) < region.count());
    }

    private static void checkEOF(FileRegion region) throws IOException {
        if (region.transfered() < region.count()) {
            throw new EOFException("Expected to be able to write " + region.count() + " bytes, " + "but only wrote " + region.transfered());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doClose() throws Exception {
        InputStream is = this.is;
        OutputStream os = this.os;
        this.is = CLOSED_IN;
        this.os = CLOSED_OUT;
        try {
            if (is != null) {
                is.close();
            }
        }
        finally {
            if (os != null) {
                os.close();
            }
        }
    }
}

