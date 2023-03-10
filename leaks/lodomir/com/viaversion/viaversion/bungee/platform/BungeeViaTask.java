/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.scheduler.ScheduledTask
 */
package com.viaversion.viaversion.bungee.platform;

import com.viaversion.viaversion.api.platform.PlatformTask;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeViaTask
implements PlatformTask<ScheduledTask> {
    private final ScheduledTask task;

    public BungeeViaTask(ScheduledTask task) {
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

