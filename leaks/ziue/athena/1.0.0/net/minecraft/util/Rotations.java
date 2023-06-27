package net.minecraft.util;

import net.minecraft.nbt.*;

public class Rotations
{
    protected final float x;
    protected final float y;
    protected final float z;
    
    public Rotations(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Rotations(final NBTTagList nbt) {
        this.x = nbt.getFloatAt(0);
        this.y = nbt.getFloatAt(1);
        this.z = nbt.getFloatAt(2);
    }
    
    public NBTTagList writeToNBT() {
        final NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.appendTag(new NBTTagFloat(this.x));
        nbttaglist.appendTag(new NBTTagFloat(this.y));
        nbttaglist.appendTag(new NBTTagFloat(this.z));
        return nbttaglist;
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        if (!(p_equals_1_ instanceof Rotations)) {
            return false;
        }
        final Rotations rotations = (Rotations)p_equals_1_;
        return this.x == rotations.x && this.y == rotations.y && this.z == rotations.z;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getZ() {
        return this.z;
    }
}
