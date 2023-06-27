package net.minecraft.nbt;

import java.io.*;

public class NBTTagEnd extends NBTBase
{
    @Override
    void read(final DataInput input, final int depth, final NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(64L);
    }
    
    @Override
    void write(final DataOutput output) throws IOException {
    }
    
    @Override
    public byte getId() {
        return 0;
    }
    
    @Override
    public String toString() {
        return "END";
    }
    
    @Override
    public NBTBase copy() {
        return new NBTTagEnd();
    }
}
