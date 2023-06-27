package de.jcm.discordgamesdk.activity;

import java.time.*;

public class ActivityTimestamps
{
    private final long pointer;
    
    ActivityTimestamps(final long pointer) {
        this.pointer = pointer;
    }
    
    public void setStart(final Instant start) {
        this.setStart(this.pointer, start.getEpochSecond());
    }
    
    public Instant getStart() {
        return Instant.ofEpochSecond(this.getStart(this.pointer));
    }
    
    public void setEnd(final Instant end) {
        this.setEnd(this.pointer, end.getEpochSecond());
    }
    
    public Instant getEnd() {
        return Instant.ofEpochSecond(this.getEnd(this.pointer));
    }
    
    private native void setStart(final long p0, final long p1);
    
    private native long getStart(final long p0);
    
    private native void setEnd(final long p0, final long p1);
    
    private native long getEnd(final long p0);
}
