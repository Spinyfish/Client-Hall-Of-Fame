/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.scheduler.BukkitTask
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.bukkit.platform;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.platform.PlatformTask;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BukkitViaTask
implements PlatformTask<BukkitTask> {
    private final BukkitTask task;

    public BukkitViaTask(@Nullable BukkitTask task) {
        this.task = task;
    }

    @Override
    public @Nullable BukkitTask getObject() {
        return this.task;
    }

    @Override
    public void cancel() {
        Preconditions.checkArgument((this.task != null ? 1 : 0) != 0, (Object)"Task cannot be cancelled");
        this.task.cancel();
    }
}

