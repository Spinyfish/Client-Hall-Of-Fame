package de.jcm.discordgamesdk.lobby;

import de.jcm.discordgamesdk.*;

public class LobbyTransaction
{
    private final long pointer;
    
    LobbyTransaction(final long pointer) {
        this.pointer = pointer;
    }
    
    public long getPointer() {
        return this.pointer;
    }
    
    public void setType(final LobbyType type) {
        final Result result = this.setType(this.pointer, type.ordinal() + 1);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void setOwner(final long ownerId) {
        final Result result = this.setOwner(this.pointer, ownerId);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void setCapacity(final int capacity) {
        final Result result = this.setCapacity(this.pointer, capacity);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void setMetadata(final String key, final String value) {
        if (key.getBytes().length >= 256) {
            throw new IllegalArgumentException("max key length is 255");
        }
        if (value.getBytes().length >= 4096) {
            throw new IllegalArgumentException("max value length is 4095");
        }
        final Result result = this.setMetadata(this.pointer, key, value);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void deleteMetadata(final String key) {
        if (key.getBytes().length >= 256) {
            throw new IllegalArgumentException("max key length is 255");
        }
        final Result result = this.deleteMetadata(this.pointer, key);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void setLocked(final boolean locked) {
        final Result result = this.setLocked(this.pointer, locked);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    private native Result setType(final long p0, final int p1);
    
    private native Result setOwner(final long p0, final long p1);
    
    private native Result setCapacity(final long p0, final int p1);
    
    private native Result setMetadata(final long p0, final String p1, final String p2);
    
    private native Result deleteMetadata(final long p0, final String p1);
    
    private native Result setLocked(final long p0, final boolean p1);
}
