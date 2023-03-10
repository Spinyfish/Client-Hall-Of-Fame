/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.ReadOnlyByteBuf;
import io.netty.buffer.ReadOnlyByteBufferBuf;
import io.netty.buffer.ReadOnlyUnsafeDirectByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.buffer.UnreleasableByteBuf;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class Unpooled {
    private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;
    public static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
    public static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    public static final ByteBuf EMPTY_BUFFER = ALLOC.buffer(0, 0);

    public static ByteBuf buffer() {
        return ALLOC.heapBuffer();
    }

    public static ByteBuf directBuffer() {
        return ALLOC.directBuffer();
    }

    public static ByteBuf buffer(int initialCapacity) {
        return ALLOC.heapBuffer(initialCapacity);
    }

    public static ByteBuf directBuffer(int initialCapacity) {
        return ALLOC.directBuffer(initialCapacity);
    }

    public static ByteBuf buffer(int initialCapacity, int maxCapacity) {
        return ALLOC.heapBuffer(initialCapacity, maxCapacity);
    }

    public static ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
        return ALLOC.directBuffer(initialCapacity, maxCapacity);
    }

    public static ByteBuf wrappedBuffer(byte[] array) {
        if (array.length == 0) {
            return EMPTY_BUFFER;
        }
        return new UnpooledHeapByteBuf(ALLOC, array, array.length);
    }

    public static ByteBuf wrappedBuffer(byte[] array, int offset, int length) {
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        if (offset == 0 && length == array.length) {
            return Unpooled.wrappedBuffer(array);
        }
        return Unpooled.wrappedBuffer(array).slice(offset, length);
    }

    public static ByteBuf wrappedBuffer(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return EMPTY_BUFFER;
        }
        if (buffer.hasArray()) {
            return Unpooled.wrappedBuffer(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining()).order(buffer.order());
        }
        if (PlatformDependent.hasUnsafe()) {
            if (buffer.isReadOnly()) {
                if (buffer.isDirect()) {
                    return new ReadOnlyUnsafeDirectByteBuf(ALLOC, buffer);
                }
                return new ReadOnlyByteBufferBuf(ALLOC, buffer);
            }
            return new UnpooledUnsafeDirectByteBuf(ALLOC, buffer, buffer.remaining());
        }
        if (buffer.isReadOnly()) {
            return new ReadOnlyByteBufferBuf(ALLOC, buffer);
        }
        return new UnpooledDirectByteBuf(ALLOC, buffer, buffer.remaining());
    }

    public static ByteBuf wrappedBuffer(ByteBuf buffer) {
        if (buffer.isReadable()) {
            return buffer.slice();
        }
        return EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(byte[] ... arrays) {
        return Unpooled.wrappedBuffer(16, arrays);
    }

    public static ByteBuf wrappedBuffer(ByteBuf ... buffers) {
        return Unpooled.wrappedBuffer(16, buffers);
    }

    public static ByteBuf wrappedBuffer(ByteBuffer ... buffers) {
        return Unpooled.wrappedBuffer(16, buffers);
    }

    public static ByteBuf wrappedBuffer(int maxNumComponents, byte[] ... arrays) {
        switch (arrays.length) {
            case 0: {
                break;
            }
            case 1: {
                if (arrays[0].length == 0) break;
                return Unpooled.wrappedBuffer(arrays[0]);
            }
            default: {
                ArrayList<ByteBuf> components = new ArrayList<ByteBuf>(arrays.length);
                for (byte[] a : arrays) {
                    if (a == null) break;
                    if (a.length <= 0) continue;
                    components.add(Unpooled.wrappedBuffer(a));
                }
                if (components.isEmpty()) break;
                return new CompositeByteBuf(ALLOC, false, maxNumComponents, components);
            }
        }
        return EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuf ... buffers) {
        switch (buffers.length) {
            case 0: {
                break;
            }
            case 1: {
                if (!buffers[0].isReadable()) break;
                return Unpooled.wrappedBuffer(buffers[0].order(BIG_ENDIAN));
            }
            default: {
                for (ByteBuf b : buffers) {
                    if (!b.isReadable()) continue;
                    return new CompositeByteBuf(ALLOC, false, maxNumComponents, buffers);
                }
            }
        }
        return EMPTY_BUFFER;
    }

    public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuffer ... buffers) {
        switch (buffers.length) {
            case 0: {
                break;
            }
            case 1: {
                if (!buffers[0].hasRemaining()) break;
                return Unpooled.wrappedBuffer(buffers[0].order(BIG_ENDIAN));
            }
            default: {
                ArrayList<ByteBuf> components = new ArrayList<ByteBuf>(buffers.length);
                for (ByteBuffer b : buffers) {
                    if (b == null) break;
                    if (b.remaining() <= 0) continue;
                    components.add(Unpooled.wrappedBuffer(b.order(BIG_ENDIAN)));
                }
                if (components.isEmpty()) break;
                return new CompositeByteBuf(ALLOC, false, maxNumComponents, components);
            }
        }
        return EMPTY_BUFFER;
    }

    public static CompositeByteBuf compositeBuffer() {
        return Unpooled.compositeBuffer(16);
    }

    public static CompositeByteBuf compositeBuffer(int maxNumComponents) {
        return new CompositeByteBuf(ALLOC, false, maxNumComponents);
    }

    public static ByteBuf copiedBuffer(byte[] array) {
        if (array.length == 0) {
            return EMPTY_BUFFER;
        }
        return Unpooled.wrappedBuffer((byte[])array.clone());
    }

    public static ByteBuf copiedBuffer(byte[] array, int offset, int length) {
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] copy = new byte[length];
        System.arraycopy(array, offset, copy, 0, length);
        return Unpooled.wrappedBuffer(copy);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuf copiedBuffer(ByteBuffer buffer) {
        int length = buffer.remaining();
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] copy = new byte[length];
        int position = buffer.position();
        try {
            buffer.get(copy);
        }
        finally {
            buffer.position(position);
        }
        return Unpooled.wrappedBuffer(copy).order(buffer.order());
    }

    public static ByteBuf copiedBuffer(ByteBuf buffer) {
        int readable = buffer.readableBytes();
        if (readable > 0) {
            ByteBuf copy = Unpooled.buffer(readable);
            copy.writeBytes(buffer, buffer.readerIndex(), readable);
            return copy;
        }
        return EMPTY_BUFFER;
    }

    public static ByteBuf copiedBuffer(byte[] ... arrays) {
        switch (arrays.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                if (arrays[0].length == 0) {
                    return EMPTY_BUFFER;
                }
                return Unpooled.copiedBuffer(arrays[0]);
            }
        }
        int length = 0;
        for (byte[] a : arrays) {
            if (Integer.MAX_VALUE - length < a.length) {
                throw new IllegalArgumentException("The total length of the specified arrays is too big.");
            }
            length += a.length;
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] mergedArray = new byte[length];
        int j = 0;
        for (int i = 0; i < arrays.length; ++i) {
            byte[] a;
            a = arrays[i];
            System.arraycopy(a, 0, mergedArray, j, a.length);
            j += a.length;
        }
        return Unpooled.wrappedBuffer(mergedArray);
    }

    public static ByteBuf copiedBuffer(ByteBuf ... buffers) {
        int bLen;
        switch (buffers.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                return Unpooled.copiedBuffer(buffers[0]);
            }
        }
        ByteOrder order = null;
        int length = 0;
        for (ByteBuf b : buffers) {
            bLen = b.readableBytes();
            if (bLen <= 0) continue;
            if (Integer.MAX_VALUE - length < bLen) {
                throw new IllegalArgumentException("The total length of the specified buffers is too big.");
            }
            length += bLen;
            if (order != null) {
                if (order.equals(b.order())) continue;
                throw new IllegalArgumentException("inconsistent byte order");
            }
            order = b.order();
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] mergedArray = new byte[length];
        int j = 0;
        for (int i = 0; i < buffers.length; ++i) {
            ByteBuf b;
            b = buffers[i];
            bLen = b.readableBytes();
            b.getBytes(b.readerIndex(), mergedArray, j, bLen);
            j += bLen;
        }
        return Unpooled.wrappedBuffer(mergedArray).order(order);
    }

    public static ByteBuf copiedBuffer(ByteBuffer ... buffers) {
        int bLen;
        switch (buffers.length) {
            case 0: {
                return EMPTY_BUFFER;
            }
            case 1: {
                return Unpooled.copiedBuffer(buffers[0]);
            }
        }
        ByteOrder order = null;
        int length = 0;
        for (ByteBuffer b : buffers) {
            bLen = b.remaining();
            if (bLen <= 0) continue;
            if (Integer.MAX_VALUE - length < bLen) {
                throw new IllegalArgumentException("The total length of the specified buffers is too big.");
            }
            length += bLen;
            if (order != null) {
                if (order.equals(b.order())) continue;
                throw new IllegalArgumentException("inconsistent byte order");
            }
            order = b.order();
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] mergedArray = new byte[length];
        int j = 0;
        for (int i = 0; i < buffers.length; ++i) {
            ByteBuffer b;
            b = buffers[i];
            bLen = b.remaining();
            int oldPos = b.position();
            b.get(mergedArray, j, bLen);
            b.position(oldPos);
            j += bLen;
        }
        return Unpooled.wrappedBuffer(mergedArray).order(order);
    }

    public static ByteBuf copiedBuffer(CharSequence string, Charset charset) {
        if (string == null) {
            throw new NullPointerException("string");
        }
        if (string instanceof CharBuffer) {
            return Unpooled.copiedBuffer((CharBuffer)string, charset);
        }
        return Unpooled.copiedBuffer(CharBuffer.wrap(string), charset);
    }

    public static ByteBuf copiedBuffer(CharSequence string, int offset, int length, Charset charset) {
        if (string == null) {
            throw new NullPointerException("string");
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        if (string instanceof CharBuffer) {
            CharBuffer buf = (CharBuffer)string;
            if (buf.hasArray()) {
                return Unpooled.copiedBuffer(buf.array(), buf.arrayOffset() + buf.position() + offset, length, charset);
            }
            buf = buf.slice();
            buf.limit(length);
            buf.position(offset);
            return Unpooled.copiedBuffer(buf, charset);
        }
        return Unpooled.copiedBuffer(CharBuffer.wrap(string, offset, offset + length), charset);
    }

    public static ByteBuf copiedBuffer(char[] array, Charset charset) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        return Unpooled.copiedBuffer(array, 0, array.length, charset);
    }

    public static ByteBuf copiedBuffer(char[] array, int offset, int length, Charset charset) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        return Unpooled.copiedBuffer(CharBuffer.wrap(array, offset, length), charset);
    }

    private static ByteBuf copiedBuffer(CharBuffer buffer, Charset charset) {
        return ByteBufUtil.encodeString0(ALLOC, true, buffer, charset);
    }

    public static ByteBuf unmodifiableBuffer(ByteBuf buffer) {
        ByteOrder endianness = buffer.order();
        if (endianness == BIG_ENDIAN) {
            return new ReadOnlyByteBuf(buffer);
        }
        return new ReadOnlyByteBuf(buffer.order(BIG_ENDIAN)).order(LITTLE_ENDIAN);
    }

    public static ByteBuf copyInt(int value) {
        ByteBuf buf = Unpooled.buffer(4);
        buf.writeInt(value);
        return buf;
    }

    public static ByteBuf copyInt(int ... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer(values.length * 4);
        for (int v : values) {
            buffer.writeInt(v);
        }
        return buffer;
    }

    public static ByteBuf copyShort(int value) {
        ByteBuf buf = Unpooled.buffer(2);
        buf.writeShort(value);
        return buf;
    }

    public static ByteBuf copyShort(short ... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer(values.length * 2);
        for (short v : values) {
            buffer.writeShort(v);
        }
        return buffer;
    }

    public static ByteBuf copyShort(int ... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer(values.length * 2);
        for (int v : values) {
            buffer.writeShort(v);
        }
        return buffer;
    }

    public static ByteBuf copyMedium(int value) {
        ByteBuf buf = Unpooled.buffer(3);
        buf.writeMedium(value);
        return buf;
    }

    public static ByteBuf copyMedium(int ... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer(values.length * 3);
        for (int v : values) {
            buffer.writeMedium(v);
        }
        return buffer;
    }

    public static ByteBuf copyLong(long value) {
        ByteBuf buf = Unpooled.buffer(8);
        buf.writeLong(value);
        return buf;
    }

    public static ByteBuf copyLong(long ... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer(values.length * 8);
        for (long v : values) {
            buffer.writeLong(v);
        }
        return buffer;
    }

    public static ByteBuf copyBoolean(boolean value) {
        ByteBuf buf = Unpooled.buffer(1);
        buf.writeBoolean(value);
        return buf;
    }

    public static ByteBuf copyBoolean(boolean ... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer(values.length);
        for (boolean v : values) {
            buffer.writeBoolean(v);
        }
        return buffer;
    }

    public static ByteBuf copyFloat(float value) {
        ByteBuf buf = Unpooled.buffer(4);
        buf.writeFloat(value);
        return buf;
    }

    public static ByteBuf copyFloat(float ... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer(values.length * 4);
        for (float v : values) {
            buffer.writeFloat(v);
        }
        return buffer;
    }

    public static ByteBuf copyDouble(double value) {
        ByteBuf buf = Unpooled.buffer(8);
        buf.writeDouble(value);
        return buf;
    }

    public static ByteBuf copyDouble(double ... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuf buffer = Unpooled.buffer(values.length * 8);
        for (double v : values) {
            buffer.writeDouble(v);
        }
        return buffer;
    }

    public static ByteBuf unreleasableBuffer(ByteBuf buf) {
        return new UnreleasableByteBuf(buf);
    }

    private Unpooled() {
    }
}

