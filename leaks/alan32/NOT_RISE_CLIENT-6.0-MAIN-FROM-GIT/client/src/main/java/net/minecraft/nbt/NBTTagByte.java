package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase.NBTPrimitive {
    /**
     * The byte value for the tag.
     */
    private byte data;

    NBTTagByte() {
    }

    public NBTTagByte(final byte data) {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput output) throws IOException {
        output.writeByte(this.data);
    }

    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(72L);
        this.data = input.readByte();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 1;
    }

    public String toString() {
        return "" + this.data + "b";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        return new NBTTagByte(this.data);
    }

    public boolean equals(final Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            final NBTTagByte nbttagbyte = (NBTTagByte) p_equals_1_;
            return this.data == nbttagbyte.data;
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
        return this.data;
    }

    public double getDouble() {
        return this.data;
    }

    public float getFloat() {
        return this.data;
    }
}
