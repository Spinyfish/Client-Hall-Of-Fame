/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public abstract class ByteBuf
implements ReferenceCounted,
Comparable<ByteBuf> {
    public abstract int capacity();

    public abstract ByteBuf capacity(int var1);

    public abstract int maxCapacity();

    public abstract ByteBufAllocator alloc();

    public abstract ByteOrder order();

    public abstract ByteBuf order(ByteOrder var1);

    public abstract ByteBuf unwrap();

    public abstract boolean isDirect();

    public abstract int readerIndex();

    public abstract ByteBuf readerIndex(int var1);

    public abstract int writerIndex();

    public abstract ByteBuf writerIndex(int var1);

    public abstract ByteBuf setIndex(int var1, int var2);

    public abstract int readableBytes();

    public abstract int writableBytes();

    public abstract int maxWritableBytes();

    public abstract boolean isReadable();

    public abstract boolean isReadable(int var1);

    public abstract boolean isWritable();

    public abstract boolean isWritable(int var1);

    public abstract ByteBuf clear();

    public abstract ByteBuf markReaderIndex();

    public abstract ByteBuf resetReaderIndex();

    public abstract ByteBuf markWriterIndex();

    public abstract ByteBuf resetWriterIndex();

    public abstract ByteBuf discardReadBytes();

    public abstract ByteBuf discardSomeReadBytes();

    public abstract ByteBuf ensureWritable(int var1);

    public abstract int ensureWritable(int var1, boolean var2);

    public abstract boolean getBoolean(int var1);

    public abstract byte getByte(int var1);

    public abstract short getUnsignedByte(int var1);

    public abstract short getShort(int var1);

    public abstract int getUnsignedShort(int var1);

    public abstract int getMedium(int var1);

    public abstract int getUnsignedMedium(int var1);

    public abstract int getInt(int var1);

    public abstract long getUnsignedInt(int var1);

    public abstract long getLong(int var1);

    public abstract char getChar(int var1);

    public abstract float getFloat(int var1);

    public abstract double getDouble(int var1);

    public abstract ByteBuf getBytes(int var1, ByteBuf var2);

    public abstract ByteBuf getBytes(int var1, ByteBuf var2, int var3);

    public abstract ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4);

    public abstract ByteBuf getBytes(int var1, byte[] var2);

    public abstract ByteBuf getBytes(int var1, byte[] var2, int var3, int var4);

    public abstract ByteBuf getBytes(int var1, ByteBuffer var2);

    public abstract ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException;

    public abstract int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException;

    public abstract ByteBuf setBoolean(int var1, boolean var2);

    public abstract ByteBuf setByte(int var1, int var2);

    public abstract ByteBuf setShort(int var1, int var2);

    public abstract ByteBuf setMedium(int var1, int var2);

    public abstract ByteBuf setInt(int var1, int var2);

    public abstract ByteBuf setLong(int var1, long var2);

    public abstract ByteBuf setChar(int var1, int var2);

    public abstract ByteBuf setFloat(int var1, float var2);

    public abstract ByteBuf setDouble(int var1, double var2);

    public abstract ByteBuf setBytes(int var1, ByteBuf var2);

    public abstract ByteBuf setBytes(int var1, ByteBuf var2, int var3);

    public abstract ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4);

    public abstract ByteBuf setBytes(int var1, byte[] var2);

    public abstract ByteBuf setBytes(int var1, byte[] var2, int var3, int var4);

    public abstract ByteBuf setBytes(int var1, ByteBuffer var2);

    public abstract int setBytes(int var1, InputStream var2, int var3) throws IOException;

    public abstract int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException;

    public abstract ByteBuf setZero(int var1, int var2);

    public abstract boolean readBoolean();

    public abstract byte readByte();

    public abstract short readUnsignedByte();

    public abstract short readShort();

    public abstract int readUnsignedShort();

    public abstract int readMedium();

    public abstract int readUnsignedMedium();

    public abstract int readInt();

    public abstract long readUnsignedInt();

    public abstract long readLong();

    public abstract char readChar();

    public abstract float readFloat();

    public abstract double readDouble();

    public abstract ByteBuf readBytes(int var1);

    public abstract ByteBuf readSlice(int var1);

    public abstract ByteBuf readBytes(ByteBuf var1);

    public abstract ByteBuf readBytes(ByteBuf var1, int var2);

    public abstract ByteBuf readBytes(ByteBuf var1, int var2, int var3);

    public abstract ByteBuf readBytes(byte[] var1);

    public abstract ByteBuf readBytes(byte[] var1, int var2, int var3);

    public abstract ByteBuf readBytes(ByteBuffer var1);

    public abstract ByteBuf readBytes(OutputStream var1, int var2) throws IOException;

    public abstract int readBytes(GatheringByteChannel var1, int var2) throws IOException;

    public abstract ByteBuf skipBytes(int var1);

    public abstract ByteBuf writeBoolean(boolean var1);

    public abstract ByteBuf writeByte(int var1);

    public abstract ByteBuf writeShort(int var1);

    public abstract ByteBuf writeMedium(int var1);

    public abstract ByteBuf writeInt(int var1);

    public abstract ByteBuf writeLong(long var1);

    public abstract ByteBuf writeChar(int var1);

    public abstract ByteBuf writeFloat(float var1);

    public abstract ByteBuf writeDouble(double var1);

    public abstract ByteBuf writeBytes(ByteBuf var1);

    public abstract ByteBuf writeBytes(ByteBuf var1, int var2);

    public abstract ByteBuf writeBytes(ByteBuf var1, int var2, int var3);

    public abstract ByteBuf writeBytes(byte[] var1);

    public abstract ByteBuf writeBytes(byte[] var1, int var2, int var3);

    public abstract ByteBuf writeBytes(ByteBuffer var1);

    public abstract int writeBytes(InputStream var1, int var2) throws IOException;

    public abstract int writeBytes(ScatteringByteChannel var1, int var2) throws IOException;

    public abstract ByteBuf writeZero(int var1);

    public abstract int indexOf(int var1, int var2, byte var3);

    public abstract int bytesBefore(byte var1);

    public abstract int bytesBefore(int var1, byte var2);

    public abstract int bytesBefore(int var1, int var2, byte var3);

    public abstract int forEachByte(ByteBufProcessor var1);

    public abstract int forEachByte(int var1, int var2, ByteBufProcessor var3);

    public abstract int forEachByteDesc(ByteBufProcessor var1);

    public abstract int forEachByteDesc(int var1, int var2, ByteBufProcessor var3);

    public abstract ByteBuf copy();

    public abstract ByteBuf copy(int var1, int var2);

    public abstract ByteBuf slice();

    public abstract ByteBuf slice(int var1, int var2);

    public abstract ByteBuf duplicate();

    public abstract int nioBufferCount();

    public abstract ByteBuffer nioBuffer();

    public abstract ByteBuffer nioBuffer(int var1, int var2);

    public abstract ByteBuffer internalNioBuffer(int var1, int var2);

    public abstract ByteBuffer[] nioBuffers();

    public abstract ByteBuffer[] nioBuffers(int var1, int var2);

    public abstract boolean hasArray();

    public abstract byte[] array();

    public abstract int arrayOffset();

    public abstract boolean hasMemoryAddress();

    public abstract long memoryAddress();

    public abstract String toString(Charset var1);

    public abstract String toString(int var1, int var2, Charset var3);

    public abstract int hashCode();

    public abstract boolean equals(Object var1);

    @Override
    public abstract int compareTo(ByteBuf var1);

    public abstract String toString();

    @Override
    public abstract ByteBuf retain(int var1);

    @Override
    public abstract ByteBuf retain();
}

