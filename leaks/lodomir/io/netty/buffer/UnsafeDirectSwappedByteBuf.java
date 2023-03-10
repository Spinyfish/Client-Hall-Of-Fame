/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteOrder;

final class UnsafeDirectSwappedByteBuf
extends SwappedByteBuf {
    private static final boolean NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    private final boolean nativeByteOrder;
    private final AbstractByteBuf wrapped;

    UnsafeDirectSwappedByteBuf(AbstractByteBuf buf) {
        super(buf);
        this.wrapped = buf;
        this.nativeByteOrder = NATIVE_ORDER == (this.order() == ByteOrder.BIG_ENDIAN);
    }

    private long addr(int index) {
        return this.wrapped.memoryAddress() + (long)index;
    }

    @Override
    public long getLong(int index) {
        this.wrapped.checkIndex(index, 8);
        long v = PlatformDependent.getLong(this.addr(index));
        return this.nativeByteOrder ? v : Long.reverseBytes(v);
    }

    @Override
    public float getFloat(int index) {
        return Float.intBitsToFloat(this.getInt(index));
    }

    @Override
    public double getDouble(int index) {
        return Double.longBitsToDouble(this.getLong(index));
    }

    @Override
    public char getChar(int index) {
        return (char)this.getShort(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return (long)this.getInt(index) & 0xFFFFFFFFL;
    }

    @Override
    public int getInt(int index) {
        this.wrapped.checkIndex(index, 4);
        int v = PlatformDependent.getInt(this.addr(index));
        return this.nativeByteOrder ? v : Integer.reverseBytes(v);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.getShort(index) & 0xFFFF;
    }

    @Override
    public short getShort(int index) {
        this.wrapped.checkIndex(index, 2);
        short v = PlatformDependent.getShort(this.addr(index));
        return this.nativeByteOrder ? v : Short.reverseBytes(v);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.wrapped.checkIndex(index, 2);
        this._setShort(index, value);
        return this;
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.wrapped.checkIndex(index, 4);
        this._setInt(index, value);
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.wrapped.checkIndex(index, 8);
        this._setLong(index, value);
        return this;
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        this.setShort(index, value);
        return this;
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        this.setInt(index, Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        this.setLong(index, Double.doubleToRawLongBits(value));
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        this.wrapped.ensureAccessible();
        this.wrapped.ensureWritable(2);
        this._setShort(this.wrapped.writerIndex, value);
        this.wrapped.writerIndex += 2;
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        this.wrapped.ensureAccessible();
        this.wrapped.ensureWritable(4);
        this._setInt(this.wrapped.writerIndex, value);
        this.wrapped.writerIndex += 4;
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        this.wrapped.ensureAccessible();
        this.wrapped.ensureWritable(8);
        this._setLong(this.wrapped.writerIndex, value);
        this.wrapped.writerIndex += 8;
        return this;
    }

    @Override
    public ByteBuf writeChar(int value) {
        this.writeShort(value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        this.writeInt(Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        this.writeLong(Double.doubleToRawLongBits(value));
        return this;
    }

    private void _setShort(int index, int value) {
        PlatformDependent.putShort(this.addr(index), this.nativeByteOrder ? (short)value : Short.reverseBytes((short)value));
    }

    private void _setInt(int index, int value) {
        PlatformDependent.putInt(this.addr(index), this.nativeByteOrder ? value : Integer.reverseBytes(value));
    }

    private void _setLong(int index, long value) {
        PlatformDependent.putLong(this.addr(index), this.nativeByteOrder ? value : Long.reverseBytes(value));
    }
}

