/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.serialization.CompactObjectOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

public class ObjectEncoderOutputStream
extends OutputStream
implements ObjectOutput {
    private final DataOutputStream out;
    private final int estimatedLength;

    public ObjectEncoderOutputStream(OutputStream out) {
        this(out, 512);
    }

    public ObjectEncoderOutputStream(OutputStream out, int estimatedLength) {
        if (out == null) {
            throw new NullPointerException("out");
        }
        if (estimatedLength < 0) {
            throw new IllegalArgumentException("estimatedLength: " + estimatedLength);
        }
        this.out = out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream(out);
        this.estimatedLength = estimatedLength;
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        ByteBufOutputStream bout = new ByteBufOutputStream(Unpooled.buffer(this.estimatedLength));
        CompactObjectOutputStream oout = new CompactObjectOutputStream(bout);
        oout.writeObject(obj);
        oout.flush();
        oout.close();
        ByteBuf buffer = bout.buffer();
        int objectSize = buffer.readableBytes();
        this.writeInt(objectSize);
        buffer.getBytes(0, this, objectSize);
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write(b);
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    public final int size() {
        return this.out.size();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.out.write(b);
    }

    @Override
    public final void writeBoolean(boolean v) throws IOException {
        this.out.writeBoolean(v);
    }

    @Override
    public final void writeByte(int v) throws IOException {
        this.out.writeByte(v);
    }

    @Override
    public final void writeBytes(String s) throws IOException {
        this.out.writeBytes(s);
    }

    @Override
    public final void writeChar(int v) throws IOException {
        this.out.writeChar(v);
    }

    @Override
    public final void writeChars(String s) throws IOException {
        this.out.writeChars(s);
    }

    @Override
    public final void writeDouble(double v) throws IOException {
        this.out.writeDouble(v);
    }

    @Override
    public final void writeFloat(float v) throws IOException {
        this.out.writeFloat(v);
    }

    @Override
    public final void writeInt(int v) throws IOException {
        this.out.writeInt(v);
    }

    @Override
    public final void writeLong(long v) throws IOException {
        this.out.writeLong(v);
    }

    @Override
    public final void writeShort(int v) throws IOException {
        this.out.writeShort(v);
    }

    @Override
    public final void writeUTF(String str) throws IOException {
        this.out.writeUTF(str);
    }
}

