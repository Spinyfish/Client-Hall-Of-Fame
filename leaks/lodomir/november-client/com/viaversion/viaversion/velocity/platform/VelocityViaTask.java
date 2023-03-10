/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.scheduler.ScheduledTask
 */
package com.viaversion.viaversion.velocity.platform;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.viaversion.viaversion.api.platform.PlatformTask;

public class VelocityViaTask
implements PlatformTask<ScheduledTask> {
    private final ScheduledTask task;

    public VelocityViaTask(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public ScheduledTask getObject() {
        return this.task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}

