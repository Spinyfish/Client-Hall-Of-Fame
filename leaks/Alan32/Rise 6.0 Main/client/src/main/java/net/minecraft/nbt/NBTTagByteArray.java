package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
    /**
     * The byte array stored in the tag.
     */
    private byte[] data;

    NBTTagByteArray() {
    }

    public NBTTagByteArray(final byte[] data) {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput output) throws IOException {
        output.writeInt(this.data.length);
        output.write(this.data);
    }

    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(192L);
        final int i = input.readInt();
        sizeTracker.read(8 * i);
        this.data = new byte[i];
        input.readFully(this.data);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 7;
    }

    public String toString() {
        return "[" + this.data.length + " bytes]";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        final byte[] abyte = new byte[this.data.length];
        System.arraycopy(this.data, 0, abyte, 0, this.data.length);
        return new NBTTagByteArray(abyte);
    }

    public boolean equals(final Object p_equals_1_) {
        return super.equals(p_equals_1_) && Arrays.equals(this.data, ((NBTTagByteArray) p_equals_1_).data);
    }

    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.data);
    }

    public byte[] getByteArray() {
        return this.data;
    }
}
