/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class SingleThreadEventExecutor
extends AbstractEventExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final int ST_TERMINATED = 5;
    private static final Runnable WAKEUP_TASK = new Runnable(){

        @Override
        public void run() {
        }
    };
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER;
    private final EventExecutorGroup parent;
    private final Queue<Runnable> taskQueue;
    final Queue<ScheduledFutureTask<?>> delayedTaskQueue = new PriorityQueue();
    private final Thread thread;
    private final Semaphore threadLock = new Semaphore(0);
    private final Set<Runnable> shutdownHooks = new LinkedHashSet<Runnable>();
    private final boolean addTaskWakesUp;
    private long lastExecutionTime;
    private volatile int state = 1;
    private volatile long gracefulShutdownQuietPeriod;
    private volatile long gracefulShutdownTimeout;
    private long gracefulShutdownStartTime;
    private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
    private static final long SCHEDULE_PURGE_INTERVAL;

    protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        this.parent = parent;
        this.addTaskWakesUp = addTaskWakesUp;
        this.thread = threadFactory.newThread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void run() {
                boolean success = false;
                SingleThreadEventExecutor.this.updateLastExecutionTime();
                try {
                    SingleThreadEventExecutor.this.run();
                    success = true;
                }
                catch (Throwable t) {
                    logger.warn("Unexpected exception from an event executor: ", t);
                }
                finally {
                    int oldState;
                    while ((oldState = STATE_UPDATER.get(SingleThreadEventExecutor.this)) < 3 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 3)) {
                    }
                    if (success && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L) {
                        logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called " + "before run() implementation terminates.");
                    }
                    while (!SingleThreadEventExecutor.this.confirmShutdown()) {
                    }
                }
                try {
                    SingleThreadEventExecutor.this.cleanup();
                    return;
                }
                finally {
                    STATE_UPDATER.set(SingleThreadEventExecutor.this, 5);
                    SingleThreadEventExecutor.this.threadLock.release();
                    if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                        logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                    }
                    SingleThreadEventExecutor.this.terminationFuture.setSuccess(null);
                }
            }
        });
        this.taskQueue = this.newTaskQueue();
    }

    protected Queue<Runnable> newTaskQueue() {
        return new LinkedBlockingQueue<Runnable>();
    }

    @Override
    public EventExecutorGroup parent() {
        return this.parent;
    }

    protected void interruptThread() {
        this.thread.interrupt();
    }

    protected Runnable pollTask() {
        Runnable task;
        assert (this.inEventLoop());
        while ((task = this.taskQueue.poll()) == WAKEUP_TASK) {
        }
        return task;
    }

    protected Runnable takeTask() {
        Runnable task;
        assert (this.inEventLoop());
        if (!(this.taskQueue instanceof BlockingQueue)) {
            throw new UnsupportedOperationException();
        }
        BlockingQueue taskQueue = (BlockingQueue)this.taskQueue;
        do {
            ScheduledFutureTask<?> delayedTask;
            if ((delayedTask = this.delayedTaskQueue.peek()) == null) {
                Runnable task2 = null;
                try {
                    task2 = (Runnable)taskQueue.take();
                    if (task2 == WAKEUP_TASK) {
                        task2 = null;
                    }
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                return task2;
            }
            long delayNanos = delayedTask.delayNanos();
            task = null;
            if (delayNanos > 0L) {
                try {
                    task = (Runnable)taskQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e) {
                    return null;
                }
            }
            if (task != null) continue;
            this.fetchFromDelayedQueue();
            task = (Runnable)taskQueue.poll();
        } while (task == null);
        return task;
    }

    private void fetchFromDelayedQueue() {
        ScheduledFutureTask<?> delayedTask;
        long nanoTime = 0L;
        while ((delayedTask = this.delayedTaskQueue.peek()) != null) {
            if (nanoTime == 0L) {
                nanoTime = ScheduledFutureTask.nanoTime();
            }
            if (delayedTask.deadlineNanos() > nanoTime) break;
            this.delayedTaskQueue.remove();
            this.taskQueue.add(delayedTask);
        }
    }

    protected Runnable peekTask() {
        assert (this.inEventLoop());
        return this.taskQueue.peek();
    }

    protected boolean hasTasks() {
        assert (this.inEventLoop());
        return !this.taskQueue.isEmpty();
    }

    protected boolean hasScheduledTasks() {
        assert (this.inEventLoop());
        ScheduledFutureTask<?> delayedTask = this.delayedTaskQueue.peek();
        return delayedTask != null && delayedTask.deadlineNanos() <= ScheduledFutureTask.nanoTime();
    }

    public final int pendingTasks() {
        return this.taskQueue.size();
    }

    protected void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (this.isShutdown()) {
            SingleThreadEventExecutor.reject();
        }
        this.taskQueue.add(task);
    }

    protected boolean removeTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        return this.taskQueue.remove(task);
    }

    protected boolean runAllTasks() {
        this.fetchFromDelayedQueue();
        Runnable task = this.pollTask();
        if (task == null) {
            return false;
        }
        do {
            try {
                task.run();
            }
            catch (Throwable t) {
                logger.warn("A task raised an exception.", t);
            }
        } while ((task = this.pollTask()) != null);
        this.lastExecutionTime = ScheduledFutureTask.nanoTime();
        return true;
    }

    protected boolean runAllTasks(long timeoutNanos) {
        long lastExecutionTime;
        block4: {
            this.fetchFromDelayedQueue();
            Runnable task = this.pollTask();
            if (task == null) {
                return false;
            }
            long deadline = ScheduledFutureTask.nanoTime() + timeoutNanos;
            long runTasks = 0L;
            do {
                try {
                    task.run();
                }
                catch (Throwable t) {
                    logger.warn("A task raised an exception.", t);
                }
                if ((++runTasks & 0x3FL) == 0L && (lastExecutionTime = ScheduledFutureTask.nanoTime()) >= deadline) break block4;
            } while ((task = this.pollTask()) != null);
            lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        this.lastExecutionTime = lastExecutionTime;
        return true;
    }

    protected long delayNanos(long currentTimeNanos) {
        ScheduledFutureTask<?> delayedTask = this.delayedTaskQueue.peek();
        if (delayedTask == null) {
            return SCHEDULE_PURGE_INTERVAL;
        }
        return delayedTask.delayNanos(currentTimeNanos);
    }

    protected void updateLastExecutionTime() {
        this.lastExecutionTime = ScheduledFutureTask.nanoTime();
    }

    protected abstract void run();

    protected void cleanup() {
    }

    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop || STATE_UPDATER.get(this) == 3) {
            this.taskQueue.add(WAKEUP_TASK);
        }
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    public void addShutdownHook(final Runnable task) {
        if (this.inEventLoop()) {
            this.shutdownHooks.add(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    SingleThreadEventExecutor.this.shutdownHooks.add(task);
                }
            });
        }
    }

    public void removeShutdownHook(final Runnable task) {
        if (this.inEventLoop()) {
            this.shutdownHooks.remove(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    SingleThreadEventExecutor.this.shutdownHooks.remove(task);
                }
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean runShutdownHooks() {
        boolean ran = false;
        while (!this.shutdownHooks.isEmpty()) {
            ArrayList<Runnable> copy = new ArrayList<Runnable>(this.shutdownHooks);
            this.shutdownHooks.clear();
            for (Runnable task : copy) {
                try {
                    task.run();
                }
                catch (Throwable t) {
                    logger.warn("Shutdown hook raised an exception.", t);
                }
                finally {
                    ran = true;
                }
            }
        }
        if (ran) {
            this.lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        return ran;
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        boolean wakeup;
        int newState;
        int oldState;
        if (quietPeriod < 0L) {
            throw new IllegalArgumentException("quietPeriod: " + quietPeriod + " (expected >= 0)");
        }
        if (timeout < quietPeriod) {
            throw new IllegalArgumentException("timeout: " + timeout + " (expected >= quietPeriod (" + quietPeriod + "))");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (this.isShuttingDown()) {
            return this.terminationFuture();
        }
        boolean inEventLoop = this.inEventLoop();
        do {
            if (this.isShuttingDown()) {
                return this.terminationFuture();
            }
            wakeup = true;
            oldState = STATE_UPDATER.get(this);
            if (inEventLoop) {
                newState = 3;
                continue;
            }
            switch (oldState) {
                case 1: 
                case 2: {
                    newState = 3;
                    break;
                }
                default: {
                    newState = oldState;
                    wakeup = false;
                }
            }
        } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
        this.gracefulShutdownQuietPeriod = unit.toNanos(quietPeriod);
        this.gracefulShutdownTimeout = unit.toNanos(timeout);
        if (oldState == 1) {
            this.thread.start();
        }
        if (wakeup) {
            this.wakeup(inEventLoop);
        }
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Override
    @Deprecated
    public void shutdown() {
        boolean wakeup;
        int newState;
        int oldState;
        if (this.isShutdown()) {
            return;
        }
        boolean inEventLoop = this.inEventLoop();
        do {
            if (this.isShuttingDown()) {
                return;
            }
            wakeup = true;
            oldState = STATE_UPDATER.get(this);
            if (inEventLoop) {
                newState = 4;
                continue;
            }
            switch (oldState) {
                case 1: 
                case 2: 
                case 3: {
                    newState = 4;
                    break;
                }
                default: {
                    newState = oldState;
                    wakeup = false;
                }
            }
        } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
        if (oldState == 1) {
            this.thread.start();
        }
        if (wakeup) {
            this.wakeup(inEventLoop);
        }
    }

    @Override
    public boolean isShuttingDown() {
        return STATE_UPDATER.get(this) >= 3;
    }

    @Override
    public boolean isShutdown() {
        return STATE_UPDATER.get(this) >= 4;
    }

    @Override
    public boolean isTerminated() {
        return STATE_UPDATER.get(this) == 5;
    }

    protected boolean confirmShutdown() {
        if (!this.isShuttingDown()) {
            return false;
        }
        if (!this.inEventLoop()) {
            throw new IllegalStateException("must be invoked from an event loop");
        }
        this.cancelDelayedTasks();
        if (this.gracefulShutdownStartTime == 0L) {
            this.gracefulShutdownStartTime = ScheduledFutureTask.nanoTime();
        }
        if (this.runAllTasks() || this.runShutdownHooks()) {
            if (this.isShutdown()) {
                return true;
            }
            this.wakeup(true);
            return false;
        }
        long nanoTime = ScheduledFutureTask.nanoTime();
        if (this.isShutdown() || nanoTime - this.gracefulShutdownStartTime > this.gracefulShutdownTimeout) {
            return true;
        }
        if (nanoTime - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
            this.wakeup(true);
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            return false;
        }
        return true;
    }

    private void cancelDelayedTasks() {
        ScheduledFutureTask[] delayedTasks;
        if (this.delayedTaskQueue.isEmpty()) {
            return;
        }
        for (ScheduledFutureTask task : delayedTasks = this.delayedTaskQueue.toArray(new ScheduledFutureTask[this.delayedTaskQueue.size()])) {
            task.cancel(false);
        }
        this.delayedTaskQueue.clear();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (this.inEventLoop()) {
            throw new IllegalStateException("cannot await termination of the current thread");
        }
        if (this.threadLock.tryAcquire(timeout, unit)) {
            this.threadLock.release();
        }
        return this.isTerminated();
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        boolean inEventLoop = this.inEventLoop();
        if (inEventLoop) {
            this.addTask(task);
        } else {
            this.startThread();
            this.addTask(task);
            if (this.isShutdown() && this.removeTask(task)) {
                SingleThreadEventExecutor.reject();
            }
        }
        if (!this.addTaskWakesUp && this.wakesUpForTask(task)) {
            this.wakeup(inEventLoop);
        }
    }

    protected boolean wakesUpForTask(Runnable task) {
        return true;
    }

    protected static void reject() {
        throw new RejectedExecutionException("event executor terminated");
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        if (command == null) {
            throw new NullPointerException("command");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (delay < 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: >= 0)", delay));
        }
        return this.schedule(new ScheduledFutureTask<Object>((EventExecutor)this, this.delayedTaskQueue, command, null, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        if (callable == null) {
            throw new NullPointerException("callable");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (delay < 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: >= 0)", delay));
        }
        return this.schedule(new ScheduledFutureTask<V>(this, this.delayedTaskQueue, callable, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        if (command == null) {
            throw new NullPointerException("command");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (initialDelay < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (period <= 0L) {
            throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", period));
        }
        return this.schedule(new ScheduledFutureTask<Object>((EventExecutor)this, this.delayedTaskQueue, Executors.callable(command, null), ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), unit.toNanos(period)));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        if (command == null) {
            throw new NullPointerException("command");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (initialDelay < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (delay <= 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", delay));
        }
        return this.schedule(new ScheduledFutureTask<Object>((EventExecutor)this, this.delayedTaskQueue, Executors.callable(command, null), ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), -unit.toNanos(delay)));
    }

    private <V> ScheduledFuture<V> schedule(final ScheduledFutureTask<V> task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (this.inEventLoop()) {
            this.delayedTaskQueue.add(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    SingleThreadEventExecutor.this.delayedTaskQueue.add(task);
                }
            });
        }
        return task;
    }

    private void startThread() {
        if (STATE_UPDATER.get(this) == 1 && STATE_UPDATER.compareAndSet(this, 1, 2)) {
            this.delayedTaskQueue.add(new ScheduledFutureTask<Object>((EventExecutor)this, this.delayedTaskQueue, Executors.callable(new PurgeTask(), null), ScheduledFutureTask.deadlineNanos(SCHEDULE_PURGE_INTERVAL), -SCHEDULE_PURGE_INTERVAL));
            this.thread.start();
        }
    }

    static {
        AtomicIntegerFieldUpdater<Object> updater = PlatformDependent.newAtomicIntegerFieldUpdater(SingleThreadEventExecutor.class, "state");
        if (updater == null) {
            updater = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");
        }
        STATE_UPDATER = updater;
        SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
    }

    private final class PurgeTask
    implements Runnable {
        private PurgeTask() {
        }

        @Override
        public void run() {
            Iterator i = SingleThreadEventExecutor.this.delayedTaskQueue.iterator();
            while (i.hasNext()) {
                ScheduledFutureTask task = (ScheduledFutureTask)i.next();
                if (!task.isCancelled()) continue;
                i.remove();
            }
        }
    }
}

