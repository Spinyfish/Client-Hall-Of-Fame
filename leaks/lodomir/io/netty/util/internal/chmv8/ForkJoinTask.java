/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.chmv8;

import io.netty.util.internal.chmv8.CountedCompleter;
import io.netty.util.internal.chmv8.ForkJoinPool;
import io.netty.util.internal.chmv8.ForkJoinWorkerThread;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public abstract class ForkJoinTask<V>
implements Future<V>,
Serializable {
    volatile int status;
    static final int DONE_MASK = -268435456;
    static final int NORMAL = -268435456;
    static final int CANCELLED = -1073741824;
    static final int EXCEPTIONAL = Integer.MIN_VALUE;
    static final int SIGNAL = 65536;
    static final int SMASK = 65535;
    private static final ExceptionNode[] exceptionTable;
    private static final ReentrantLock exceptionTableLock;
    private static final ReferenceQueue<Object> exceptionTableRefQueue;
    private static final int EXCEPTION_MAP_CAPACITY = 32;
    private static final long serialVersionUID = -7721805057305804111L;
    private static final Unsafe U;
    private static final long STATUS;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int setCompletion(int completion) {
        int s;
        do {
            if ((s = this.status) >= 0) continue;
            return s;
        } while (!U.compareAndSwapInt(this, STATUS, s, s | completion));
        if (s >>> 16 != 0) {
            ForkJoinTask forkJoinTask = this;
            synchronized (forkJoinTask) {
                this.notifyAll();
            }
        }
        return completion;
    }

    final int doExec() {
        int s = this.status;
        if (s >= 0) {
            boolean completed;
            try {
                completed = this.exec();
            }
            catch (Throwable rex) {
                return this.setExceptionalCompletion(rex);
            }
            if (completed) {
                s = this.setCompletion(-268435456);
            }
        }
        return s;
    }

    final boolean trySetSignal() {
        int s = this.status;
        return s >= 0 && U.compareAndSwapInt(this, STATUS, s, s | 0x10000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int externalAwaitDone() {
        ForkJoinPool cp = ForkJoinPool.common;
        int s = this.status;
        if (s >= 0) {
            if (cp != null) {
                if (this instanceof CountedCompleter) {
                    s = cp.externalHelpComplete((CountedCompleter)this);
                } else if (cp.tryExternalUnpush(this)) {
                    s = this.doExec();
                }
            }
            if (s >= 0 && (s = this.status) >= 0) {
                boolean interrupted = false;
                do {
                    if (!U.compareAndSwapInt(this, STATUS, s, s | 0x10000)) continue;
                    ForkJoinTask forkJoinTask = this;
                    synchronized (forkJoinTask) {
                        if (this.status >= 0) {
                            try {
                                this.wait();
                            }
                            catch (InterruptedException ie) {
                                interrupted = true;
                            }
                        } else {
                            this.notifyAll();
                        }
                    }
                } while ((s = this.status) >= 0);
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return s;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int externalInterruptibleAwaitDone() throws InterruptedException {
        ForkJoinPool cp = ForkJoinPool.common;
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        int s = this.status;
        if (s >= 0 && cp != null) {
            if (this instanceof CountedCompleter) {
                cp.externalHelpComplete((CountedCompleter)this);
            } else if (cp.tryExternalUnpush(this)) {
                this.doExec();
            }
        }
        while ((s = this.status) >= 0) {
            if (!U.compareAndSwapInt(this, STATUS, s, s | 0x10000)) continue;
            ForkJoinTask forkJoinTask = this;
            synchronized (forkJoinTask) {
                if (this.status >= 0) {
                    this.wait();
                } else {
                    this.notifyAll();
                }
            }
        }
        return s;
    }

    private int doJoin() {
        int n;
        int s = this.status;
        if (s < 0) {
            n = s;
        } else {
            Thread t = Thread.currentThread();
            if (t instanceof ForkJoinWorkerThread) {
                ForkJoinWorkerThread wt = (ForkJoinWorkerThread)t;
                ForkJoinPool.WorkQueue w = wt.workQueue;
                n = w.tryUnpush(this) && (s = this.doExec()) < 0 ? s : wt.pool.awaitJoin(w, this);
            } else {
                n = this.externalAwaitDone();
            }
        }
        return n;
    }

    private int doInvoke() {
        int n;
        int s = this.doExec();
        if (s < 0) {
            n = s;
        } else {
            Thread t = Thread.currentThread();
            if (t instanceof ForkJoinWorkerThread) {
                ForkJoinWorkerThread wt = (ForkJoinWorkerThread)t;
                n = wt.pool.awaitJoin(wt.workQueue, this);
            } else {
                n = this.externalAwaitDone();
            }
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final int recordExceptionalCompletion(Throwable ex) {
        int s = this.status;
        if (s >= 0) {
            int h = System.identityHashCode(this);
            ReentrantLock lock = exceptionTableLock;
            lock.lock();
            try {
                ForkJoinTask.expungeStaleExceptions();
                ExceptionNode[] t = exceptionTable;
                int i = h & t.length - 1;
                ExceptionNode e = t[i];
                while (true) {
                    if (e == null) {
                        t[i] = new ExceptionNode(this, ex, t[i]);
                        break;
                    }
                    if (e.get() == this) {
                        break;
                    }
                    e = e.next;
                }
            }
            finally {
                lock.unlock();
            }
            s = this.setCompletion(Integer.MIN_VALUE);
        }
        return s;
    }

    private int setExceptionalCompletion(Throwable ex) {
        int s = this.recordExceptionalCompletion(ex);
        if ((s & 0xF0000000) == Integer.MIN_VALUE) {
            this.internalPropagateException(ex);
        }
        return s;
    }

    void internalPropagateException(Throwable ex) {
    }

    static final void cancelIgnoringExceptions(ForkJoinTask<?> t) {
        if (t != null && t.status >= 0) {
            try {
                t.cancel(false);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clearExceptionalCompletion() {
        int h = System.identityHashCode(this);
        ReentrantLock lock = exceptionTableLock;
        lock.lock();
        try {
            ExceptionNode[] t = exceptionTable;
            int i = h & t.length - 1;
            ExceptionNode e = t[i];
            ExceptionNode pred = null;
            while (e != null) {
                ExceptionNode next = e.next;
                if (e.get() == this) {
                    if (pred == null) {
                        t[i] = next;
                        break;
                    }
                    pred.next = next;
                    break;
                }
                pred = e;
                e = next;
            }
            ForkJoinTask.expungeStaleExceptions();
            this.status = 0;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Throwable getThrowableException() {
        Throwable ex;
        ExceptionNode e;
        if ((this.status & 0xF0000000) != Integer.MIN_VALUE) {
            return null;
        }
        int h = System.identityHashCode(this);
        ReentrantLock lock = exceptionTableLock;
        lock.lock();
        try {
            ForkJoinTask.expungeStaleExceptions();
            ExceptionNode[] t = exceptionTable;
            e = t[h & t.length - 1];
            while (e != null && e.get() != this) {
                e = e.next;
            }
        }
        finally {
            lock.unlock();
        }
        if (e == null || (ex = e.ex) == null) {
            return null;
        }
        return ex;
    }

    private static void expungeStaleExceptions() {
        Reference<Object> x;
        block0: while ((x = exceptionTableRefQueue.poll()) != null) {
            if (!(x instanceof ExceptionNode)) continue;
            ForkJoinTask key = (ForkJoinTask)((ExceptionNode)x).get();
            ExceptionNode[] t = exceptionTable;
            int i = System.identityHashCode(key) & t.length - 1;
            ExceptionNode e = t[i];
            ExceptionNode pred = null;
            while (e != null) {
                ExceptionNode next = e.next;
                if (e == x) {
                    if (pred == null) {
                        t[i] = next;
                        continue block0;
                    }
                    pred.next = next;
                    continue block0;
                }
                pred = e;
                e = next;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static final void helpExpungeStaleExceptions() {
        ReentrantLock lock = exceptionTableLock;
        if (lock.tryLock()) {
            try {
                ForkJoinTask.expungeStaleExceptions();
            }
            finally {
                lock.unlock();
            }
        }
    }

    static void rethrow(Throwable ex) {
        if (ex != null) {
            ForkJoinTask.uncheckedThrow(ex);
        }
    }

    static <T extends Throwable> void uncheckedThrow(Throwable t) throws T {
        throw t;
    }

    private void reportException(int s) {
        if (s == -1073741824) {
            throw new CancellationException();
        }
        if (s == Integer.MIN_VALUE) {
            ForkJoinTask.rethrow(this.getThrowableException());
        }
    }

    public final ForkJoinTask<V> fork() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ((ForkJoinWorkerThread)t).workQueue.push(this);
        } else {
            ForkJoinPool.common.externalPush(this);
        }
        return this;
    }

    public final V join() {
        int s = this.doJoin() & 0xF0000000;
        if (s != -268435456) {
            this.reportException(s);
        }
        return this.getRawResult();
    }

    public final V invoke() {
        int s = this.doInvoke() & 0xF0000000;
        if (s != -268435456) {
            this.reportException(s);
        }
        return this.getRawResult();
    }

    public static void invokeAll(ForkJoinTask<?> t1, ForkJoinTask<?> t2) {
        int s2;
        t2.fork();
        int s1 = super.doInvoke() & 0xF0000000;
        if (s1 != -268435456) {
            super.reportException(s1);
        }
        if ((s2 = super.doJoin() & 0xF0000000) != -268435456) {
            super.reportException(s2);
        }
    }

    public static void invokeAll(ForkJoinTask<?> ... tasks) {
        ForkJoinTask<?> t;
        int last;
        int i;
        Throwable ex = null;
        for (i = last = tasks.length - 1; i >= 0; --i) {
            t = tasks[i];
            if (t == null) {
                if (ex != null) continue;
                ex = new NullPointerException();
                continue;
            }
            if (i != 0) {
                t.fork();
                continue;
            }
            if (super.doInvoke() >= -268435456 || ex != null) continue;
            ex = t.getException();
        }
        for (i = 1; i <= last; ++i) {
            t = tasks[i];
            if (t == null) continue;
            if (ex != null) {
                t.cancel(false);
                continue;
            }
            if (super.doJoin() >= -268435456) continue;
            ex = t.getException();
        }
        if (ex != null) {
            ForkJoinTask.rethrow(ex);
        }
    }

    public static <T extends ForkJoinTask<?>> Collection<T> invokeAll(Collection<T> tasks) {
        ForkJoinTask t;
        int last;
        int i;
        if (!(tasks instanceof RandomAccess) || !(tasks instanceof List)) {
            ForkJoinTask.invokeAll(tasks.toArray(new ForkJoinTask[tasks.size()]));
            return tasks;
        }
        List ts = (List)tasks;
        Throwable ex = null;
        for (i = last = ts.size() - 1; i >= 0; --i) {
            t = (ForkJoinTask)ts.get(i);
            if (t == null) {
                if (ex != null) continue;
                ex = new NullPointerException();
                continue;
            }
            if (i != 0) {
                t.fork();
                continue;
            }
            if (t.doInvoke() >= -268435456 || ex != null) continue;
            ex = t.getException();
        }
        for (i = 1; i <= last; ++i) {
            t = (ForkJoinTask)ts.get(i);
            if (t == null) continue;
            if (ex != null) {
                t.cancel(false);
                continue;
            }
            if (t.doJoin() >= -268435456) continue;
            ex = t.getException();
        }
        if (ex != null) {
            ForkJoinTask.rethrow(ex);
        }
        return tasks;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return (this.setCompletion(-1073741824) & 0xF0000000) == -1073741824;
    }

    @Override
    public final boolean isDone() {
        return this.status < 0;
    }

    @Override
    public final boolean isCancelled() {
        return (this.status & 0xF0000000) == -1073741824;
    }

    public final boolean isCompletedAbnormally() {
        return this.status < -268435456;
    }

    public final boolean isCompletedNormally() {
        return (this.status & 0xF0000000) == -268435456;
    }

    public final Throwable getException() {
        int s = this.status & 0xF0000000;
        return s >= -268435456 ? null : (s == -1073741824 ? new CancellationException() : this.getThrowableException());
    }

    public void completeExceptionally(Throwable ex) {
        this.setExceptionalCompletion(ex instanceof RuntimeException || ex instanceof Error ? ex : new RuntimeException(ex));
    }

    public void complete(V value) {
        try {
            this.setRawResult(value);
        }
        catch (Throwable rex) {
            this.setExceptionalCompletion(rex);
            return;
        }
        this.setCompletion(-268435456);
    }

    public final void quietlyComplete() {
        this.setCompletion(-268435456);
    }

    @Override
    public final V get() throws InterruptedException, ExecutionException {
        Throwable ex;
        int s;
        int n = s = Thread.currentThread() instanceof ForkJoinWorkerThread ? this.doJoin() : this.externalInterruptibleAwaitDone();
        if ((s &= 0xF0000000) == -1073741824) {
            throw new CancellationException();
        }
        if (s == Integer.MIN_VALUE && (ex = this.getThrowableException()) != null) {
            throw new ExecutionException(ex);
        }
        return this.getRawResult();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        long ns = unit.toNanos(timeout);
        int s = this.status;
        if (s >= 0 && ns > 0L) {
            long deadline = System.nanoTime() + ns;
            ForkJoinPool p = null;
            ForkJoinPool.WorkQueue w = null;
            Thread t = Thread.currentThread();
            if (t instanceof ForkJoinWorkerThread) {
                ForkJoinWorkerThread wt = (ForkJoinWorkerThread)t;
                p = wt.pool;
                w = wt.workQueue;
                p.helpJoinOnce(w, this);
            } else {
                ForkJoinPool cp = ForkJoinPool.common;
                if (cp != null) {
                    if (this instanceof CountedCompleter) {
                        cp.externalHelpComplete((CountedCompleter)this);
                    } else if (cp.tryExternalUnpush(this)) {
                        this.doExec();
                    }
                }
            }
            boolean canBlock = false;
            boolean interrupted = false;
            try {
                while ((s = this.status) >= 0) {
                    if (w != null && w.qlock < 0) {
                        ForkJoinTask.cancelIgnoringExceptions(this);
                        continue;
                    }
                    if (!canBlock) {
                        if (p != null && !p.tryCompensate(p.ctl)) continue;
                        canBlock = true;
                        continue;
                    }
                    long ms = TimeUnit.NANOSECONDS.toMillis(ns);
                    if (ms > 0L && U.compareAndSwapInt(this, STATUS, s, s | 0x10000)) {
                        ForkJoinTask forkJoinTask = this;
                        synchronized (forkJoinTask) {
                            if (this.status >= 0) {
                                try {
                                    this.wait(ms);
                                }
                                catch (InterruptedException ie) {
                                    if (p == null) {
                                        interrupted = true;
                                    }
                                }
                            } else {
                                this.notifyAll();
                            }
                        }
                    }
                    if ((s = this.status) >= 0 && !interrupted && (ns = deadline - System.nanoTime()) > 0L) continue;
                    break;
                }
            }
            finally {
                if (p != null && canBlock) {
                    p.incrementActiveCount();
                }
            }
            if (interrupted) {
                throw new InterruptedException();
            }
        }
        if ((s &= 0xF0000000) != -268435456) {
            if (s == -1073741824) {
                throw new CancellationException();
            }
            if (s != Integer.MIN_VALUE) {
                throw new TimeoutException();
            }
            Throwable ex = this.getThrowableException();
            if (ex != null) {
                throw new ExecutionException(ex);
            }
        }
        return this.getRawResult();
    }

    public final void quietlyJoin() {
        this.doJoin();
    }

    public final void quietlyInvoke() {
        this.doInvoke();
    }

    public static void helpQuiesce() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread)t;
            wt.pool.helpQuiescePool(wt.workQueue);
        } else {
            ForkJoinPool.quiesceCommonPool();
        }
    }

    public void reinitialize() {
        if ((this.status & 0xF0000000) == Integer.MIN_VALUE) {
            this.clearExceptionalCompletion();
        } else {
            this.status = 0;
        }
    }

    public static ForkJoinPool getPool() {
        Thread t = Thread.currentThread();
        return t instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread)t).pool : null;
    }

    public static boolean inForkJoinPool() {
        return Thread.currentThread() instanceof ForkJoinWorkerThread;
    }

    public boolean tryUnfork() {
        Thread t = Thread.currentThread();
        return t instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread)t).workQueue.tryUnpush(this) : ForkJoinPool.common.tryExternalUnpush(this);
    }

    public static int getQueuedTaskCount() {
        Thread t = Thread.currentThread();
        ForkJoinPool.WorkQueue q = t instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread)t).workQueue : ForkJoinPool.commonSubmitterQueue();
        return q == null ? 0 : q.queueSize();
    }

    public static int getSurplusQueuedTaskCount() {
        return ForkJoinPool.getSurplusQueuedTaskCount();
    }

    public abstract V getRawResult();

    protected abstract void setRawResult(V var1);

    protected abstract boolean exec();

    protected static ForkJoinTask<?> peekNextLocalTask() {
        Thread t = Thread.currentThread();
        ForkJoinPool.WorkQueue q = t instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread)t).workQueue : ForkJoinPool.commonSubmitterQueue();
        return q == null ? null : q.peek();
    }

    protected static ForkJoinTask<?> pollNextLocalTask() {
        Thread t = Thread.currentThread();
        return t instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread)t).workQueue.nextLocalTask() : null;
    }

    protected static ForkJoinTask<?> pollTask() {
        ForkJoinTask<?> forkJoinTask;
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread)t;
            forkJoinTask = wt.pool.nextTaskFor(wt.workQueue);
        } else {
            forkJoinTask = null;
        }
        return forkJoinTask;
    }

    public final short getForkJoinTaskTag() {
        return (short)this.status;
    }

    public final short setForkJoinTaskTag(short tag) {
        int s;
        while (!U.compareAndSwapInt(this, STATUS, s = this.status, s & 0xFFFF0000 | tag & 0xFFFF)) {
        }
        return (short)s;
    }

    public final boolean compareAndSetForkJoinTaskTag(short e, short tag) {
        int s;
        do {
            if ((short)(s = this.status) == e) continue;
            return false;
        } while (!U.compareAndSwapInt(this, STATUS, s, s & 0xFFFF0000 | tag & 0xFFFF));
        return true;
    }

    public static ForkJoinTask<?> adapt(Runnable runnable) {
        return new AdaptedRunnableAction(runnable);
    }

    public static <T> ForkJoinTask<T> adapt(Runnable runnable, T result) {
        return new AdaptedRunnable<T>(runnable, result);
    }

    public static <T> ForkJoinTask<T> adapt(Callable<? extends T> callable) {
        return new AdaptedCallable<T>(callable);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(this.getException());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Object ex = s.readObject();
        if (ex != null) {
            this.setExceptionalCompletion((Throwable)ex);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (SecurityException tryReflectionInstead) {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                    @Override
                    public Unsafe run() throws Exception {
                        Class<Unsafe> k = Unsafe.class;
                        for (Field f : k.getDeclaredFields()) {
                            f.setAccessible(true);
                            Object x = f.get(null);
                            if (!k.isInstance(x)) continue;
                            return (Unsafe)k.cast(x);
                        }
                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            }
            catch (PrivilegedActionException e) {
                throw new RuntimeException("Could not initialize intrinsics", e.getCause());
            }
        }
    }

    static {
        exceptionTableLock = new ReentrantLock();
        exceptionTableRefQueue = new ReferenceQueue();
        exceptionTable = new ExceptionNode[32];
        try {
            U = ForkJoinTask.getUnsafe();
            Class<ForkJoinTask> k = ForkJoinTask.class;
            STATUS = U.objectFieldOffset(k.getDeclaredField("status"));
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }

    static final class AdaptedCallable<T>
    extends ForkJoinTask<T>
    implements RunnableFuture<T> {
        final Callable<? extends T> callable;
        T result;
        private static final long serialVersionUID = 2838392045355241008L;

        AdaptedCallable(Callable<? extends T> callable) {
            if (callable == null) {
                throw new NullPointerException();
            }
            this.callable = callable;
        }

        @Override
        public final T getRawResult() {
            return this.result;
        }

        @Override
        public final void setRawResult(T v) {
            this.result = v;
        }

        @Override
        public final boolean exec() {
            try {
                this.result = this.callable.call();
                return true;
            }
            catch (Error err) {
                throw err;
            }
            catch (RuntimeException rex) {
                throw rex;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public final void run() {
            this.invoke();
        }
    }

    static final class RunnableExecuteAction
    extends ForkJoinTask<Void> {
        final Runnable runnable;
        private static final long serialVersionUID = 5232453952276885070L;

        RunnableExecuteAction(Runnable runnable) {
            if (runnable == null) {
                throw new NullPointerException();
            }
            this.runnable = runnable;
        }

        @Override
        public final Void getRawResult() {
            return null;
        }

        @Override
        public final void setRawResult(Void v) {
        }

        @Override
        public final boolean exec() {
            this.runnable.run();
            return true;
        }

        @Override
        void internalPropagateException(Throwable ex) {
            RunnableExecuteAction.rethrow(ex);
        }
    }

    static final class AdaptedRunnableAction
    extends ForkJoinTask<Void>
    implements RunnableFuture<Void> {
        final Runnable runnable;
        private static final long serialVersionUID = 5232453952276885070L;

        AdaptedRunnableAction(Runnable runnable) {
            if (runnable == null) {
                throw new NullPointerException();
            }
            this.runnable = runnable;
        }

        @Override
        public final Void getRawResult() {
            return null;
        }

        @Override
        public final void setRawResult(Void v) {
        }

        @Override
        public final boolean exec() {
            this.runnable.run();
            return true;
        }

        @Override
        public final void run() {
            this.invoke();
        }
    }

    static final class AdaptedRunnable<T>
    extends ForkJoinTask<T>
    implements RunnableFuture<T> {
        final Runnable runnable;
        T result;
        private static final long serialVersionUID = 5232453952276885070L;

        AdaptedRunnable(Runnable runnable, T result) {
            if (runnable == null) {
                throw new NullPointerException();
            }
            this.runnable = runnable;
            this.result = result;
        }

        @Override
        public final T getRawResult() {
            return this.result;
        }

        @Override
        public final void setRawResult(T v) {
            this.result = v;
        }

        @Override
        public final boolean exec() {
            this.runnable.run();
            return true;
        }

        @Override
        public final void run() {
            this.invoke();
        }
    }

    static final class ExceptionNode
    extends WeakReference<ForkJoinTask<?>> {
        final Throwable ex;
        ExceptionNode next;
        final long thrower;

        ExceptionNode(ForkJoinTask<?> task, Throwable ex, ExceptionNode next) {
            super(task, exceptionTableRefQueue);
            this.ex = ex;
            this.next = next;
            this.thrower = Thread.currentThread().getId();
        }
    }
}

