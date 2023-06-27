package de.jcm.discordgamesdk;

import de.jcm.discordgamesdk.voice.*;
import java.util.function.*;
import java.util.*;

public class VoiceManager
{
    private final long pointer;
    private final Core core;
    
    VoiceManager(final long pointer, final Core core) {
        this.pointer = pointer;
        this.core = core;
    }
    
    public VoiceInputMode getInputMode() {
        final Object ret = this.core.execute(() -> this.getInputMode(this.pointer));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (VoiceInputMode)ret;
    }
    
    public void setInputMode(final VoiceInputMode inputMode, final Consumer<Result> callback) {
        this.core.execute(() -> this.setInputMode(this.pointer, inputMode, Objects.requireNonNull(callback)));
    }
    
    public void setInputMode(final VoiceInputMode inputMode) {
        this.setInputMode(inputMode, Core.DEFAULT_CALLBACK);
    }
    
    public boolean isSelfMute() {
        final Object ret = this.core.execute(() -> this.isSelfMute(this.pointer));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (boolean)ret;
    }
    
    public void setSelfMute(final boolean selfMute) {
        final Result result = this.core.execute(() -> this.setSelfMute(this.pointer, selfMute));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public boolean isSelfDeaf() {
        final Object ret = this.core.execute(() -> this.isSelfDeaf(this.pointer));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (boolean)ret;
    }
    
    public void setSelfDeaf(final boolean selfDeaf) {
        final Result result = this.core.execute(() -> this.setSelfDeaf(this.pointer, selfDeaf));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public boolean isLocalMute(final long userId) {
        final Object ret = this.core.execute(() -> this.isLocalMute(this.pointer, userId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (boolean)ret;
    }
    
    public void setLocalMute(final long userId, final boolean mute) {
        final Result result = this.core.execute(() -> this.setLocalMute(this.pointer, userId, mute));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public int getLocalVolume(final long userId) {
        final Object ret = this.core.execute(() -> this.getLocalVolume(this.pointer, userId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (int)ret;
    }
    
    public void setLocalVolume(final long userId, final int volume) {
        if (volume < 0 || volume > 200) {
            throw new IllegalArgumentException("volume out of range: " + volume);
        }
        final Result result = this.core.execute(() -> this.setLocalVolume(this.pointer, userId, (byte)volume));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    private native Object getInputMode(final long p0);
    
    private native void setInputMode(final long p0, final VoiceInputMode p1, final Consumer<Result> p2);
    
    private native Object isSelfMute(final long p0);
    
    private native Result setSelfMute(final long p0, final boolean p1);
    
    private native Object isSelfDeaf(final long p0);
    
    private native Result setSelfDeaf(final long p0, final boolean p1);
    
    private native Object isLocalMute(final long p0, final long p1);
    
    private native Result setLocalMute(final long p0, final long p1, final boolean p2);
    
    private native Object getLocalVolume(final long p0, final long p1);
    
    private native Result setLocalVolume(final long p0, final long p1, final byte p2);
}
