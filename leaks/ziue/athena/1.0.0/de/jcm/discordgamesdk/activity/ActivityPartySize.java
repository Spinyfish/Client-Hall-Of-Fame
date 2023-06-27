package de.jcm.discordgamesdk.activity;

public class ActivityPartySize
{
    private final long pointer;
    
    ActivityPartySize(final long pointer) {
        this.pointer = pointer;
    }
    
    public void setCurrentSize(final int size) {
        this.setCurrentSize(this.pointer, size);
    }
    
    public int getCurrentSize() {
        return this.getCurrentSize(this.pointer);
    }
    
    public void setMaxSize(final int size) {
        this.setMaxSize(this.pointer, size);
    }
    
    public int getMaxSize() {
        return this.getMaxSize(this.pointer);
    }
    
    private native void setCurrentSize(final long p0, final int p1);
    
    private native int getCurrentSize(final long p0);
    
    private native void setMaxSize(final long p0, final int p1);
    
    private native int getMaxSize(final long p0);
}
