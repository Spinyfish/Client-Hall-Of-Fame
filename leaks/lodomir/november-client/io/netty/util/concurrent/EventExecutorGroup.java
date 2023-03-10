/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface EventExecutorGroup
extends ScheduledExecutorService,
Iterable<EventExecutor> {
    public boolean isShuttingDown();

    public Future<?> shutdownGracefully();

    public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5);

    public Future<?> terminationFuture();

    @Override
    @Deprecated
    public void shutdown();

    @Override
    @Deprecated
    public List<Runnable> shutdownNow();

    public EventExecutor next();

    @Override
    public Iterator<EventExecutor> iterator();

    public Future<?> submit(Runnable var1);

    public <T> Future<T> submit(Runnable var1, T var2);

    public <T> Future<T> submit(Callable<T> var1);

    public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);

    public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4);

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6);
}

