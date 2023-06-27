package de.jcm.discordgamesdk.lobby;

import de.jcm.discordgamesdk.*;

public class LobbyMemberTransaction
{
    private final long pointer;
    
    LobbyMemberTransaction(final long pointer) {
        this.pointer = pointer;
    }
    
    public long getPointer() {
        return this.pointer;
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
    
    private native Result setMetadata(final long p0, final String p1, final String p2);
    
    private native Result deleteMetadata(final long p0, final String p1);
}
