/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WeakMemoryHolder;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

public class Memory
extends Pointer {
    private static final Map<Memory, Reference<Memory>> allocatedMemory = Collections.synchronizedMap(new WeakHashMap());
    private static final WeakMemoryHolder buffers = new WeakMemoryHolder();
    protected long size;

    public static void purge() {
        buffers.clean();
    }

    public static void disposeAll() {
        LinkedList<Memory> refs = new LinkedList<Memory>(allocatedMemory.keySet());
        for (Memory r : refs) {
            r.dispose();
        }
    }

    public Memory(long size) {
        this.size = size;
        if (size <= 0L) {
            throw new IllegalArgumentException("Allocation size must be greater than zero");
        }
        this.peer = Memory.malloc(size);
        if (this.peer == 0L) {
            throw new OutOfMemoryError("Cannot allocate " + size + " bytes");
        }
        allocatedMemory.put(this, new WeakReference<Memory>(this));
    }

    protected Memory() {
    }

    @Override
    public Pointer share(long offset) {
        return this.share(offset, this.size() - offset);
    }

    @Override
    public Pointer share(long offset, long sz) {
        this.boundsCheck(offset, sz);
        return new SharedMemory(offset, sz);
    }

    public Memory align(int byteBoundary) {
        if (byteBoundary <= 0) {
            throw new IllegalArgumentException("Byte boundary must be positive: " + byteBoundary);
        }
        for (int i = 0; i < 32; ++i) {
            if (byteBoundary != 1 << i) continue;
            long mask = (long)byteBoundary - 1L ^ 0xFFFFFFFFFFFFFFFFL;
            if ((this.peer & mask) != this.peer) {
                long newPeer = this.peer + (long)byteBoundary - 1L & mask;
                long newSize = this.peer + this.size - newPeer;
                if (newSize <= 0L) {
                    throw new IllegalArgumentException("Insufficient memory to align to the requested boundary");
                }
                return (Memory)this.share(newPeer - this.peer, newSize);
            }
            return this;
        }
        throw new IllegalArgumentException("Byte boundary must be a power of two");
    }

    protected void finalize() {
        this.dispose();
    }

    protected synchronized void dispose() {
        try {
            Memory.free(this.peer);
        }
        finally {
            allocatedMemory.remove(this);
            this.peer = 0L;
        }
    }

    public void clear() {
        this.clear(this.size);
    }

    public boolean valid() {
        return this.peer != 0L;
    }

    public long size() {
        return this.size;
    }

    protected void boundsCheck(long off, long sz) {
        if (off < 0L) {
            throw new IndexOutOfBoundsException("Invalid offset: " + off);
        }
        if (off + sz > this.size) {
            String msg = "Bounds exceeds available space : size=" + this.size + ", offset=" + (off + sz);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    @Override
    public void read(long bOff, byte[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 1L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, short[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 2L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, char[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 2L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, int[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 4L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, long[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 8L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, float[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 4L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, double[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 8L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, byte[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 1L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, short[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 2L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, char[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 2L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, int[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 4L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, long[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 8L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, float[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 4L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, double[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 8L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public byte getByte(long offset) {
        this.boundsCheck(offset, 1L);
        return super.getByte(offset);
    }

    @Override
    public char getChar(long offset) {
        this.boundsCheck(offset, 1L);
        return super.getChar(offset);
    }

    @Override
    public short getShort(long offset) {
        this.boundsCheck(offset, 2L);
        return super.getShort(offset);
    }

    @Override
    public int getInt(long offset) {
        this.boundsCheck(offset, 4L);
        return super.getInt(offset);
    }

    @Override
    public long getLong(long offset) {
        this.boundsCheck(offset, 8L);
        return super.getLong(offset);
    }

    @Override
    public float getFloat(long offset) {
        this.boundsCheck(offset, 4L);
        return super.getFloat(offset);
    }

    @Override
    public double getDouble(long offset) {
        this.boundsCheck(offset, 8L);
        return super.getDouble(offset);
    }

    @Override
    public Pointer getPointer(long offset) {
        this.boundsCheck(offset, Pointer.SIZE);
        return super.getPointer(offset);
    }

    @Override
    public ByteBuffer getByteBuffer(long offset, long length) {
        this.boundsCheck(offset, length);
        ByteBuffer b = super.getByteBuffer(offset, length);
        buffers.put(b, this);
        return b;
    }

    @Override
    public String getString(long offset, String encoding) {
        this.boundsCheck(offset, 0L);
        return super.getString(offset, encoding);
    }

    @Override
    public String getWideString(long offset) {
        this.boundsCheck(offset, 0L);
        return super.getWideString(offset);
    }

    @Override
    public void setByte(long offset, byte value) {
        this.boundsCheck(offset, 1L);
        super.setByte(offset, value);
    }

    @Override
    public void setChar(long offset, char value) {
        this.boundsCheck(offset, Native.WCHAR_SIZE);
        super.setChar(offset, value);
    }

    @Override
    public void setShort(long offset, short value) {
        this.boundsCheck(offset, 2L);
        super.setShort(offset, value);
    }

    @Override
    public void setInt(long offset, int value) {
        this.boundsCheck(offset, 4L);
        super.setInt(offset, value);
    }

    @Override
    public void setLong(long offset, long value) {
        this.boundsCheck(offset, 8L);
        super.setLong(offset, value);
    }

    @Override
    public void setFloat(long offset, float value) {
        this.boundsCheck(offset, 4L);
        super.setFloat(offset, value);
    }

    @Override
    public void setDouble(long offset, double value) {
        this.boundsCheck(offset, 8L);
        super.setDouble(offset, value);
    }

    @Override
    public void setPointer(long offset, Pointer value) {
        this.boundsCheck(offset, Pointer.SIZE);
        super.setPointer(offset, value);
    }

    @Override
    public void setString(long offset, String value, String encoding) {
        this.boundsCheck(offset, (long)Native.getBytes(value, encoding).length + 1L);
        super.setString(offset, value, encoding);
    }

    @Override
    public void setWideString(long offset, String value) {
        this.boundsCheck(offset, ((long)value.length() + 1L) * (long)Native.WCHAR_SIZE);
        super.setWideString(offset, value);
    }

    @Override
    public String toString() {
        return "allocated@0x" + Long.toHexString(this.peer) + " (" + this.size + " bytes)";
    }

    protected static void free(long p) {
        if (p != 0L) {
            Native.free(p);
        }
    }

    protected static long malloc(long size) {
        return Native.malloc(size);
    }

    public String dump() {
        return this.dump(0L, (int)this.size());
    }

    private class SharedMemory
    extends Memory {
        public SharedMemory(long offset, long size) {
            this.size = size;
            this.peer = Memory.this.peer + offset;
        }

        @Override
        protected void dispose() {
            this.peer = 0L;
        }

        @Override
        protected void boundsCheck(long off, long sz) {
            Memory.this.boundsCheck(this.peer - Memory.this.peer + off, sz);
        }

        @Override
        public String toString() {
            return super.toString() + " (shared from " + Memory.this.toString() + ")";
        }
    }
}

