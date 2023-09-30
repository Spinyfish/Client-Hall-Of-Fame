package net.minecraft.nbt;

import net.minecraft.util.MathHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase.NBTPrimitive {
    /**
     * The double value for the tag.
     */
    private double data;

    NBTTagDouble() {
    }

    public NBTTagDouble(final double data) {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput output) throws IOException {
        output.writeDouble(this.data);
    }

    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(128L);
        this.data = input.readDouble();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 6;
    }

    public String toString() {
        return "" + this.data + "d";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        return new NBTTagDouble(this.data);
    }

    public boolean equals(final Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            final NBTTagDouble nbttagdouble = (NBTTagDouble) p_equals_1_;
            return this.data == nbttagdouble.data;
        } else {
            return false;
        }
    }

    public int hashCode() {
        final long i = Double.doubleToLongBits(this.data);
        return super.hashCode() ^ (int) (i ^ i >>> 32);
    }

    public long getLong() {
        return (long) Math.floor(this.data);
    }

    public int getInt() {
        return MathHelper.floor_double(this.data);
    }

    public short getShort() {
        return (short) (MathHelper.floor_double(this.data) & 65535);
    }

    public byte getByte() {
        return (byte) (MathHelper.floor_double(this.data) & 255);
    }

    public double getDouble() {
        return this.data;
    }

    public float getFloat() {
        return (float) this.data;
    }
}
