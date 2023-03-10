/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteBufOutputStream
extends OutputStream
implements DataOutput {
    private final ByteBuf buffer;
    private final int startIndex;
    private final DataOutputStream utf8out = new DataOutputStream(this);

    public ByteBufOutputStream(ByteBuf buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        this.startIndex = buffer.writerIndex();
    }

    public int writtenBytes() {
        return this.buffer.writerIndex() - this.startIndex;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        this.buffer.writeBytes(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.buffer.writeBytes(b);
    }

    @Override
    public void write(int b) throws IOException {
        this.buffer.writeByte((byte)b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.write(v ? 1 : 0);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.write(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            this.write((byte)s.charAt(i));
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.writeShort((short)v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            this.writeChar(s.charAt(i));
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.buffer.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.buffer.writeLong(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.buffer.writeShort((short)v);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        this.utf8out.writeUTF(s);
    }

    public ByteBuf buffer() {
        return this.buffer;
    }
}

