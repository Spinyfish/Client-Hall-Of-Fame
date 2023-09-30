package net.minecraft.nbt;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class NBTTagList extends NBTBase {
    private static final Logger LOGGER = LogManager.getLogger();
    private List<NBTBase> tagList = Lists.newArrayList();

    /**
     * The type byte for the tags in the list - they must all be of the same type.
     */
    private byte tagType = 0;

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput output) throws IOException {
        if (!this.tagList.isEmpty()) {
            this.tagType = this.tagList.get(0).getId();
        } else {
            this.tagType = 0;
        }

        output.writeByte(this.tagType);
        output.writeInt(this.tagList.size());

        for (int i = 0; i < this.tagList.size(); ++i) {
            this.tagList.get(i).write(output);
        }
    }

    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(296L);

        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagType = input.readByte();
            final int i = input.readInt();

            if (this.tagType == 0 && i > 0) {
                throw new RuntimeException("Missing type on ListTag");
            } else {
                sizeTracker.read(32L * (long) i);
                this.tagList = Lists.newArrayListWithCapacity(i);

                for (int j = 0; j < i; ++j) {
                    final NBTBase nbtbase = NBTBase.createNewByType(this.tagType);
                    nbtbase.read(input, depth + 1, sizeTracker);
                    this.tagList.add(nbtbase);
                }
            }
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 9;
    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("[");

        for (int i = 0; i < this.tagList.size(); ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(i).append(':').append(this.tagList.get(i));
        }

        return stringbuilder.append(']').toString();
    }

    /**
     * Adds the provided tag to the end of the list. There is no check to verify this tag is of the same type as any
     * previous tag.
     */
    public void appendTag(final NBTBase nbt) {
        if (nbt.getId() == 0) {
            LOGGER.warn("Invalid TagEnd added to ListTag");
        } else {
            if (this.tagType == 0) {
                this.tagType = nbt.getId();
            } else if (this.tagType != nbt.getId()) {
                LOGGER.warn("Adding mismatching tag types to tag list");
                return;
            }

            this.tagList.add(nbt);
        }
    }

    /**
     * Set the given index to the given tag
     */
    public void set(final int idx, final NBTBase nbt) {
        if (nbt.getId() == 0) {
            LOGGER.warn("Invalid TagEnd added to ListTag");
        } else if (idx >= 0 && idx < this.tagList.size()) {
            if (this.tagType == 0) {
                this.tagType = nbt.getId();
            } else if (this.tagType != nbt.getId()) {
                LOGGER.warn("Adding mismatching tag types to tag list");
                return;
            }

            this.tagList.set(idx, nbt);
        } else {
            LOGGER.warn("index out of bounds to set tag in tag list");
        }
    }

    /**
     * Removes a tag at the given index.
     */
    public NBTBase removeTag(final int i) {
        return this.tagList.remove(i);
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return this.tagList.isEmpty();
    }

    /**
     * Retrieves the NBTTagCompound at the specified index in the list
     */
    public NBTTagCompound getCompoundTagAt(final int i) {
        if (i >= 0 && i < this.tagList.size()) {
            final NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 10 ? (NBTTagCompound) nbtbase : new NBTTagCompound();
        } else {
            return new NBTTagCompound();
        }
    }

    public int[] getIntArrayAt(final int i) {
        if (i >= 0 && i < this.tagList.size()) {
            final NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 11 ? ((NBTTagIntArray) nbtbase).getIntArray() : new int[0];
        } else {
            return new int[0];
        }
    }

    public double getDoubleAt(final int i) {
        if (i >= 0 && i < this.tagList.size()) {
            final NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 6 ? ((NBTTagDouble) nbtbase).getDouble() : 0.0D;
        } else {
            return 0.0D;
        }
    }

    public float getFloatAt(final int i) {
        if (i >= 0 && i < this.tagList.size()) {
            final NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 5 ? ((NBTTagFloat) nbtbase).getFloat() : 0.0F;
        } else {
            return 0.0F;
        }
    }

    /**
     * Retrieves the tag String value at the specified index in the list
     */
    public String getStringTagAt(final int i) {
        if (i >= 0 && i < this.tagList.size()) {
            final NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 8 ? nbtbase.getString() : nbtbase.toString();
        } else {
            return "";
        }
    }

    /**
     * Get the tag at the given position
     */
    public NBTBase get(final int idx) {
        return idx >= 0 && idx < this.tagList.size() ? this.tagList.get(idx) : new NBTTagEnd();
    }

    /**
     * Returns the number of tags in the list.
     */
    public int tagCount() {
        return this.tagList.size();
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        final NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.tagType = this.tagType;

        for (final NBTBase nbtbase : this.tagList) {
            final NBTBase nbtbase1 = nbtbase.copy();
            nbttaglist.tagList.add(nbtbase1);
        }

        return nbttaglist;
    }

    public boolean equals(final Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            final NBTTagList nbttaglist = (NBTTagList) p_equals_1_;

            if (this.tagType == nbttaglist.tagType) {
                return this.tagList.equals(nbttaglist.tagList);
            }
        }

        return false;
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagList.hashCode();
    }

    public int getTagType() {
        return this.tagType;
    }
}
