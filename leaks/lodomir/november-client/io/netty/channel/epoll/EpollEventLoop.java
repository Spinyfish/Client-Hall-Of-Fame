/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Native;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class EpollEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
    private static final AtomicIntegerFieldUpdater<EpollEventLoop> WAKEN_UP_UPDATER;
    private final int epollFd;
    private final int eventFd;
    private final IntObjectMap<AbstractEpollChannel> ids = new IntObjectHashMap<AbstractEpollChannel>();
    private final long[] events;
    private int id;
    private boolean overflown;
    private volatile int wakenUp;
    private volatile int ioRatio = 50;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    EpollEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, int maxEvents) {
        super(parent, threadFactory, false);
        this.events = new long[maxEvents];
        boolean success = false;
        int epollFd = -1;
        int eventFd = -1;
        try {
            this.epollFd = epollFd = Native.epollCreate();
            this.eventFd = eventFd = Native.eventFd();
            Native.epollCtlAdd(epollFd, eventFd, 1, 0);
            success = true;
        }
        finally {
            if (!success) {
                if (epollFd != -1) {
                    try {
                        Native.close(epollFd);
                    }
                    catch (Exception e) {}
                }
                if (eventFd != -1) {
                    try {
                        Native.close(eventFd);
                    }
                    catch (Exception e) {}
                }
            }
        }
    }

    private int nextId() {
        int id = this.id;
        if (id == Integer.MAX_VALUE) {
            this.overflown = true;
            id = 0;
        }
        if (this.overflown) {
            while (this.ids.containsKey(++id)) {
            }
            this.id = id;
        } else {
            this.id = ++id;
        }
        return id;
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
            Native.eventFdWrite(this.eventFd, 1L);
        }
    }

    void add(AbstractEpollChannel ch) {
        assert (this.inEventLoop());
        int id = this.nextId();
        Native.epollCtlAdd(this.epollFd, ch.fd, ch.flags, id);
        ch.id = id;
        this.ids.put(id, ch);
    }

    void modify(AbstractEpollChannel ch) {
        assert (this.inEventLoop());
        Native.epollCtlMod(this.epollFd, ch.fd, ch.flags, ch.id);
    }

    void remove(AbstractEpollChannel ch) {
        assert (this.inEventLoop());
        if (this.ids.remove(ch.id) != null && ch.isOpen()) {
            Native.epollCtlDel(this.epollFd, ch.fd);
        }
    }

    @Override
    protected Queue<Runnable> newTaskQueue() {
        return PlatformDependent.newMpscQueue();
    }

    public int getIoRatio() {
        return this.ioRatio;
    }

    public void setIoRatio(int ioRatio) {
        if (ioRatio <= 0 || ioRatio > 100) {
            throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
        }
        this.ioRatio = ioRatio;
    }

    private int epollWait(boolean oldWakenUp) {
        int selectCnt = 0;
        long currentTimeNanos = System.nanoTime();
        long selectDeadLineNanos = currentTimeNanos + this.delayNanos(currentTimeNanos);
        while (true) {
            long timeoutMillis;
            if ((timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L) <= 0L) {
                int ready;
                if (selectCnt != 0 || (ready = Native.epollWait(this.epollFd, this.events, 0)) <= 0) break;
                return ready;
            }
            int selectedKeys = Native.epollWait(this.epollFd, this.events, (int)timeoutMillis);
            ++selectCnt;
            if (selectedKeys != 0 || oldWakenUp || this.wakenUp == 1 || this.hasTasks() || this.hasScheduledTasks()) {
                return selectedKeys;
            }
            currentTimeNanos = System.nanoTime();
        }
        return 0;
    }

    @Override
    protected void run() {
        while (true) {
            boolean oldWakenUp = WAKEN_UP_UPDATER.getAndSet(this, 0) == 1;
            try {
                int ready;
                if (this.hasTasks()) {
                    ready = Native.epollWait(this.epollFd, this.events, 0);
                } else {
                    ready = this.epollWait(oldWakenUp);
                    if (this.wakenUp == 1) {
                        Native.eventFdWrite(this.eventFd, 1L);
                    }
                }
                int ioRatio = this.ioRatio;
                if (ioRatio == 100) {
                    if (ready > 0) {
                        this.processReady(this.events, ready);
                    }
                    this.runAllTasks();
                } else {
                    long ioStartTime = System.nanoTime();
                    if (ready > 0) {
                        this.processReady(this.events, ready);
                    }
                    long ioTime = System.nanoTime() - ioStartTime;
                    this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                }
                if (!this.isShuttingDown()) continue;
                this.closeAll();
                if (!this.confirmShutdown()) continue;
            }
            catch (Throwable t) {
                logger.warn("Unexpected exception in the selector loop.", t);
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {}
                continue;
            }
            break;
        }
    }

    private void closeAll() {
        Native.epollWait(this.epollFd, this.events, 0);
        ArrayList<AbstractEpollChannel> channels = new ArrayList<AbstractEpollChannel>(this.ids.size());
        for (IntObjectMap.Entry<AbstractEpollChannel> entry : this.ids.entries()) {
            channels.add(entry.value());
        }
        for (AbstractEpollChannel ch : channels) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }

    private void processReady(long[] events, int ready) {
        for (int i = 0; i < ready; ++i) {
            long ev = events[i];
            int id = (int)(ev >> 32);
            if (id == 0) {
                Native.eventFdRead(this.eventFd);
                continue;
            }
            boolean read = (ev & 1L) != 0L;
            boolean write = (ev & 2L) != 0L;
            boolean close = (ev & 8L) != 0L;
            AbstractEpollChannel ch = this.ids.get(id);
            if (ch == null) continue;
            AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch.unsafe();
            if (write && ch.isOpen()) {
                unsafe.epollOutReady();
            }
            if (read && ch.isOpen()) {
                unsafe.epollInReady();
            }
            if (!close || !ch.isOpen()) continue;
            unsafe.epollRdHupReady();
        }
    }

    @Override
    protected void cleanup() {
        try {
            Native.close(this.epollFd);
        }
        catch (IOException e) {
            logger.warn("Failed to close the epoll fd.", e);
        }
        try {
            Native.close(this.eventFd);
        }
        catch (IOException e) {
            logger.warn("Failed to close the event fd.", e);
        }
    }

    static {
        AtomicIntegerFieldUpdater<Object> updater = PlatformDependent.newAtomicIntegerFieldUpdater(EpollEventLoop.class, "wakenUp");
        if (updater == null) {
            updater = AtomicIntegerFieldUpdater.newUpdater(EpollEventLoop.class, "wakenUp");
        }
        WAKEN_UP_UPDATER = updater;
    }
}

