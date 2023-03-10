/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagScope;
import java.io.DataInput;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TrackingDataInput
implements DataInput,
BinaryTagScope {
    private static final int MAX_DEPTH = 512;
    private final DataInput input;
    private final long maxLength;
    private long counter;
    private int depth;

    TrackingDataInput(DataInput input, long maxLength) {
        this.input = input;
        this.maxLength = maxLength;
    }

    public static BinaryTagScope enter(DataInput input) throws IOException {
        if (input instanceof TrackingDataInput) {
            return ((TrackingDataInput)input).enter();
        }
        return BinaryTagScope.NoOp.INSTANCE;
    }

    public static BinaryTagScope enter(DataInput input, long expectedSize) throws IOException {
        if (input instanceof TrackingDataInput) {
            return ((TrackingDataInput)input).enter(expectedSize);
        }
        return BinaryTagScope.NoOp.INSTANCE;
    }

    public DataInput input() {
        return this.input;
    }

    public TrackingDataInput enter(long expectedSize) throws IOException {
        if (this.depth++ > 512) {
            throw new IOException("NBT read exceeded maximum depth of 512");
        }
        this.ensureMaxLength(expectedSize);
        return this;
    }

    public TrackingDataInput enter() throws IOException {
        if (this.depth++ > 512) {
            throw new IOException("NBT read exceeded maximum depth of 512");
        }
        this.ensureMaxLength(0L);
        return this;
    }

    public void exit() throws IOException {
        --this.depth;
        this.ensureMaxLength(0L);
    }

    private void ensureMaxLength(long expected) throws IOException {
        if (this.maxLength > 0L && this.counter + expected > this.maxLength) {
            throw new IOException("The read NBT was longer than the maximum allowed size of " + this.maxLength + " bytes!");
        }
    }

    @Override
    public void readFully(byte @NotNull [] array) throws IOException {
        this.counter += (long)array.length;
        this.input.readFully(array);
    }

    @Override
    public void readFully(byte @NotNull [] array, int off, int len) throws IOException {
        this.counter += (long)len;
        this.input.readFully(array, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.input.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        ++this.counter;
        return this.input.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        ++this.counter;
        return this.input.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        ++this.counter;
        return this.input.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        this.counter += 2L;
        return this.input.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        this.counter += 2L;
        return this.input.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        this.counter += 2L;
        return this.input.readChar();
    }

    @Override
    public int readInt() throws IOException {
        this.counter += 4L;
        return this.input.readInt();
    }

    @Override
    public long readLong() throws IOException {
        this.counter += 8L;
        return this.input.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        this.counter += 4L;
        return this.input.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        this.counter += 8L;
        return this.input.readDouble();
    }

    @Override
    @Nullable
    public String readLine() throws IOException {
        @Nullable String result = this.input.readLine();
        if (result != null) {
            this.counter += (long)(result.length() + 1);
        }
        return result;
    }

    @Override
    @NotNull
    public String readUTF() throws IOException {
        String result = this.input.readUTF();
        this.counter += (long)result.length() * 2L + 2L;
        return result;
    }

    @Override
    public void close() throws IOException {
        this.exit();
    }
}

