/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.scheduler.Task
 */
package com.viaversion.viaversion.sponge.platform;

import com.viaversion.viaversion.api.platform.PlatformTask;
import org.spongepowered.api.scheduler.Task;

public class SpongeViaTask
implements PlatformTask<Task> {
    private final Task task;

    public SpongeViaTask(Task task) {
        this.task = task;
    }

    @Override
    public Task getObject() {
        return this.task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}

