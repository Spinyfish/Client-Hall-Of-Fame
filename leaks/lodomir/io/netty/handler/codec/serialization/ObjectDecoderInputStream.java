/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.serialization;

import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.CompactObjectInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.StreamCorruptedException;

public class ObjectDecoderInputStream
extends InputStream
implements ObjectInput {
    private final DataInputStream in;
    private final int maxObjectSize;
    private final ClassResolver classResolver;

    public ObjectDecoderInputStream(InputStream in) {
        this(in, null);
    }

    public ObjectDecoderInputStream(InputStream in, ClassLoader classLoader) {
        this(in, classLoader, 0x100000);
    }

    public ObjectDecoderInputStream(InputStream in, int maxObjectSize) {
        this(in, null, maxObjectSize);
    }

    public ObjectDecoderInputStream(InputStream in, ClassLoader classLoader, int maxObjectSize) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        if (maxObjectSize <= 0) {
            throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
        }
        this.in = in instanceof DataInputStream ? (DataInputStream)in : new DataInputStream(in);
        this.classResolver = ClassResolvers.weakCachingResolver(classLoader);
        this.maxObjectSize = maxObjectSize;
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException {
        int dataLen = this.readInt();
        if (dataLen <= 0) {
            throw new StreamCorruptedException("invalid data length: " + dataLen);
        }
        if (dataLen > this.maxObjectSize) {
            throw new StreamCorruptedException("data length too big: " + dataLen + " (max: " + this.maxObjectSize + ')');
        }
        return new CompactObjectInputStream(this.in, this.classResolver).readObject();
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public void mark(int readlimit) {
        this.in.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public final int read(byte[] b, int off, int len) throws IOException {
        return this.in.read(b, off, len);
    }

    @Override
    public final int read(byte[] b) throws IOException {
        return this.in.read(b);
    }

    @Override
    public final boolean readBoolean() throws IOException {
        return this.in.readBoolean();
    }

    @Override
    public final byte readByte() throws IOException {
        return this.in.readByte();
    }

    @Override
    public final char readChar() throws IOException {
        return this.in.readChar();
    }

    @Override
    public final double readDouble() throws IOException {
        return this.in.readDouble();
    }

    @Override
    public final float readFloat() throws IOException {
        return this.in.readFloat();
    }

    @Override
    public final void readFully(byte[] b, int off, int len) throws IOException {
        this.in.readFully(b, off, len);
    }

    @Override
    public final void readFully(byte[] b) throws IOException {
        this.in.readFully(b);
    }

    @Override
    public final int readInt() throws IOException {
        return this.in.readInt();
    }

    @Override
    @Deprecated
    public final String readLine() throws IOException {
        return this.in.readLine();
    }

    @Override
    public final long readLong() throws IOException {
        return this.in.readLong();
    }

    @Override
    public final short readShort() throws IOException {
        return this.in.readShort();
    }

    @Override
    public final int readUnsignedByte() throws IOException {
        return this.in.readUnsignedByte();
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        return this.in.readUnsignedShort();
    }

    @Override
    public final String readUTF() throws IOException {
        return this.in.readUTF();
    }

    @Override
    public void reset() throws IOException {
        this.in.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.in.skip(n);
    }

    @Override
    public final int skipBytes(int n) throws IOException {
        return this.in.skipBytes(n);
    }
}

