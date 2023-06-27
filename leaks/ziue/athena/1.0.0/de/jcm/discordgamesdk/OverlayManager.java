package de.jcm.discordgamesdk;

import java.util.function.*;
import java.util.*;
import de.jcm.discordgamesdk.activity.*;

public class OverlayManager
{
    private final long pointer;
    private final Core core;
    
    OverlayManager(final long pointer, final Core core) {
        this.pointer = pointer;
        this.core = core;
    }
    
    public boolean isEnabled() {
        return this.core.execute(() -> this.isEnabled(this.pointer));
    }
    
    public boolean isLocked() {
        return this.core.execute(() -> this.isLocked(this.pointer));
    }
    
    public void setLocked(final boolean locked) {
        this.setLocked(locked, Core.DEFAULT_CALLBACK);
    }
    
    public void setLocked(final boolean locked, final Consumer<Result> callback) {
        this.core.execute(() -> this.setLocked(this.pointer, locked, Objects.requireNonNull(callback)));
    }
    
    public void openActivityInvite(final ActivityActionType type) {
        this.openActivityInvite(type, Core.DEFAULT_CALLBACK);
    }
    
    public void openActivityInvite(final ActivityActionType type, final Consumer<Result> callback) {
        this.core.execute(() -> this.openActivityInvite(this.pointer, type.ordinal(), Objects.requireNonNull(callback)));
    }
    
    public void openGuildInvite(final String code) {
        this.openGuildInvite(code, Core.DEFAULT_CALLBACK);
    }
    
    public void openGuildInvite(final String code, final Consumer<Result> callback) {
        this.core.execute(() -> this.openGuildInvite(this.pointer, code, Objects.requireNonNull(callback)));
    }
    
    public void openVoiceSettings() {
        this.openVoiceSettings(Core.DEFAULT_CALLBACK);
    }
    
    public void openVoiceSettings(final Consumer<Result> callback) {
        this.core.execute(() -> this.openVoiceSettings(this.pointer, Objects.requireNonNull(callback)));
    }
    
    private native boolean isEnabled(final long p0);
    
    private native boolean isLocked(final long p0);
    
    private native void setLocked(final long p0, final boolean p1, final Consumer<Result> p2);
    
    private native void openActivityInvite(final long p0, final int p1, final Consumer<Result> p2);
    
    private native void openGuildInvite(final long p0, final String p1, final Consumer<Result> p2);
    
    private native void openVoiceSettings(final long p0, final Consumer<Result> p1);
}
