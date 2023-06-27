package net.minecraft.nbt;

public class NBTSizeTracker
{
    public static final NBTSizeTracker INFINITE;
    private final long max;
    private long read;
    
    public NBTSizeTracker(final long max) {
        this.max = max;
    }
    
    public void read(final long bits) {
        this.read += bits / 8L;
        if (this.read > this.max) {
            throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.read + "bytes where max allowed: " + this.max);
        }
    }
    
    static {
        INFINITE = new NBTSizeTracker(0L) {
            @Override
            public void read(final long bits) {
            }
        };
    }
}
