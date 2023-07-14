/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;

public class NBTTagLong
extends NBTBase.NBTPrimitive {
    private long data;

    NBTTagLong() {
    }

    public NBTTagLong(long data) {
        this.data = data;
    }

    @Override
    void write(DataOutput output) throws IOException {
        output.writeLong(this.data);
    }

    @Override
    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(128L);
        this.data = input.readLong();
    }

    @Override
    public byte getId() {
        return 4;
    }

    @Override
    public String toString() {
        return "" + this.data + "L";
    }

    @Override
    public NBTBase copy() {
        return new NBTTagLong(this.data);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            NBTTagLong nbttaglong = (NBTTagLong)p_equals_1_;
            return this.data == nbttaglong.data;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (int)(this.data ^ this.data >>> 32);
    }

    @Override
    public long getLong() {
        return this.data;
    }

    @Override
    public int getInt() {
        return (int)(this.data & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public short getShort() {
        return (short)(this.data & 0xFFFFL);
    }

    @Override
    public byte getByte() {
        return (byte)(this.data & 0xFFL);
    }

    @Override
    public double getDouble() {
        return this.data;
    }

    @Override
    public float getFloat() {
        return this.data;
    }
}

