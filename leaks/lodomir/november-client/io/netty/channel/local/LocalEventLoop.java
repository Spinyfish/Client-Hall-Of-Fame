/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.local;

import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import java.util.concurrent.ThreadFactory;

final class LocalEventLoop
extends SingleThreadEventLoop {
    LocalEventLoop(LocalEventLoopGroup parent, ThreadFactory threadFactory) {
        super(parent, threadFactory, true);
    }

    @Override
    protected void run() {
        do {
            Runnable task;
            if ((task = this.takeTask()) == null) continue;
            task.run();
            this.updateLastExecutionTime();
        } while (!this.confirmShutdown());
    }
}

