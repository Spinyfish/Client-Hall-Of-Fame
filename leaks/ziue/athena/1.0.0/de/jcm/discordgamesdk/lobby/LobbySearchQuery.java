package de.jcm.discordgamesdk.lobby;

import de.jcm.discordgamesdk.*;

public class LobbySearchQuery
{
    private final long pointer;
    
    LobbySearchQuery(final long pointer) {
        this.pointer = pointer;
    }
    
    public long getPointer() {
        return this.pointer;
    }
    
    public LobbySearchQuery filter(final String key, final Comparison comparison, final Cast cast, final String value) {
        if (key.getBytes().length >= 256) {
            throw new IllegalArgumentException("max key length is 255");
        }
        if (value.getBytes().length >= 4096) {
            throw new IllegalArgumentException("max value length is 4095");
        }
        final Result result = this.filter(this.pointer, key, comparison.nativeValue(), cast.nativeValue(), value);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
        return this;
    }
    
    public LobbySearchQuery sort(final String key, final Cast cast, final String baseValue) {
        if (key.getBytes().length >= 256) {
            throw new IllegalArgumentException("max key length is 255");
        }
        final Result result = this.sort(this.pointer, key, cast.nativeValue(), baseValue);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
        return this;
    }
    
    public LobbySearchQuery limit(final int limit) {
        final Result result = this.limit(this.pointer, limit);
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
        return this;
    }
    
    public LobbySearchQuery distance(final Distance distance) {
        final Result result = this.distance(this.pointer, distance.nativeValue());
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
        return this;
    }
    
    private native Result filter(final long p0, final String p1, final int p2, final int p3, final String p4);
    
    private native Result sort(final long p0, final String p1, final int p2, final String p3);
    
    private native Result limit(final long p0, final int p1);
    
    private native Result distance(final long p0, final int p1);
    
    public enum Comparison
    {
        LESS_THAN_OR_EQUAL, 
        LESS_THAN, 
        EQUAL, 
        GREATER_THAN, 
        GREATER_THAN_OR_EQUAL, 
        NOT_EQUAL;
        
        private static final int OFFSET = -2;
        
        private int nativeValue() {
            return this.ordinal() - 2;
        }
    }
    
    public enum Cast
    {
        STRING, 
        NUMBER;
        
        private static final int OFFSET = 1;
        
        private int nativeValue() {
            return this.ordinal() + 1;
        }
    }
    
    public enum Distance
    {
        LOCAL, 
        DEFAULT, 
        EXTENDED, 
        GLOBAL;
        
        private static final int OFFSET = 0;
        
        private int nativeValue() {
            return this.ordinal() + 0;
        }
    }
}
