/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.ResourceLeak;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class AdvancedLeakAwareByteBuf
extends WrappedByteBuf {
    private final ResourceLeak leak;

    AdvancedLeakAwareByteBuf(ByteBuf buf, ResourceLeak leak) {
        super(buf);
        this.leak = leak;
    }

    @Override
    public boolean release() {
        boolean deallocated = super.release();
        if (deallocated) {
            this.leak.close();
        } else {
            this.leak.record();
        }
        return deallocated;
    }

    @Override
    public boolean release(int decrement) {
        boolean deallocated = super.release(decrement);
        if (deallocated) {
            this.leak.close();
        } else {
            this.leak.record();
        }
        return deallocated;
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        this.leak.record();
        if (this.order() == endianness) {
            return this;
        }
        return new AdvancedLeakAwareByteBuf(super.order(endianness), this.leak);
    }

    @Override
    public ByteBuf slice() {
        this.leak.record();
        return new AdvancedLeakAwareByteBuf(super.slice(), this.leak);
    }

    @Override
    public ByteBuf slice(int index, int length) {
        this.leak.record();
        return new AdvancedLeakAwareByteBuf(super.slice(index, length), this.leak);
    }

    @Override
    public ByteBuf duplicate() {
        this.leak.record();
        return new AdvancedLeakAwareByteBuf(super.duplicate(), this.leak);
    }

    @Override
    public ByteBuf readSlice(int length) {
        this.leak.record();
        return new AdvancedLeakAwareByteBuf(super.readSlice(length), this.leak);
    }

    @Override
    public ByteBuf discardReadBytes() {
        this.leak.record();
        return super.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        this.leak.record();
        return super.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        this.leak.record();
        return super.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        this.leak.record();
        return super.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        this.leak.record();
        return super.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        this.leak.record();
        return super.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        this.leak.record();
        return super.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        this.leak.record();
        return super.getShort(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        this.leak.record();
        return super.getUnsignedShort(index);
    }

    @Override
    public int getMedium(int index) {
        this.leak.record();
        return super.getMedium(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.leak.record();
        return super.getUnsignedMedium(index);
    }

    @Override
    public int getInt(int index) {
        this.leak.record();
        return super.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        this.leak.record();
        return super.getUnsignedInt(index);
    }

    @Override
    public long getLong(int index) {
        this.leak.record();
        return super.getLong(index);
    }

    @Override
    public char getChar(int index) {
        this.leak.record();
        return super.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        this.leak.record();
        return super.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        this.leak.record();
        return super.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        this.leak.record();
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.leak.record();
        return super.getBytes(index, dst, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.leak.record();
        return super.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        this.leak.record();
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.leak.record();
        return super.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.leak.record();
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.leak.record();
        return super.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        this.leak.record();
        return super.getBytes(index, out, length);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        this.leak.record();
        return super.setBoolean(index, value);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.leak.record();
        return super.setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.leak.record();
        return super.setShort(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.leak.record();
        return super.setMedium(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.leak.record();
        return super.setInt(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.leak.record();
        return super.setLong(index, value);
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        this.leak.record();
        return super.setChar(index, value);
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        this.leak.record();
        return super.setFloat(index, value);
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        this.leak.record();
        return super.setDouble(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        this.leak.record();
        return super.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        this.leak.record();
        return super.setBytes(index, src, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.leak.record();
        return super.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        this.leak.record();
        return super.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.leak.record();
        return super.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.leak.record();
        return super.setBytes(index, src);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.leak.record();
        return super.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.leak.record();
        return super.setBytes(index, in, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        this.leak.record();
        return super.setZero(index, length);
    }

    @Override
    public boolean readBoolean() {
        this.leak.record();
        return super.readBoolean();
    }

    @Override
    public byte readByte() {
        this.leak.record();
        return super.readByte();
    }

    @Override
    public short readUnsignedByte() {
        this.leak.record();
        return super.readUnsignedByte();
    }

    @Override
    public short readShort() {
        this.leak.record();
        return super.readShort();
    }

    @Override
    public int readUnsignedShort() {
        this.leak.record();
        return super.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        this.leak.record();
        return super.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        this.leak.record();
        return super.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        this.leak.record();
        return super.readInt();
    }

    @Override
    public long readUnsignedInt() {
        this.leak.record();
        return super.readUnsignedInt();
    }

    @Override
    public long readLong() {
        this.leak.record();
        return super.readLong();
    }

    @Override
    public char readChar() {
        this.leak.record();
        return super.readChar();
    }

    @Override
    public float readFloat() {
        this.leak.record();
        return super.readFloat();
    }

    @Override
    public double readDouble() {
        this.leak.record();
        return super.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        this.leak.record();
        return super.readBytes(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        this.leak.record();
        return super.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        this.leak.record();
        return super.readBytes(dst, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        this.leak.record();
        return super.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        this.leak.record();
        return super.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.leak.record();
        return super.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        this.leak.record();
        return super.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.leak.record();
        return super.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        this.leak.record();
        return super.readBytes(out, length);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        this.leak.record();
        return super.skipBytes(length);
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        this.leak.record();
        return super.writeBoolean(value);
    }

    @Override
    public ByteBuf writeByte(int value) {
        this.leak.record();
        return super.writeByte(value);
    }

    @Override
    public ByteBuf writeShort(int value) {
        this.leak.record();
        return super.writeShort(value);
    }

    @Override
    public ByteBuf writeMedium(int value) {
        this.leak.record();
        return super.writeMedium(value);
    }

    @Override
    public ByteBuf writeInt(int value) {
        this.leak.record();
        return super.writeInt(value);
    }

    @Override
    public ByteBuf writeLong(long value) {
        this.leak.record();
        return super.writeLong(value);
    }

    @Override
    public ByteBuf writeChar(int value) {
        this.leak.record();
        return super.writeChar(value);
    }

    @Override
    public ByteBuf writeFloat(float value) {
        this.leak.record();
        return super.writeFloat(value);
    }

    @Override
    public ByteBuf writeDouble(double value) {
        this.leak.record();
        return super.writeDouble(value);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        this.leak.record();
        return super.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        this.leak.record();
        return super.writeBytes(src, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        this.leak.record();
        return super.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        this.leak.record();
        return super.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        this.leak.record();
        return super.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        this.leak.record();
        return super.writeBytes(src);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        this.leak.record();
        return super.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        this.leak.record();
        return super.writeBytes(in, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        this.leak.record();
        return super.writeZero(length);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        this.leak.record();
        return super.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        this.leak.record();
        return super.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        this.leak.record();
        return super.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        this.leak.record();
        return super.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteBufProcessor processor) {
        this.leak.record();
        return super.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteBufProcessor processor) {
        this.leak.record();
        return super.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor processor) {
        this.leak.record();
        return super.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
        this.leak.record();
        return super.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        this.leak.record();
        return super.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.leak.record();
        return super.copy(index, length);
    }

    @Override
    public int nioBufferCount() {
        this.leak.record();
        return super.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        this.leak.record();
        return super.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.leak.record();
        return super.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        this.leak.record();
        return super.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        this.leak.record();
        return super.nioBuffers(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.leak.record();
        return super.internalNioBuffer(index, length);
    }

    @Override
    public String toString(Charset charset) {
        this.leak.record();
        return super.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        this.leak.record();
        return super.toString(index, length, charset);
    }

    @Override
    public ByteBuf retain() {
        this.leak.record();
        return super.retain();
    }

    @Override
    public ByteBuf retain(int increment) {
        this.leak.record();
        return super.retain(increment);
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.leak.record();
        return super.capacity(newCapacity);
    }
}

