package de.jcm.discordgamesdk;

import java.util.*;
import java.util.function.*;
import de.jcm.discordgamesdk.activity.*;

public class ActivityManager
{
    private final long pointer;
    private final Core core;
    
    ActivityManager(final long pointer, final Core core) {
        this.pointer = pointer;
        this.core = core;
    }
    
    public Result registerCommand(final String command) {
        return this.core.execute(() -> this.registerCommand(this.pointer, Objects.requireNonNull(command)));
    }
    
    public Result registerSteam(final int steamId) {
        return this.core.execute(() -> this.registerSteam(this.pointer, steamId));
    }
    
    public void updateActivity(final Activity activity) {
        this.updateActivity(activity, Core.DEFAULT_CALLBACK);
    }
    
    public void updateActivity(final Activity activity, final Consumer<Result> callback) {
        this.core.execute(() -> this.updateActivity(this.pointer, activity.getPointer(), Objects.requireNonNull(callback)));
    }
    
    public void clearActivity() {
        this.clearActivity(Core.DEFAULT_CALLBACK);
    }
    
    public void clearActivity(final Consumer<Result> callback) {
        this.core.execute(() -> this.clearActivity(this.pointer, Objects.requireNonNull(callback)));
    }
    
    public void sendRequestReply(final long userId, final ActivityJoinRequestReply reply) {
        this.sendRequestReply(userId, reply, Core.DEFAULT_CALLBACK);
    }
    
    public void sendRequestReply(final long userId, final ActivityJoinRequestReply reply, final Consumer<Result> callback) {
        this.core.execute(() -> this.sendRequestReply(this.pointer, userId, reply.ordinal(), Objects.requireNonNull(callback)));
    }
    
    public void sendInvite(final long userId, final ActivityActionType type, final String content) {
        this.sendInvite(userId, type, content, Core.DEFAULT_CALLBACK);
    }
    
    public void sendInvite(final long userId, final ActivityActionType type, final String content, final Consumer<Result> callback) {
        this.core.execute(() -> this.sendInvite(this.pointer, userId, type.nativeValue(), Objects.requireNonNull(content), Objects.requireNonNull(callback)));
    }
    
    public void acceptRequest(final long userId) {
        this.acceptRequest(userId, Core.DEFAULT_CALLBACK);
    }
    
    public void acceptRequest(final long userId, final Consumer<Result> callback) {
        this.core.execute(() -> this.acceptRequest(this.pointer, userId, Objects.requireNonNull(callback)));
    }
    
    private native Result registerCommand(final long p0, final String p1);
    
    private native Result registerSteam(final long p0, final int p1);
    
    private native void updateActivity(final long p0, final long p1, final Consumer<Result> p2);
    
    private native void clearActivity(final long p0, final Consumer<Result> p1);
    
    private native void sendRequestReply(final long p0, final long p1, final int p2, final Consumer<Result> p3);
    
    private native void sendInvite(final long p0, final long p1, final int p2, final String p3, final Consumer<Result> p4);
    
    private native void acceptRequest(final long p0, final long p1, final Consumer<Result> p2);
}
