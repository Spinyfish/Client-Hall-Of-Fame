package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase.NBTPrimitive {
    /**
     * The short value for the tag.
     */
    private short data;

    public NBTTagShort() {
    }

    public NBTTagShort(final short data) {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput output) throws IOException {
        output.writeShort(this.data);
    }

    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(80L);
        this.data = input.readShort();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 2;
    }

    public String toString() {
        return "" + this.data + "s";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        return new NBTTagShort(this.data);
    }

    public boolean equals(final Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            final NBTTagShort nbttagshort = (NBTTagShort) p_equals_1_;
            return this.data == nbttagshort.data;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    public long getLong() {
        return this.data;
    }

    public int getInt() {
        return this.data;
    }

    public short getShort() {
        return this.data;
    }

    public byte getByte() {
        return (byte) (this.data & 255);
    }

    public double getDouble() {
        return this.data;
    }

    public float getFloat() {
        return this.data;
    }
}
