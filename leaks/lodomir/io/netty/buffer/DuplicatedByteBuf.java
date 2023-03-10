/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractDerivedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class DuplicatedByteBuf
extends AbstractDerivedByteBuf {
    private final ByteBuf buffer;

    public DuplicatedByteBuf(ByteBuf buffer) {
        super(buffer.maxCapacity());
        this.buffer = buffer instanceof DuplicatedByteBuf ? ((DuplicatedByteBuf)buffer).buffer : buffer;
        this.setIndex(buffer.readerIndex(), buffer.writerIndex());
    }

    @Override
    public ByteBuf unwrap() {
        return this.buffer;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buffer.alloc();
    }

    @Override
    public ByteOrder order() {
        return this.buffer.order();
    }

    @Override
    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    @Override
    public int capacity() {
        return this.buffer.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.buffer.capacity(newCapacity);
        return this;
    }

    @Override
    public boolean hasArray() {
        return this.buffer.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buffer.array();
    }

    @Override
    public int arrayOffset() {
        return this.buffer.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buffer.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.buffer.memoryAddress();
    }

    @Override
    public byte getByte(int index) {
        return this._getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        return this.buffer.getByte(index);
    }

    @Override
    public short getShort(int index) {
        return this._getShort(index);
    }

    @Override
    protected short _getShort(int index) {
        return this.buffer.getShort(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this._getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return this.buffer.getUnsignedMedium(index);
    }

    @Override
    public int getInt(int index) {
        return this._getInt(index);
    }

    @Override
    protected int _getInt(int index) {
        return this.buffer.getInt(index);
    }

    @Override
    public long getLong(int index) {
        return this._getLong(index);
    }

    @Override
    protected long _getLong(int index) {
        return this.buffer.getLong(index);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.buffer.copy(index, length);
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.buffer.slice(index, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.buffer.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this._setByte(index, value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        this.buffer.setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this._setShort(index, value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        this.buffer.setShort(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this._setMedium(index, value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        this.buffer.setMedium(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this._setInt(index, value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        this.buffer.setInt(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this._setLong(index, value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        this.buffer.setLong(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.buffer.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.buffer.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.buffer.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.buffer.getBytes(index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.buffer.getBytes(index, out, length);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.buffer.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.buffer.setBytes(index, in, length);
    }

    @Override
    public int nioBufferCount() {
        return this.buffer.nioBufferCount();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.buffer.nioBuffers(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.nioBuffer(index, length);
    }

    @Override
    public int forEachByte(int index, int length, ByteBufProcessor processor) {
        return this.buffer.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
        return this.buffer.forEachByteDesc(index, length, processor);
    }
}

