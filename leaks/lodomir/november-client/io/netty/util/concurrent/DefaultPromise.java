/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.Signal;
import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultFutureListeners;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GenericProgressiveFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class DefaultPromise<V>
extends AbstractFuture<V>
implements Promise<V> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromise.class);
    private static final InternalLogger rejectedExecutionLogger = InternalLoggerFactory.getInstance(DefaultPromise.class.getName() + ".rejectedExecution");
    private static final int MAX_LISTENER_STACK_DEPTH = 8;
    private static final Signal SUCCESS = Signal.valueOf(DefaultPromise.class.getName() + ".SUCCESS");
    private static final Signal UNCANCELLABLE = Signal.valueOf(DefaultPromise.class.getName() + ".UNCANCELLABLE");
    private static final CauseHolder CANCELLATION_CAUSE_HOLDER = new CauseHolder(new CancellationException());
    private final EventExecutor executor;
    private volatile Object result;
    private Object listeners;
    private LateListeners lateListeners;
    private short waiters;

    public DefaultPromise(EventExecutor executor) {
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        this.executor = executor;
    }

    protected DefaultPromise() {
        this.executor = null;
    }

    protected EventExecutor executor() {
        return this.executor;
    }

    @Override
    public boolean isCancelled() {
        return DefaultPromise.isCancelled0(this.result);
    }

    private static boolean isCancelled0(Object result) {
        return result instanceof CauseHolder && ((CauseHolder)result).cause instanceof CancellationException;
    }

    @Override
    public boolean isCancellable() {
        return this.result == null;
    }

    @Override
    public boolean isDone() {
        return DefaultPromise.isDone0(this.result);
    }

    private static boolean isDone0(Object result) {
        return result != null && result != UNCANCELLABLE;
    }

    @Override
    public boolean isSuccess() {
        Object result = this.result;
        if (result == null || result == UNCANCELLABLE) {
            return false;
        }
        return !(result instanceof CauseHolder);
    }

    @Override
    public Throwable cause() {
        Object result = this.result;
        if (result instanceof CauseHolder) {
            return ((CauseHolder)result).cause;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        if (this.isDone()) {
            this.notifyLateListener(listener);
            return this;
        }
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            if (!this.isDone()) {
                if (this.listeners == null) {
                    this.listeners = listener;
                } else if (this.listeners instanceof DefaultFutureListeners) {
                    ((DefaultFutureListeners)this.listeners).add(listener);
                } else {
                    GenericFutureListener firstListener = (GenericFutureListener)this.listeners;
                    this.listeners = new DefaultFutureListeners(firstListener, listener);
                }
                return this;
            }
        }
        this.notifyLateListener(listener);
        return this;
    }

    @Override
    public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>> ... listeners) {
        if (listeners == null) {
            throw new NullPointerException("listeners");
        }
        for (GenericFutureListener<? extends Future<? super V>> l : listeners) {
            if (l == null) break;
            this.addListener((GenericFutureListener)l);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        if (this.isDone()) {
            return this;
        }
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            if (!this.isDone()) {
                if (this.listeners instanceof DefaultFutureListeners) {
                    ((DefaultFutureListeners)this.listeners).remove(listener);
                } else if (this.listeners == listener) {
                    this.listeners = null;
                }
            }
        }
        return this;
    }

    @Override
    public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>> ... listeners) {
        if (listeners == null) {
            throw new NullPointerException("listeners");
        }
        for (GenericFutureListener<? extends Future<? super V>> l : listeners) {
            if (l == null) break;
            this.removeListener((GenericFutureListener)l);
        }
        return this;
    }

    @Override
    public Promise<V> sync() throws InterruptedException {
        this.await();
        this.rethrowIfFailed();
        return this;
    }

    @Override
    public Promise<V> syncUninterruptibly() {
        this.awaitUninterruptibly();
        this.rethrowIfFailed();
        return this;
    }

    private void rethrowIfFailed() {
        Throwable cause = this.cause();
        if (cause == null) {
            return;
        }
        PlatformDependent.throwException(cause);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> await() throws InterruptedException {
        if (this.isDone()) {
            return this;
        }
        if (Thread.interrupted()) {
            throw new InterruptedException(this.toString());
        }
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            while (!this.isDone()) {
                this.checkDeadLock();
                this.incWaiters();
                try {
                    this.wait();
                }
                finally {
                    this.decWaiters();
                }
            }
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.await0(unit.toNanos(timeout), true);
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return this.await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> awaitUninterruptibly() {
        if (this.isDone()) {
            return this;
        }
        boolean interrupted = false;
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            while (!this.isDone()) {
                this.checkDeadLock();
                this.incWaiters();
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
                finally {
                    this.decWaiters();
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        try {
            return this.await0(unit.toNanos(timeout), false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        try {
            return this.await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    /*
     * Exception decompiling
     */
    private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 10[MONITOR]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    protected void checkDeadLock() {
        EventExecutor e = this.executor();
        if (e != null && e.inEventLoop()) {
            throw new BlockingOperationException(this.toString());
        }
    }

    @Override
    public Promise<V> setSuccess(V result) {
        if (this.setSuccess0(result)) {
            this.notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this);
    }

    @Override
    public boolean trySuccess(V result) {
        if (this.setSuccess0(result)) {
            this.notifyListeners();
            return true;
        }
        return false;
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        if (this.setFailure0(cause)) {
            this.notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this, cause);
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        if (this.setFailure0(cause)) {
            this.notifyListeners();
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Object result = this.result;
        if (DefaultPromise.isDone0(result) || result == UNCANCELLABLE) {
            return false;
        }
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            result = this.result;
            if (DefaultPromise.isDone0(result) || result == UNCANCELLABLE) {
                return false;
            }
            this.result = CANCELLATION_CAUSE_HOLDER;
            if (this.hasWaiters()) {
                this.notifyAll();
            }
        }
        this.notifyListeners();
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean setUncancellable() {
        Object result = this.result;
        if (DefaultPromise.isDone0(result)) {
            return !DefaultPromise.isCancelled0(result);
        }
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            result = this.result;
            if (DefaultPromise.isDone0(result)) {
                return !DefaultPromise.isCancelled0(result);
            }
            this.result = UNCANCELLABLE;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean setFailure0(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        if (this.isDone()) {
            return false;
        }
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            if (this.isDone()) {
                return false;
            }
            this.result = new CauseHolder(cause);
            if (this.hasWaiters()) {
                this.notifyAll();
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean setSuccess0(V result) {
        if (this.isDone()) {
            return false;
        }
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            if (this.isDone()) {
                return false;
            }
            this.result = result == null ? SUCCESS : result;
            if (this.hasWaiters()) {
                this.notifyAll();
            }
        }
        return true;
    }

    @Override
    public V getNow() {
        Object result = this.result;
        if (result instanceof CauseHolder || result == SUCCESS) {
            return null;
        }
        return (V)result;
    }

    private boolean hasWaiters() {
        return this.waiters > 0;
    }

    private void incWaiters() {
        if (this.waiters == Short.MAX_VALUE) {
            throw new IllegalStateException("too many waiters: " + this);
        }
        this.waiters = (short)(this.waiters + 1);
    }

    private void decWaiters() {
        this.waiters = (short)(this.waiters - 1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyListeners() {
        InternalThreadLocalMap threadLocals;
        int stackDepth;
        Object listeners = this.listeners;
        if (listeners == null) {
            return;
        }
        EventExecutor executor = this.executor();
        if (executor.inEventLoop() && (stackDepth = (threadLocals = InternalThreadLocalMap.get()).futureListenerStackDepth()) < 8) {
            threadLocals.setFutureListenerStackDepth(stackDepth + 1);
            try {
                if (listeners instanceof DefaultFutureListeners) {
                    DefaultPromise.notifyListeners0(this, (DefaultFutureListeners)listeners);
                } else {
                    GenericFutureListener l = (GenericFutureListener)listeners;
                    DefaultPromise.notifyListener0(this, l);
                }
            }
            finally {
                this.listeners = null;
                threadLocals.setFutureListenerStackDepth(stackDepth);
            }
            return;
        }
        if (listeners instanceof DefaultFutureListeners) {
            final DefaultFutureListeners dfl = (DefaultFutureListeners)listeners;
            DefaultPromise.execute(executor, new Runnable(){

                @Override
                public void run() {
                    DefaultPromise.notifyListeners0(DefaultPromise.this, dfl);
                    DefaultPromise.this.listeners = null;
                }
            });
        } else {
            final GenericFutureListener l = (GenericFutureListener)listeners;
            DefaultPromise.execute(executor, new Runnable(){

                @Override
                public void run() {
                    DefaultPromise.notifyListener0(DefaultPromise.this, l);
                    DefaultPromise.this.listeners = null;
                }
            });
        }
    }

    private static void notifyListeners0(Future<?> future, DefaultFutureListeners listeners) {
        GenericFutureListener<? extends Future<?>>[] a = listeners.listeners();
        int size = listeners.size();
        for (int i = 0; i < size; ++i) {
            DefaultPromise.notifyListener0(future, a[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyLateListener(GenericFutureListener<?> l) {
        EventExecutor executor = this.executor();
        if (executor.inEventLoop()) {
            if (this.listeners == null && this.lateListeners == null) {
                InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
                int stackDepth = threadLocals.futureListenerStackDepth();
                if (stackDepth < 8) {
                    threadLocals.setFutureListenerStackDepth(stackDepth + 1);
                    try {
                        DefaultPromise.notifyListener0(this, l);
                    }
                    finally {
                        threadLocals.setFutureListenerStackDepth(stackDepth);
                    }
                    return;
                }
            } else {
                LateListeners lateListeners = this.lateListeners;
                if (lateListeners == null) {
                    this.lateListeners = lateListeners = new LateListeners();
                }
                lateListeners.add(l);
                DefaultPromise.execute(executor, lateListeners);
                return;
            }
        }
        DefaultPromise.execute(executor, new LateListenerNotifier(l));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static void notifyListener(EventExecutor eventExecutor, final Future<?> future, final GenericFutureListener<?> l) {
        InternalThreadLocalMap threadLocals;
        int stackDepth;
        if (eventExecutor.inEventLoop() && (stackDepth = (threadLocals = InternalThreadLocalMap.get()).futureListenerStackDepth()) < 8) {
            threadLocals.setFutureListenerStackDepth(stackDepth + 1);
            try {
                DefaultPromise.notifyListener0(future, l);
            }
            finally {
                threadLocals.setFutureListenerStackDepth(stackDepth);
            }
            return;
        }
        DefaultPromise.execute(eventExecutor, new Runnable(){

            @Override
            public void run() {
                DefaultPromise.notifyListener0(future, l);
            }
        });
    }

    private static void execute(EventExecutor executor, Runnable task) {
        try {
            executor.execute(task);
        }
        catch (Throwable t) {
            rejectedExecutionLogger.error("Failed to submit a listener notification task. Event loop shut down?", t);
        }
    }

    static void notifyListener0(Future future, GenericFutureListener l) {
        block2: {
            try {
                l.operationComplete(future);
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationComplete()", t);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private synchronized Object progressiveListeners() {
        Object listeners = this.listeners;
        if (listeners == null) {
            return null;
        }
        if (listeners instanceof DefaultFutureListeners) {
            void var7_12;
            DefaultFutureListeners dfl = (DefaultFutureListeners)listeners;
            int progressiveSize = dfl.progressiveSize();
            switch (progressiveSize) {
                case 0: {
                    return null;
                }
                case 1: {
                    for (GenericFutureListener<Future<?>> genericFutureListener : dfl.listeners()) {
                        if (!(genericFutureListener instanceof GenericProgressiveFutureListener)) continue;
                        return genericFutureListener;
                    }
                    return null;
                }
            }
            GenericFutureListener<? extends Future<?>>[] array = dfl.listeners();
            GenericProgressiveFutureListener[] copy = new GenericProgressiveFutureListener[progressiveSize];
            int i = 0;
            boolean bl = false;
            while (var7_12 < progressiveSize) {
                GenericFutureListener<Future<?>> l = array[i];
                if (l instanceof GenericProgressiveFutureListener) {
                    copy[++var7_12] = (GenericProgressiveFutureListener)l;
                }
                ++i;
            }
            return copy;
        }
        if (listeners instanceof GenericProgressiveFutureListener) {
            return listeners;
        }
        return null;
    }

    void notifyProgressiveListeners(final long progress, final long total) {
        Object listeners = this.progressiveListeners();
        if (listeners == null) {
            return;
        }
        final ProgressiveFuture self = (ProgressiveFuture)((Object)this);
        EventExecutor executor = this.executor();
        if (executor.inEventLoop()) {
            if (listeners instanceof GenericProgressiveFutureListener[]) {
                DefaultPromise.notifyProgressiveListeners0(self, (GenericProgressiveFutureListener[])listeners, progress, total);
            } else {
                DefaultPromise.notifyProgressiveListener0(self, (GenericProgressiveFutureListener)listeners, progress, total);
            }
        } else if (listeners instanceof GenericProgressiveFutureListener[]) {
            final GenericProgressiveFutureListener[] array = (GenericProgressiveFutureListener[])listeners;
            DefaultPromise.execute(executor, new Runnable(){

                @Override
                public void run() {
                    DefaultPromise.notifyProgressiveListeners0(self, array, progress, total);
                }
            });
        } else {
            final GenericProgressiveFutureListener l = (GenericProgressiveFutureListener)listeners;
            DefaultPromise.execute(executor, new Runnable(){

                @Override
                public void run() {
                    DefaultPromise.notifyProgressiveListener0(self, l, progress, total);
                }
            });
        }
    }

    private static void notifyProgressiveListeners0(ProgressiveFuture<?> future, GenericProgressiveFutureListener<?>[] listeners, long progress, long total) {
        for (GenericProgressiveFutureListener<?> l : listeners) {
            if (l == null) break;
            DefaultPromise.notifyProgressiveListener0(future, l, progress, total);
        }
    }

    private static void notifyProgressiveListener0(ProgressiveFuture future, GenericProgressiveFutureListener l, long progress, long total) {
        block2: {
            try {
                l.operationProgressed(future, progress, total);
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationProgressed()", t);
            }
        }
    }

    public String toString() {
        return this.toStringBuilder().toString();
    }

    protected StringBuilder toStringBuilder() {
        StringBuilder buf = new StringBuilder(64);
        buf.append(StringUtil.simpleClassName(this));
        buf.append('@');
        buf.append(Integer.toHexString(this.hashCode()));
        Object result = this.result;
        if (result == SUCCESS) {
            buf.append("(success)");
        } else if (result == UNCANCELLABLE) {
            buf.append("(uncancellable)");
        } else if (result instanceof CauseHolder) {
            buf.append("(failure(");
            buf.append(((CauseHolder)result).cause);
            buf.append(')');
        } else {
            buf.append("(incomplete)");
        }
        return buf;
    }

    static {
        DefaultPromise.CANCELLATION_CAUSE_HOLDER.cause.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    }

    private final class LateListenerNotifier
    implements Runnable {
        private GenericFutureListener<?> l;

        LateListenerNotifier(GenericFutureListener<?> l) {
            this.l = l;
        }

        @Override
        public void run() {
            LateListeners lateListeners = DefaultPromise.this.lateListeners;
            if (this.l != null) {
                if (lateListeners == null) {
                    lateListeners = new LateListeners();
                    DefaultPromise.this.lateListeners = lateListeners;
                }
                lateListeners.add(this.l);
                this.l = null;
            }
            lateListeners.run();
        }
    }

    private final class LateListeners
    extends ArrayDeque<GenericFutureListener<?>>
    implements Runnable {
        private static final long serialVersionUID = -687137418080392244L;

        LateListeners() {
            super(2);
        }

        @Override
        public void run() {
            if (DefaultPromise.this.listeners == null) {
                GenericFutureListener l;
                while ((l = (GenericFutureListener)this.poll()) != null) {
                    DefaultPromise.notifyListener0(DefaultPromise.this, l);
                }
            } else {
                DefaultPromise.execute(DefaultPromise.this.executor(), this);
            }
        }
    }

    private static final class CauseHolder {
        final Throwable cause;

        CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }
}

