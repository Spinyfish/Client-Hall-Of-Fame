package de.jcm.discordgamesdk;

import java.util.concurrent.atomic.*;
import java.util.*;
import java.util.stream.*;

public class CreateParams implements AutoCloseable
{
    private final long pointer;
    private final AtomicBoolean open;
    
    public CreateParams() {
        this.open = new AtomicBoolean(true);
        this.pointer = this.allocate();
    }
    
    public void setClientID(final long id) {
        this.setClientID(this.pointer, id);
    }
    
    public long getClientID() {
        return this.getClientID(this.pointer);
    }
    
    public void setFlags(final Flags... flags) {
        this.setFlags(this.pointer, Flags.toLong(flags));
    }
    
    public void setFlags(final long flags) {
        this.setFlags(this.pointer, flags);
    }
    
    public long getFlags() {
        return this.getFlags(this.pointer);
    }
    
    public void registerEventHandler(final DiscordEventAdapter eventHandler) {
        this.registerEventHandler(this.pointer, Objects.requireNonNull(eventHandler));
    }
    
    private native long allocate();
    
    private native void free(final long p0);
    
    private native void setClientID(final long p0, final long p1);
    
    private native long getClientID(final long p0);
    
    private native void setFlags(final long p0, final long p1);
    
    private native long getFlags(final long p0);
    
    private native void registerEventHandler(final long p0, final DiscordEventAdapter p1);
    
    public static native long getDefaultFlags();
    
    @Override
    public void close() {
        if (this.open.compareAndSet(true, false)) {
            this.free(this.pointer);
        }
    }
    
    public long getPointer() {
        return this.pointer;
    }
    
    public enum Flags
    {
        DEFAULT(0L), 
        NO_REQUIRE_DISCORD(1L);
        
        private final long value;
        
        private Flags(final long value) {
            this.value = value;
        }
        
        public static long toLong(final Flags... flags) {
            long l = 0L;
            for (final Flags f : flags) {
                l |= f.value;
            }
            return l;
        }
        
        public static Flags[] fromLong(final long l) {
            return Stream.of(values()).filter(f -> (l & f.value) != 0x0L).toArray(Flags[]::new);
        }
    }
}
