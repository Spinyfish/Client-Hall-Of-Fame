/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.chmv8;

import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.chmv8.CountedCompleter;
import io.netty.util.internal.chmv8.ForkJoinTask;
import io.netty.util.internal.chmv8.ForkJoinWorkerThread;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class ForkJoinPool
extends AbstractExecutorService {
    static final ThreadLocal<Submitter> submitters;
    public static final ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
    private static final RuntimePermission modifyThreadPermission;
    static final ForkJoinPool common;
    static final int commonParallelism;
    private static int poolNumberSequence;
    private static final long IDLE_TIMEOUT = 2000000000L;
    private static final long FAST_IDLE_TIMEOUT = 200000000L;
    private static final long TIMEOUT_SLOP = 2000000L;
    private static final int MAX_HELP = 64;
    private static final int SEED_INCREMENT = 1640531527;
    private static final int AC_SHIFT = 48;
    private static final int TC_SHIFT = 32;
    private static final int ST_SHIFT = 31;
    private static final int EC_SHIFT = 16;
    private static final int SMASK = 65535;
    private static final int MAX_CAP = Short.MAX_VALUE;
    private static final int EVENMASK = 65534;
    private static final int SQMASK = 126;
    private static final int SHORT_SIGN = 32768;
    private static final int INT_SIGN = Integer.MIN_VALUE;
    private static final long STOP_BIT = 0x80000000L;
    private static final long AC_MASK = -281474976710656L;
    private static final long TC_MASK = 0xFFFF00000000L;
    private static final long TC_UNIT = 0x100000000L;
    private static final long AC_UNIT = 0x1000000000000L;
    private static final int UAC_SHIFT = 16;
    private static final int UTC_SHIFT = 0;
    private static final int UAC_MASK = -65536;
    private static final int UTC_MASK = 65535;
    private static final int UAC_UNIT = 65536;
    private static final int UTC_UNIT = 1;
    private static final int E_MASK = Integer.MAX_VALUE;
    private static final int E_SEQ = 65536;
    private static final int SHUTDOWN = Integer.MIN_VALUE;
    private static final int PL_LOCK = 2;
    private static final int PL_SIGNAL = 1;
    private static final int PL_SPINS = 256;
    static final int LIFO_QUEUE = 0;
    static final int FIFO_QUEUE = 1;
    static final int SHARED_QUEUE = -1;
    volatile long pad00;
    volatile long pad01;
    volatile long pad02;
    volatile long pad03;
    volatile long pad04;
    volatile long pad05;
    volatile long pad06;
    volatile long stealCount;
    volatile long ctl;
    volatile int plock;
    volatile int indexSeed;
    final short parallelism;
    final short mode;
    WorkQueue[] workQueues;
    final ForkJoinWorkerThreadFactory factory;
    final Thread.UncaughtExceptionHandler ueh;
    final String workerNamePrefix;
    volatile Object pad10;
    volatile Object pad11;
    volatile Object pad12;
    volatile Object pad13;
    volatile Object pad14;
    volatile Object pad15;
    volatile Object pad16;
    volatile Object pad17;
    volatile Object pad18;
    volatile Object pad19;
    volatile Object pad1a;
    volatile Object pad1b;
    private static final Unsafe U;
    private static final long CTL;
    private static final long PARKBLOCKER;
    private static final int ABASE;
    private static final int ASHIFT;
    private static final long STEALCOUNT;
    private static final long PLOCK;
    private static final long INDEXSEED;
    private static final long QBASE;
    private static final long QLOCK;

    private static void checkPermission() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(modifyThreadPermission);
        }
    }

    private static final synchronized int nextPoolId() {
        return ++poolNumberSequence;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int acquirePlock() {
        int spins = 256;
        int nps;
        int ps;
        while (((ps = this.plock) & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps, nps = ps + 2)) {
            if (spins >= 0) {
                if (ThreadLocalRandom.current().nextInt() < 0) continue;
                --spins;
                continue;
            }
            if (!U.compareAndSwapInt(this, PLOCK, ps, ps | 1)) continue;
            ForkJoinPool forkJoinPool = this;
            synchronized (forkJoinPool) {
                if ((this.plock & 1) != 0) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ie) {
                        try {
                            Thread.currentThread().interrupt();
                        }
                        catch (SecurityException ignore) {}
                    }
                } else {
                    this.notifyAll();
                }
            }
        }
        return nps;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void releasePlock(int ps) {
        this.plock = ps;
        ForkJoinPool forkJoinPool = this;
        synchronized (forkJoinPool) {
            this.notifyAll();
        }
    }

    private void tryAddWorker() {
        int e;
        long c;
        int u;
        while ((u = (int)((c = this.ctl) >>> 32)) < 0 && (u & 0x8000) != 0 && (e = (int)c) >= 0) {
            long nc = (long)(u + 1 & 0xFFFF | u + 65536 & 0xFFFF0000) << 32 | (long)e;
            if (!U.compareAndSwapLong(this, CTL, c, nc)) continue;
            Throwable ex = null;
            ForkJoinWorkerThread wt = null;
            try {
                ForkJoinWorkerThreadFactory fac = this.factory;
                if (fac != null && (wt = fac.newThread(this)) != null) {
                    wt.start();
                    break;
                }
            }
            catch (Throwable rex) {
                ex = rex;
            }
            this.deregisterWorker(wt, ex);
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final WorkQueue registerWorker(ForkJoinWorkerThread wt) {
        int s;
        wt.setDaemon(true);
        Thread.UncaughtExceptionHandler handler = this.ueh;
        if (handler != null) {
            wt.setUncaughtExceptionHandler(handler);
        }
        do {
            s = this.indexSeed;
        } while (!U.compareAndSwapInt(this, INDEXSEED, s, s += 1640531527) || s == 0);
        WorkQueue w = new WorkQueue(this, wt, this.mode, s);
        int ps = this.plock;
        if ((ps & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps, ps += 2)) {
            ps = this.acquirePlock();
        }
        int nps = ps & Integer.MIN_VALUE | ps + 2 & Integer.MAX_VALUE;
        try {
            WorkQueue[] ws = this.workQueues;
            if (this.workQueues != null) {
                int n = ws.length;
                int m = n - 1;
                int r = s << 1 | 1;
                if (ws[r &= m] != null) {
                    int step;
                    int probes = 0;
                    int n2 = step = n <= 4 ? 2 : (n >>> 1 & 0xFFFE) + 2;
                    while (ws[r = r + step & m] != null) {
                        if (++probes < n) continue;
                        this.workQueues = ws = Arrays.copyOf(ws, n <<= 1);
                        m = n - 1;
                        probes = 0;
                    }
                }
                w.poolIndex = (short)r;
                w.eventCount = r;
                ws[r] = w;
            }
        }
        finally {
            if (!U.compareAndSwapInt(this, PLOCK, ps, nps)) {
                this.releasePlock(nps);
            }
        }
        wt.setName(this.workerNamePrefix.concat(Integer.toString(w.poolIndex >>> 1)));
        return w;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void deregisterWorker(ForkJoinWorkerThread wt, Throwable ex) {
        long c;
        WorkQueue w = null;
        if (wt != null && (w = wt.workQueue) != null) {
            long sc;
            w.qlock = -1;
            while (!U.compareAndSwapLong(this, STEALCOUNT, sc = this.stealCount, sc + (long)w.nsteals)) {
            }
            int ps = this.plock;
            if ((ps & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps, ps += 2)) {
                ps = this.acquirePlock();
            }
            int nps = ps & Integer.MIN_VALUE | ps + 2 & Integer.MAX_VALUE;
            try {
                short idx = w.poolIndex;
                WorkQueue[] ws = this.workQueues;
                if (ws != null && idx >= 0 && idx < ws.length && ws[idx] == w) {
                    ws[idx] = null;
                }
            }
            finally {
                if (!U.compareAndSwapInt(this, PLOCK, ps, nps)) {
                    this.releasePlock(nps);
                }
            }
        }
        while (!U.compareAndSwapLong(this, CTL, c = this.ctl, c - 0x1000000000000L & 0xFFFF000000000000L | c - 0x100000000L & 0xFFFF00000000L | c & 0xFFFFFFFFL)) {
        }
        if (!this.tryTerminate(false, false) && w != null && w.array != null) {
            int e;
            int u;
            w.cancelAll();
            while ((u = (int)((c = this.ctl) >>> 32)) < 0 && (e = (int)c) >= 0) {
                if (e > 0) {
                    WorkQueue v;
                    int i;
                    WorkQueue[] ws = this.workQueues;
                    if (this.workQueues == null || (i = e & 0xFFFF) >= ws.length || (v = ws[i]) == null) break;
                    long nc = (long)(v.nextWait & Integer.MAX_VALUE) | (long)(u + 65536) << 32;
                    if (v.eventCount != (e | Integer.MIN_VALUE)) break;
                    if (!U.compareAndSwapLong(this, CTL, c, nc)) continue;
                    v.eventCount = e + 65536 & Integer.MAX_VALUE;
                    Thread p = v.parker;
                    if (p == null) break;
                    U.unpark(p);
                    break;
                }
                if ((short)u >= 0) break;
                this.tryAddWorker();
                break;
            }
        }
        if (ex == null) {
            ForkJoinTask.helpExpungeStaleExceptions();
        } else {
            ForkJoinTask.rethrow(ex);
        }
    }

    final void externalPush(ForkJoinTask<?> task) {
        int r;
        WorkQueue q;
        int m;
        Submitter z = submitters.get();
        int ps = this.plock;
        WorkQueue[] ws = this.workQueues;
        if (z != null && ps > 0 && ws != null && (m = ws.length - 1) >= 0 && (q = ws[m & (r = z.seed) & 0x7E]) != null && r != 0 && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
            int s;
            int n;
            int am;
            ForkJoinTask<?>[] a = q.array;
            if (q.array != null && (am = a.length - 1) > (n = (s = q.top) - q.base)) {
                int j = ((am & s) << ASHIFT) + ABASE;
                U.putOrderedObject(a, j, task);
                q.top = s + 1;
                q.qlock = 0;
                if (n <= 1) {
                    this.signalWork(ws, q);
                }
                return;
            }
            q.qlock = 0;
        }
        this.fullExternalPush(task);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fullExternalPush(ForkJoinTask<?> task) {
        int r = 0;
        Submitter z = submitters.get();
        while (true) {
            int m;
            WorkQueue[] ws;
            int ps;
            block25: {
                int nps;
                short p;
                block24: {
                    if (z == null) {
                        r = this.indexSeed;
                        if (U.compareAndSwapInt(this, INDEXSEED, r, r += 1640531527) && r != 0) {
                            z = new Submitter(r);
                            submitters.set(z);
                        }
                    } else if (r == 0) {
                        r = z.seed;
                        r ^= r << 13;
                        r ^= r >>> 17;
                        r ^= r << 5;
                        z.seed = r;
                    }
                    if ((ps = this.plock) < 0) {
                        throw new RejectedExecutionException();
                    }
                    if (ps == 0) break block24;
                    ws = this.workQueues;
                    if (this.workQueues != null && (m = ws.length - 1) >= 0) break block25;
                }
                int n = (p = this.parallelism) > 1 ? p - 1 : 1;
                n |= n >>> 1;
                n |= n >>> 2;
                n |= n >>> 4;
                n |= n >>> 8;
                n |= n >>> 16;
                n = n + 1 << 1;
                ws = this.workQueues;
                WorkQueue[] nws = this.workQueues == null || ws.length == 0 ? new WorkQueue[n] : null;
                ps = this.plock;
                if ((ps & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps, ps += 2)) {
                    ps = this.acquirePlock();
                }
                ws = this.workQueues;
                if ((this.workQueues == null || ws.length == 0) && nws != null) {
                    this.workQueues = nws;
                }
                if (U.compareAndSwapInt(this, PLOCK, ps, nps = ps & Integer.MIN_VALUE | ps + 2 & Integer.MAX_VALUE)) continue;
                this.releasePlock(nps);
                continue;
            }
            int k = r & m & 0x7E;
            WorkQueue q = ws[k];
            if (q != null) {
                if (q.qlock == 0 && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
                    ForkJoinTask<?>[] a = q.array;
                    int s = q.top;
                    boolean submitted = false;
                    try {
                        if (a != null && a.length > s + 1 - q.base || (a = q.growArray()) != null) {
                            int j = ((a.length - 1 & s) << ASHIFT) + ABASE;
                            U.putOrderedObject(a, j, task);
                            q.top = s + 1;
                            submitted = true;
                        }
                    }
                    finally {
                        q.qlock = 0;
                    }
                    if (submitted) {
                        this.signalWork(ws, q);
                        return;
                    }
                }
                r = 0;
                continue;
            }
            ps = this.plock;
            if ((ps & 2) == 0) {
                int nps;
                q = new WorkQueue(this, null, -1, r);
                q.poolIndex = (short)k;
                ps = this.plock;
                if ((ps & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps, ps += 2)) {
                    ps = this.acquirePlock();
                }
                ws = this.workQueues;
                if (this.workQueues != null && k < ws.length && ws[k] == null) {
                    ws[k] = q;
                }
                if (U.compareAndSwapInt(this, PLOCK, ps, nps = ps & Integer.MIN_VALUE | ps + 2 & Integer.MAX_VALUE)) continue;
                this.releasePlock(nps);
                continue;
            }
            r = 0;
        }
    }

    final void incrementActiveCount() {
        long c;
        while (!U.compareAndSwapLong(this, CTL, c = this.ctl, c & 0xFFFFFFFFFFFFL | (c & 0xFFFF000000000000L) + 0x1000000000000L)) {
        }
    }

    final void signalWork(WorkQueue[] ws, WorkQueue q) {
        long c;
        int u;
        while ((u = (int)((c = this.ctl) >>> 32)) < 0) {
            WorkQueue w;
            int i;
            int e = (int)c;
            if (e <= 0) {
                if ((short)u >= 0) break;
                this.tryAddWorker();
                break;
            }
            if (ws == null || ws.length <= (i = e & 0xFFFF) || (w = ws[i]) == null) break;
            long nc = (long)(w.nextWait & Integer.MAX_VALUE) | (long)(u + 65536) << 32;
            int ne = e + 65536 & Integer.MAX_VALUE;
            if (w.eventCount == (e | Integer.MIN_VALUE) && U.compareAndSwapLong(this, CTL, c, nc)) {
                w.eventCount = ne;
                Thread p = w.parker;
                if (p == null) break;
                U.unpark(p);
                break;
            }
            if (q == null || q.base < q.top) continue;
            break;
        }
    }

    final void runWorker(WorkQueue w) {
        w.growArray();
        int r = w.hint;
        while (this.scan(w, r) == 0) {
            r ^= r << 13;
            r ^= r >>> 17;
            r ^= r << 5;
        }
    }

    private final int scan(WorkQueue w, int r) {
        block6: {
            int m;
            long c = this.ctl;
            WorkQueue[] ws = this.workQueues;
            if (this.workQueues == null || (m = ws.length - 1) < 0 || w == null) break block6;
            int j = m + m + 1;
            int ec = w.eventCount;
            do {
                int b;
                WorkQueue q;
                if ((q = ws[r - j & m]) == null || (b = q.base) - q.top >= 0) continue;
                ForkJoinTask<?>[] a = q.array;
                if (q.array == null) continue;
                long i = ((a.length - 1 & b) << ASHIFT) + ABASE;
                ForkJoinTask t = (ForkJoinTask)U.getObjectVolatile(a, i);
                if (t == null) break block6;
                if (ec < 0) {
                    this.helpRelease(c, ws, w, q, b);
                    break block6;
                }
                if (q.base != b || !U.compareAndSwapObject(a, i, t, null)) break block6;
                U.putOrderedInt(q, QBASE, b + 1);
                if (b + 1 - q.top < 0) {
                    this.signalWork(ws, q);
                }
                w.runTask(t);
                break block6;
            } while (--j >= 0);
            int e = (int)c;
            if ((ec | e) < 0) {
                return this.awaitWork(w, c, ec);
            }
            if (this.ctl == c) {
                long nc = (long)ec | c - 0x1000000000000L & 0xFFFFFFFF00000000L;
                w.nextWait = e;
                w.eventCount = ec | Integer.MIN_VALUE;
                if (!U.compareAndSwapLong(this, CTL, c, nc)) {
                    w.eventCount = ec;
                }
            }
        }
        return 0;
    }

    private final int awaitWork(WorkQueue w, long c, int ec) {
        int stat = w.qlock;
        if (stat >= 0 && w.eventCount == ec && this.ctl == c && !Thread.interrupted()) {
            int e = (int)c;
            int u = (int)(c >>> 32);
            int d = (u >> 16) + this.parallelism;
            if (e < 0 || d <= 0 && this.tryTerminate(false, false)) {
                w.qlock = -1;
                stat = -1;
            } else {
                int ns = w.nsteals;
                if (ns != 0) {
                    long sc;
                    w.nsteals = 0;
                    while (!U.compareAndSwapLong(this, STEALCOUNT, sc = this.stealCount, sc + (long)ns)) {
                    }
                } else {
                    long deadline;
                    long parkTime;
                    long pc;
                    long l = pc = d > 0 || ec != (e | Integer.MIN_VALUE) ? 0L : (long)(w.nextWait & Integer.MAX_VALUE) | (long)(u + 65536) << 32;
                    if (pc != 0L) {
                        short dc = -((short)(c >>> 32));
                        parkTime = dc < 0 ? 200000000L : (long)(dc + 1) * 2000000000L;
                        deadline = System.nanoTime() + parkTime - 2000000L;
                    } else {
                        deadline = 0L;
                        parkTime = 0L;
                    }
                    if (w.eventCount == ec && this.ctl == c) {
                        Thread wt = Thread.currentThread();
                        U.putObject(wt, PARKBLOCKER, this);
                        w.parker = wt;
                        if (w.eventCount == ec && this.ctl == c) {
                            U.park(false, parkTime);
                        }
                        w.parker = null;
                        U.putObject(wt, PARKBLOCKER, null);
                        if (parkTime != 0L && this.ctl == c && deadline - System.nanoTime() <= 0L && U.compareAndSwapLong(this, CTL, c, pc)) {
                            w.qlock = -1;
                            stat = -1;
                        }
                    }
                }
            }
        }
        return stat;
    }

    private final void helpRelease(long c, WorkQueue[] ws, WorkQueue w, WorkQueue q, int b) {
        WorkQueue v;
        int i;
        int e;
        if (w != null && w.eventCount < 0 && (e = (int)c) > 0 && ws != null && ws.length > (i = e & 0xFFFF) && (v = ws[i]) != null && this.ctl == c) {
            long nc = (long)(v.nextWait & Integer.MAX_VALUE) | (long)((int)(c >>> 32) + 65536) << 32;
            int ne = e + 65536 & Integer.MAX_VALUE;
            if (q != null && q.base == b && w.eventCount < 0 && v.eventCount == (e | Integer.MIN_VALUE) && U.compareAndSwapLong(this, CTL, c, nc)) {
                v.eventCount = ne;
                Thread p = v.parker;
                if (p != null) {
                    U.unpark(p);
                }
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    private int tryHelpStealer(WorkQueue joiner, ForkJoinTask<?> task) {
        stat = 0;
        steps = 0;
        if (task != null && joiner != null && joiner.base - joiner.top >= 0) {
            block0: while (true) {
                subtask = task;
                j = joiner;
                while (true) {
                    block9: {
                        block8: {
                            if ((s = task.status) < 0) {
                                stat = s;
                                break block0;
                            }
                            ws = this.workQueues;
                            if (this.workQueues == null || (m = ws.length - 1) <= 0) break block0;
                            h = (j.hint | 1) & m;
                            v = ws[h];
                            if (v == null || v.currentSteal != subtask) {
                                origin = h;
                                do {
                                    if (((h = h + 2 & m) & 15) == 1 && (subtask.status < 0 || j.currentJoin != subtask)) continue block0;
                                    v = ws[h];
                                    if (v == null || v.currentSteal != subtask) continue;
                                    j.hint = h;
                                    break block8;
                                } while (h != origin);
                                break block0;
                            }
                        }
                        while (true) {
                            if (subtask.status < 0) continue block0;
                            b = v.base;
                            if (b - v.top >= 0) break block9;
                            a = v.array;
                            if (v.array == null) break block9;
                            i = ((a.length - 1 & b) << ForkJoinPool.ASHIFT) + ForkJoinPool.ABASE;
                            t = (ForkJoinTask<?>)ForkJoinPool.U.getObjectVolatile(a, i);
                            if (subtask.status < 0 || j.currentJoin != subtask || v.currentSteal != subtask) continue block0;
                            stat = 1;
                            if (v.base != b) continue;
                            if (t == null) break block0;
                            if (ForkJoinPool.U.compareAndSwapObject(a, i, t, null)) break;
                        }
                        ForkJoinPool.U.putOrderedInt(v, ForkJoinPool.QBASE, b + 1);
                        ps = joiner.currentSteal;
                        jt = joiner.top;
                        do {
                            joiner.currentSteal = t;
                            t.doExec();
                        } while (task.status >= 0 && joiner.top != jt && (t = joiner.pop()) != null);
                        joiner.currentSteal = ps;
                        break block0;
                    }
                    next = v.currentJoin;
                    if (subtask.status >= 0 && j.currentJoin == subtask && v.currentSteal == subtask) ** break;
                    continue block0;
                    if (next == null || ++steps == 64) break block0;
                    subtask = next;
                    j = v;
                }
                break;
            }
        }
        return stat;
    }

    private int helpComplete(WorkQueue joiner, CountedCompleter<?> task) {
        int m;
        int s = 0;
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null && (m = ws.length - 1) >= 0 && joiner != null && task != null) {
            int j = joiner.poolIndex;
            int scans = m + m + 1;
            long c = 0L;
            int k = scans;
            while ((s = task.status) >= 0) {
                if (joiner.internalPopAndExecCC(task)) {
                    k = scans;
                } else {
                    s = task.status;
                    if (s < 0) break;
                    WorkQueue q = ws[j & m];
                    if (q != null && q.pollAndExecCC(task)) {
                        k = scans;
                    } else if (--k < 0) {
                        if (c == (c = this.ctl)) break;
                        k = scans;
                    }
                }
                j += 2;
            }
        }
        return s;
    }

    final boolean tryCompensate(long c) {
        int m;
        WorkQueue[] ws = this.workQueues;
        short pc = this.parallelism;
        int e = (int)c;
        if (ws != null && (m = ws.length - 1) >= 0 && e >= 0 && this.ctl == c) {
            WorkQueue w = ws[e & m];
            if (e != 0 && w != null) {
                long nc = (long)(w.nextWait & Integer.MAX_VALUE) | c & 0xFFFFFFFF00000000L;
                int ne = e + 65536 & Integer.MAX_VALUE;
                if (w.eventCount == (e | Integer.MIN_VALUE) && U.compareAndSwapLong(this, CTL, c, nc)) {
                    w.eventCount = ne;
                    Thread p = w.parker;
                    if (p != null) {
                        U.unpark(p);
                    }
                    return true;
                }
            } else {
                long nc;
                short tc = (short)(c >>> 32);
                if (tc >= 0 && (int)(c >> 48) + pc > 1) {
                    long nc2 = c - 0x1000000000000L & 0xFFFF000000000000L | c & 0xFFFFFFFFFFFFL;
                    if (U.compareAndSwapLong(this, CTL, c, nc2)) {
                        return true;
                    }
                } else if (tc + pc < Short.MAX_VALUE && U.compareAndSwapLong(this, CTL, c, nc = c + 0x100000000L & 0xFFFF00000000L | c & 0xFFFF0000FFFFFFFFL)) {
                    Throwable ex = null;
                    ForkJoinWorkerThread wt = null;
                    try {
                        ForkJoinWorkerThreadFactory fac = this.factory;
                        if (fac != null && (wt = fac.newThread(this)) != null) {
                            wt.start();
                            return true;
                        }
                    }
                    catch (Throwable rex) {
                        ex = rex;
                    }
                    this.deregisterWorker(wt, ex);
                }
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final int awaitJoin(WorkQueue joiner, ForkJoinTask<?> task) {
        int s = 0;
        if (task != null && (s = task.status) >= 0 && joiner != null) {
            ForkJoinTask<?> prevJoin = joiner.currentJoin;
            joiner.currentJoin = task;
            while (joiner.tryRemoveAndExec(task) && (s = task.status) >= 0) {
            }
            if (s >= 0 && task instanceof CountedCompleter) {
                s = this.helpComplete(joiner, (CountedCompleter)task);
            }
            long cc = 0L;
            while (s >= 0 && (s = task.status) >= 0) {
                long c;
                s = this.tryHelpStealer(joiner, task);
                if (s != 0 || (s = task.status) < 0) continue;
                if (!this.tryCompensate(cc)) {
                    cc = this.ctl;
                    continue;
                }
                if (task.trySetSignal() && (s = task.status) >= 0) {
                    ForkJoinTask<?> forkJoinTask = task;
                    synchronized (forkJoinTask) {
                        if (task.status >= 0) {
                            try {
                                task.wait();
                            }
                            catch (InterruptedException ie) {}
                        } else {
                            task.notifyAll();
                        }
                    }
                }
                while (!U.compareAndSwapLong(this, CTL, c = this.ctl, c & 0xFFFFFFFFFFFFL | (c & 0xFFFF000000000000L) + 0x1000000000000L)) {
                }
            }
            joiner.currentJoin = prevJoin;
        }
        return s;
    }

    final void helpJoinOnce(WorkQueue joiner, ForkJoinTask<?> task) {
        int s;
        if (joiner != null && task != null && (s = task.status) >= 0) {
            ForkJoinTask<?> prevJoin = joiner.currentJoin;
            joiner.currentJoin = task;
            while (joiner.tryRemoveAndExec(task) && (s = task.status) >= 0) {
            }
            if (s >= 0) {
                if (task instanceof CountedCompleter) {
                    this.helpComplete(joiner, (CountedCompleter)task);
                }
                while (task.status >= 0 && this.tryHelpStealer(joiner, task) > 0) {
                }
            }
            joiner.currentJoin = prevJoin;
        }
    }

    private WorkQueue findNonEmptyStealQueue() {
        int ps;
        int r = ThreadLocalRandom.current().nextInt();
        do {
            int m;
            ps = this.plock;
            WorkQueue[] ws = this.workQueues;
            if (this.workQueues == null || (m = ws.length - 1) < 0) continue;
            for (int j = m + 1 << 2; j >= 0; --j) {
                WorkQueue q = ws[(r - j << 1 | 1) & m];
                if (q == null || q.base - q.top >= 0) continue;
                return q;
            }
        } while (this.plock != ps);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    final void helpQuiescePool(WorkQueue w) {
        ForkJoinTask<?> ps = w.currentSteal;
        boolean active = true;
        while (true) {
            long c;
            ForkJoinTask<?> t;
            if ((t = w.nextLocalTask()) != null) {
                t.doExec();
                continue;
            }
            WorkQueue q = this.findNonEmptyStealQueue();
            if (q != null) {
                int b;
                if (!active) {
                    active = true;
                    while (!U.compareAndSwapLong(this, CTL, c = this.ctl, c & 0xFFFFFFFFFFFFL | (c & 0xFFFF000000000000L) + 0x1000000000000L)) {
                    }
                }
                if ((b = q.base) - q.top >= 0 || (t = q.pollAt(b)) == null) continue;
                w.currentSteal = t;
                w.currentSteal.doExec();
                w.currentSteal = ps;
                continue;
            }
            if (active) {
                c = this.ctl;
                long nc = c & 0xFFFFFFFFFFFFL | (c & 0xFFFF000000000000L) - 0x1000000000000L;
                if ((int)(nc >> 48) + this.parallelism == 0) return;
                if (!U.compareAndSwapLong(this, CTL, c, nc)) continue;
                active = false;
                continue;
            }
            c = this.ctl;
            if ((int)(c >> 48) + this.parallelism <= 0 && U.compareAndSwapLong(this, CTL, c, c & 0xFFFFFFFFFFFFL | (c & 0xFFFF000000000000L) + 0x1000000000000L)) return;
        }
    }

    final ForkJoinTask<?> nextTaskFor(WorkQueue w) {
        ForkJoinTask<?> t;
        WorkQueue q;
        int b;
        do {
            if ((t = w.nextLocalTask()) != null) {
                return t;
            }
            q = this.findNonEmptyStealQueue();
            if (q != null) continue;
            return null;
        } while ((b = q.base) - q.top >= 0 || (t = q.pollAt(b)) == null);
        return t;
    }

    static int getSurplusQueuedTaskCount() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread)t;
            ForkJoinPool pool = wt.pool;
            int p = pool.parallelism;
            WorkQueue q = wt.workQueue;
            int n = q.top - q.base;
            int a = (int)(pool.ctl >> 48) + p;
            return n - (a > (p >>>= 1) ? 0 : (a > (p >>>= 1) ? 1 : (a > (p >>>= 1) ? 2 : (a > (p >>>= 1) ? 4 : 8))));
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    private boolean tryTerminate(boolean now, boolean enable) {
        if (this == ForkJoinPool.common) {
            return false;
        }
        ps = this.plock;
        if (ps >= 0) {
            if (!enable) {
                return false;
            }
            if ((ps & 2) != 0 || !ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, ps, ps += 2)) {
                ps = this.acquirePlock();
            }
            if (!ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, ps, nps = ps + 2 & 0x7FFFFFFF | -2147483648)) {
                this.releasePlock(nps);
            }
        }
        block5: while (true) {
            if (((c = this.ctl) & 0x80000000L) != 0L) {
                if ((short)(c >>> 32) + this.parallelism <= 0) {
                    var6_6 = this;
                    synchronized (var6_6) {
                        this.notifyAll();
                    }
                }
                return true;
            }
            if (!now) {
                if ((int)(c >> 48) + this.parallelism > 0) {
                    return false;
                }
                ws = this.workQueues;
                if (this.workQueues != null) {
                    for (i = 0; i < ws.length; ++i) {
                        w = ws[i];
                        if (w == null || w.isEmpty() && ((i & 1) == 0 || w.eventCount < 0)) continue;
                        this.signalWork(ws, w);
                        return false;
                    }
                }
            }
            if (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, c, c | 0x80000000L)) continue;
            pass = 0;
            while (true) {
                if (pass < 3) ** break;
                continue block5;
                ws = this.workQueues;
                if (this.workQueues != null) {
                    n = ws.length;
                    for (i = 0; i < n; ++i) {
                        w = ws[i];
                        if (w == null) continue;
                        w.qlock = -1;
                        if (pass <= 0) continue;
                        w.cancelAll();
                        if (pass <= 1 || (wt = w.owner) == null) continue;
                        if (!wt.isInterrupted()) {
                            try {
                                wt.interrupt();
                            }
                            catch (Throwable ignore) {
                                // empty catch block
                            }
                        }
                        ForkJoinPool.U.unpark(wt);
                    }
                    while ((e = (int)(cc = this.ctl) & 0x7FFFFFFF) != 0 && (i = e & 65535) < n && i >= 0 && (w = ws[i]) != null) {
                        nc = (long)(w.nextWait & 0x7FFFFFFF) | cc + 0x1000000000000L & -281474976710656L | cc & 0xFFFF80000000L;
                        if (w.eventCount != (e | -2147483648) || !ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, cc, nc)) continue;
                        w.eventCount = e + 65536 & 0x7FFFFFFF;
                        w.qlock = -1;
                        p = w.parker;
                        if (p == null) continue;
                        ForkJoinPool.U.unpark(p);
                    }
                }
                ++pass;
            }
            break;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static WorkQueue commonSubmitterQueue() {
        Submitter z = submitters.get();
        if (z == null) return null;
        ForkJoinPool p = common;
        if (p == null) return null;
        WorkQueue[] ws = p.workQueues;
        if (p.workQueues == null) return null;
        int m = ws.length - 1;
        if (m < 0) return null;
        WorkQueue workQueue = ws[m & z.seed & 0x7E];
        return workQueue;
    }

    final boolean tryExternalUnpush(ForkJoinTask<?> task) {
        int s;
        WorkQueue joiner;
        int m;
        Submitter z = submitters.get();
        WorkQueue[] ws = this.workQueues;
        boolean popped = false;
        if (z != null && ws != null && (m = ws.length - 1) >= 0 && (joiner = ws[z.seed & m & 0x7E]) != null && joiner.base != (s = joiner.top)) {
            long j;
            ForkJoinTask<?>[] a = joiner.array;
            if (joiner.array != null && U.getObject(a, j = (long)(((a.length - 1 & s - 1) << ASHIFT) + ABASE)) == task && U.compareAndSwapInt(joiner, QLOCK, 0, 1)) {
                if (joiner.top == s && joiner.array == a && U.compareAndSwapObject(a, j, task, null)) {
                    joiner.top = s - 1;
                    popped = true;
                }
                joiner.qlock = 0;
            }
        }
        return popped;
    }

    final int externalHelpComplete(CountedCompleter<?> task) {
        int j;
        WorkQueue joiner;
        int m;
        Submitter z = submitters.get();
        WorkQueue[] ws = this.workQueues;
        int s = 0;
        if (z != null && ws != null && (m = ws.length - 1) >= 0 && (joiner = ws[(j = z.seed) & m & 0x7E]) != null && task != null) {
            int scans = m + m + 1;
            long c = 0L;
            j |= 1;
            int k = scans;
            while ((s = task.status) >= 0) {
                if (joiner.externalPopAndExecCC(task)) {
                    k = scans;
                } else {
                    s = task.status;
                    if (s < 0) break;
                    WorkQueue q = ws[j & m];
                    if (q != null && q.pollAndExecCC(task)) {
                        k = scans;
                    } else if (--k < 0) {
                        if (c == (c = this.ctl)) break;
                        k = scans;
                    }
                }
                j += 2;
            }
        }
        return s;
    }

    public ForkJoinPool() {
        this(Math.min(Short.MAX_VALUE, Runtime.getRuntime().availableProcessors()), defaultForkJoinWorkerThreadFactory, null, false);
    }

    public ForkJoinPool(int parallelism) {
        this(parallelism, defaultForkJoinWorkerThreadFactory, null, false);
    }

    public ForkJoinPool(int parallelism, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler, boolean asyncMode) {
        this(ForkJoinPool.checkParallelism(parallelism), ForkJoinPool.checkFactory(factory), handler, asyncMode ? 1 : 0, "ForkJoinPool-" + ForkJoinPool.nextPoolId() + "-worker-");
        ForkJoinPool.checkPermission();
    }

    private static int checkParallelism(int parallelism) {
        if (parallelism <= 0 || parallelism > Short.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        return parallelism;
    }

    private static ForkJoinWorkerThreadFactory checkFactory(ForkJoinWorkerThreadFactory factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        return factory;
    }

    private ForkJoinPool(int parallelism, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler, int mode, String workerNamePrefix) {
        this.workerNamePrefix = workerNamePrefix;
        this.factory = factory;
        this.ueh = handler;
        this.mode = (short)mode;
        this.parallelism = (short)parallelism;
        long np = -parallelism;
        this.ctl = np << 48 & 0xFFFF000000000000L | np << 32 & 0xFFFF00000000L;
    }

    public static ForkJoinPool commonPool() {
        return common;
    }

    public <T> T invoke(ForkJoinTask<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        this.externalPush(task);
        return task.join();
    }

    public void execute(ForkJoinTask<?> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        this.externalPush(task);
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        ForkJoinTask job = task instanceof ForkJoinTask ? (ForkJoinTask)((Object)task) : new ForkJoinTask.RunnableExecuteAction(task);
        this.externalPush(job);
    }

    public <T> ForkJoinTask<T> submit(ForkJoinTask<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        this.externalPush(task);
        return task;
    }

    public <T> ForkJoinTask<T> submit(Callable<T> task) {
        ForkJoinTask.AdaptedCallable<T> job = new ForkJoinTask.AdaptedCallable<T>(task);
        this.externalPush(job);
        return job;
    }

    public <T> ForkJoinTask<T> submit(Runnable task, T result) {
        ForkJoinTask.AdaptedRunnable<T> job = new ForkJoinTask.AdaptedRunnable<T>(task, result);
        this.externalPush(job);
        return job;
    }

    public ForkJoinTask<?> submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        ForkJoinTask job = task instanceof ForkJoinTask ? (ForkJoinTask)((Object)task) : new ForkJoinTask.AdaptedRunnableAction(task);
        this.externalPush(job);
        return job;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
        ArrayList<Future<T>> arrayList;
        block7: {
            int size;
            ArrayList<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
            boolean done = false;
            try {
                for (Callable<T> t : tasks) {
                    ForkJoinTask.AdaptedCallable<T> f = new ForkJoinTask.AdaptedCallable<T>(t);
                    futures.add(f);
                    this.externalPush(f);
                }
                int size2 = futures.size();
                for (int i = 0; i < size2; ++i) {
                    ((ForkJoinTask)futures.get(i)).quietlyJoin();
                }
                done = true;
                arrayList = futures;
                if (done) break block7;
                size = futures.size();
            }
            catch (Throwable throwable) {
                if (!done) {
                    int size3 = futures.size();
                    for (int i = 0; i < size3; ++i) {
                        ((Future)futures.get(i)).cancel(false);
                    }
                }
                throw throwable;
            }
            for (int i = 0; i < size; ++i) {
                futures.get(i).cancel(false);
            }
        }
        return arrayList;
    }

    public ForkJoinWorkerThreadFactory getFactory() {
        return this.factory;
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.ueh;
    }

    public int getParallelism() {
        short par = this.parallelism;
        return par > 0 ? par : (short)1;
    }

    public static int getCommonPoolParallelism() {
        return commonParallelism;
    }

    public int getPoolSize() {
        return this.parallelism + (short)(this.ctl >>> 32);
    }

    public boolean getAsyncMode() {
        return this.mode == 1;
    }

    public int getRunningThreadCount() {
        int rc = 0;
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null) {
            for (int i = 1; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w == null || !w.isApparentlyUnblocked()) continue;
                ++rc;
            }
        }
        return rc;
    }

    public int getActiveThreadCount() {
        int r = this.parallelism + (int)(this.ctl >> 48);
        return r <= 0 ? 0 : r;
    }

    public boolean isQuiescent() {
        return this.parallelism + (int)(this.ctl >> 48) <= 0;
    }

    public long getStealCount() {
        long count = this.stealCount;
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null) {
            for (int i = 1; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w == null) continue;
                count += (long)w.nsteals;
            }
        }
        return count;
    }

    public long getQueuedTaskCount() {
        long count = 0L;
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null) {
            for (int i = 1; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w == null) continue;
                count += (long)w.queueSize();
            }
        }
        return count;
    }

    public int getQueuedSubmissionCount() {
        int count = 0;
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null) {
            for (int i = 0; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w == null) continue;
                count += w.queueSize();
            }
        }
        return count;
    }

    public boolean hasQueuedSubmissions() {
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null) {
            for (int i = 0; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w == null || w.isEmpty()) continue;
                return true;
            }
        }
        return false;
    }

    protected ForkJoinTask<?> pollSubmission() {
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null) {
            for (int i = 0; i < ws.length; i += 2) {
                ForkJoinTask<?> t;
                WorkQueue w = ws[i];
                if (w == null || (t = w.poll()) == null) continue;
                return t;
            }
        }
        return null;
    }

    protected int drainTasksTo(Collection<? super ForkJoinTask<?>> c) {
        int count = 0;
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null) {
            for (int i = 0; i < ws.length; ++i) {
                ForkJoinTask<?> t;
                WorkQueue w = ws[i];
                if (w == null) continue;
                while ((t = w.poll()) != null) {
                    c.add(t);
                    ++count;
                }
            }
        }
        return count;
    }

    public String toString() {
        long qt = 0L;
        long qs = 0L;
        int rc = 0;
        long st = this.stealCount;
        long c = this.ctl;
        WorkQueue[] ws = this.workQueues;
        if (this.workQueues != null) {
            for (int i = 0; i < ws.length; ++i) {
                WorkQueue w = ws[i];
                if (w == null) continue;
                int size = w.queueSize();
                if ((i & 1) == 0) {
                    qs += (long)size;
                    continue;
                }
                qt += (long)size;
                st += (long)w.nsteals;
                if (!w.isApparentlyUnblocked()) continue;
                ++rc;
            }
        }
        short pc = this.parallelism;
        int tc = pc + (short)(c >>> 32);
        int ac = pc + (int)(c >> 48);
        if (ac < 0) {
            ac = 0;
        }
        String level = (c & 0x80000000L) != 0L ? (tc == 0 ? "Terminated" : "Terminating") : (this.plock < 0 ? "Shutting down" : "Running");
        return super.toString() + "[" + level + ", parallelism = " + pc + ", size = " + tc + ", active = " + ac + ", running = " + rc + ", steals = " + st + ", tasks = " + qt + ", submissions = " + qs + "]";
    }

    @Override
    public void shutdown() {
        ForkJoinPool.checkPermission();
        this.tryTerminate(false, true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        ForkJoinPool.checkPermission();
        this.tryTerminate(true, true);
        return Collections.emptyList();
    }

    @Override
    public boolean isTerminated() {
        long c = this.ctl;
        return (c & 0x80000000L) != 0L && (short)(c >>> 32) + this.parallelism <= 0;
    }

    public boolean isTerminating() {
        long c = this.ctl;
        return (c & 0x80000000L) != 0L && (short)(c >>> 32) + this.parallelism > 0;
    }

    @Override
    public boolean isShutdown() {
        return this.plock < 0;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        if (this == common) {
            this.awaitQuiescence(timeout, unit);
            return false;
        }
        long nanos = unit.toNanos(timeout);
        if (this.isTerminated()) {
            return true;
        }
        if (nanos <= 0L) {
            return false;
        }
        long deadline = System.nanoTime() + nanos;
        ForkJoinPool forkJoinPool = this;
        synchronized (forkJoinPool) {
            while (true) {
                if (this.isTerminated()) {
                    return true;
                }
                if (nanos <= 0L) {
                    return false;
                }
                long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
                this.wait(millis > 0L ? millis : 1L);
                nanos = deadline - System.nanoTime();
            }
        }
    }

    public boolean awaitQuiescence(long timeout, TimeUnit unit) {
        long nanos = unit.toNanos(timeout);
        Thread thread = Thread.currentThread();
        if (thread instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread)thread;
            if (wt.pool == this) {
                this.helpQuiescePool(wt.workQueue);
                return true;
            }
        }
        long startTime = System.nanoTime();
        int r = 0;
        boolean found = true;
        block0: while (!this.isQuiescent()) {
            int m;
            WorkQueue[] ws = this.workQueues;
            if (this.workQueues == null || (m = ws.length - 1) < 0) break;
            if (!found) {
                if (System.nanoTime() - startTime > nanos) {
                    return false;
                }
                Thread.yield();
            }
            found = false;
            for (int j = m + 1 << 2; j >= 0; --j) {
                int b;
                WorkQueue q;
                if ((q = ws[r++ & m]) == null || (b = q.base) - q.top >= 0) continue;
                found = true;
                ForkJoinTask<?> t = q.pollAt(b);
                if (t == null) continue block0;
                t.doExec();
                continue block0;
            }
        }
        return true;
    }

    static void quiesceCommonPool() {
        common.awaitQuiescence(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void managedBlock(ManagedBlocker blocker) throws InterruptedException {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinPool p = ((ForkJoinWorkerThread)t).pool;
            while (!blocker.isReleasable()) {
                if (!p.tryCompensate(p.ctl)) continue;
                try {
                    while (!blocker.isReleasable() && !blocker.block()) {
                    }
                    break;
                }
                finally {
                    p.incrementActiveCount();
                }
            }
        } else {
            while (!blocker.isReleasable() && !blocker.block()) {
            }
        }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ForkJoinTask.AdaptedRunnable<T>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ForkJoinTask.AdaptedCallable<T>(callable);
    }

    private static ForkJoinPool makeCommonPool() {
        int parallelism = -1;
        ForkJoinWorkerThreadFactory factory = defaultForkJoinWorkerThreadFactory;
        Thread.UncaughtExceptionHandler handler = null;
        try {
            String pp = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
            String fp = System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
            String hp = System.getProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
            if (pp != null) {
                parallelism = Integer.parseInt(pp);
            }
            if (fp != null) {
                factory = (ForkJoinWorkerThreadFactory)ClassLoader.getSystemClassLoader().loadClass(fp).newInstance();
            }
            if (hp != null) {
                handler = (Thread.UncaughtExceptionHandler)ClassLoader.getSystemClassLoader().loadClass(hp).newInstance();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (parallelism < 0 && (parallelism = Runtime.getRuntime().availableProcessors() - 1) < 0) {
            parallelism = 0;
        }
        if (parallelism > Short.MAX_VALUE) {
            parallelism = Short.MAX_VALUE;
        }
        return new ForkJoinPool(parallelism, factory, handler, 0, "ForkJoinPool.commonPool-worker-");
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
        try {
            U = ForkJoinPool.getUnsafe();
            Class<ForkJoinPool> k = ForkJoinPool.class;
            CTL = U.objectFieldOffset(k.getDeclaredField("ctl"));
            STEALCOUNT = U.objectFieldOffset(k.getDeclaredField("stealCount"));
            PLOCK = U.objectFieldOffset(k.getDeclaredField("plock"));
            INDEXSEED = U.objectFieldOffset(k.getDeclaredField("indexSeed"));
            Class<Thread> tk = Thread.class;
            PARKBLOCKER = U.objectFieldOffset(tk.getDeclaredField("parkBlocker"));
            Class<WorkQueue> wk = WorkQueue.class;
            QBASE = U.objectFieldOffset(wk.getDeclaredField("base"));
            QLOCK = U.objectFieldOffset(wk.getDeclaredField("qlock"));
            Class<ForkJoinTask[]> ak = ForkJoinTask[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & scale - 1) != 0) {
                throw new Error("data type scale not a power of two");
            }
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        }
        catch (Exception e) {
            throw new Error(e);
        }
        submitters = new ThreadLocal();
        defaultForkJoinWorkerThreadFactory = new DefaultForkJoinWorkerThreadFactory();
        modifyThreadPermission = new RuntimePermission("modifyThread");
        common = AccessController.doPrivileged(new PrivilegedAction<ForkJoinPool>(){

            @Override
            public ForkJoinPool run() {
                return ForkJoinPool.makeCommonPool();
            }
        });
        short par = ForkJoinPool.common.parallelism;
        commonParallelism = par > 0 ? par : (short)1;
    }

    public static interface ManagedBlocker {
        public boolean block() throws InterruptedException;

        public boolean isReleasable();
    }

    static final class Submitter {
        int seed;

        Submitter(int s) {
            this.seed = s;
        }
    }

    static final class WorkQueue {
        static final int INITIAL_QUEUE_CAPACITY = 8192;
        static final int MAXIMUM_QUEUE_CAPACITY = 0x4000000;
        volatile long pad00;
        volatile long pad01;
        volatile long pad02;
        volatile long pad03;
        volatile long pad04;
        volatile long pad05;
        volatile long pad06;
        volatile int eventCount;
        int nextWait;
        int nsteals;
        int hint;
        short poolIndex;
        final short mode;
        volatile int qlock;
        volatile int base;
        int top;
        ForkJoinTask<?>[] array;
        final ForkJoinPool pool;
        final ForkJoinWorkerThread owner;
        volatile Thread parker;
        volatile ForkJoinTask<?> currentJoin;
        ForkJoinTask<?> currentSteal;
        volatile Object pad10;
        volatile Object pad11;
        volatile Object pad12;
        volatile Object pad13;
        volatile Object pad14;
        volatile Object pad15;
        volatile Object pad16;
        volatile Object pad17;
        volatile Object pad18;
        volatile Object pad19;
        volatile Object pad1a;
        volatile Object pad1b;
        volatile Object pad1c;
        volatile Object pad1d;
        private static final Unsafe U;
        private static final long QBASE;
        private static final long QLOCK;
        private static final int ABASE;
        private static final int ASHIFT;

        WorkQueue(ForkJoinPool pool, ForkJoinWorkerThread owner, int mode, int seed) {
            this.pool = pool;
            this.owner = owner;
            this.mode = (short)mode;
            this.hint = seed;
            this.top = 4096;
            this.base = 4096;
        }

        final int queueSize() {
            int n = this.base - this.top;
            return n >= 0 ? 0 : -n;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        final boolean isEmpty() {
            int s = this.top;
            int n = this.base - s;
            if (n >= 0) return true;
            if (n != -1) return false;
            ForkJoinTask<?>[] a = this.array;
            if (this.array == null) return true;
            int m = a.length - 1;
            if (m < 0) return true;
            if (U.getObject(a, (long)((m & s - 1) << ASHIFT) + (long)ABASE) != null) return false;
            return true;
        }

        final void push(ForkJoinTask<?> task) {
            int s = this.top;
            ForkJoinTask<?>[] a = this.array;
            if (this.array != null) {
                int m = a.length - 1;
                U.putOrderedObject(a, ((m & s) << ASHIFT) + ABASE, task);
                this.top = s + 1;
                int n = this.top - this.base;
                if (n <= 2) {
                    ForkJoinPool p = this.pool;
                    p.signalWork(p.workQueues, this);
                } else if (n >= m) {
                    this.growArray();
                }
            }
        }

        final ForkJoinTask<?>[] growArray() {
            int b;
            int t;
            int oldMask;
            int size;
            ForkJoinTask<?>[] oldA = this.array;
            int n = size = oldA != null ? oldA.length << 1 : 8192;
            if (size > 0x4000000) {
                throw new RejectedExecutionException("Queue capacity exceeded");
            }
            this.array = new ForkJoinTask[size];
            ForkJoinTask[] a = this.array;
            if (oldA != null && (oldMask = oldA.length - 1) >= 0 && (t = this.top) - (b = this.base) > 0) {
                int mask = size - 1;
                do {
                    int oldj = ((b & oldMask) << ASHIFT) + ABASE;
                    int j = ((b & mask) << ASHIFT) + ABASE;
                    ForkJoinTask x = (ForkJoinTask)U.getObjectVolatile(oldA, oldj);
                    if (x == null || !U.compareAndSwapObject(oldA, oldj, x, null)) continue;
                    U.putObjectVolatile(a, j, x);
                } while (++b != t);
            }
            return a;
        }

        final ForkJoinTask<?> pop() {
            int m;
            ForkJoinTask<?>[] a = this.array;
            if (this.array != null && (m = a.length - 1) >= 0) {
                long j;
                ForkJoinTask t;
                int s;
                while ((s = this.top - 1) - this.base >= 0 && (t = (ForkJoinTask)U.getObject(a, j = (long)(((m & s) << ASHIFT) + ABASE))) != null) {
                    if (!U.compareAndSwapObject(a, j, t, null)) continue;
                    this.top = s;
                    return t;
                }
            }
            return null;
        }

        final ForkJoinTask<?> pollAt(int b) {
            int j;
            ForkJoinTask t;
            ForkJoinTask<?>[] a = this.array;
            if (this.array != null && (t = (ForkJoinTask)U.getObjectVolatile(a, j = ((a.length - 1 & b) << ASHIFT) + ABASE)) != null && this.base == b && U.compareAndSwapObject(a, j, t, null)) {
                U.putOrderedInt(this, QBASE, b + 1);
                return t;
            }
            return null;
        }

        final ForkJoinTask<?> poll() {
            int b;
            while ((b = this.base) - this.top < 0) {
                ForkJoinTask<?>[] a = this.array;
                if (this.array == null) break;
                int j = ((a.length - 1 & b) << ASHIFT) + ABASE;
                ForkJoinTask t = (ForkJoinTask)U.getObjectVolatile(a, j);
                if (t != null) {
                    if (!U.compareAndSwapObject(a, j, t, null)) continue;
                    U.putOrderedInt(this, QBASE, b + 1);
                    return t;
                }
                if (this.base != b) continue;
                if (b + 1 == this.top) break;
                Thread.yield();
            }
            return null;
        }

        final ForkJoinTask<?> nextLocalTask() {
            return this.mode == 0 ? this.pop() : this.poll();
        }

        final ForkJoinTask<?> peek() {
            int m;
            ForkJoinTask<?>[] a = this.array;
            if (a == null || (m = a.length - 1) < 0) {
                return null;
            }
            int i = this.mode == 0 ? this.top - 1 : this.base;
            int j = ((i & m) << ASHIFT) + ABASE;
            return (ForkJoinTask)U.getObjectVolatile(a, j);
        }

        final boolean tryUnpush(ForkJoinTask<?> t) {
            int s;
            ForkJoinTask<?>[] a = this.array;
            if (this.array != null && (s = this.top) != this.base && U.compareAndSwapObject(a, ((a.length - 1 & --s) << ASHIFT) + ABASE, t, null)) {
                this.top = s;
                return true;
            }
            return false;
        }

        final void cancelAll() {
            ForkJoinTask<?> t;
            ForkJoinTask.cancelIgnoringExceptions(this.currentJoin);
            ForkJoinTask.cancelIgnoringExceptions(this.currentSteal);
            while ((t = this.poll()) != null) {
                ForkJoinTask.cancelIgnoringExceptions(t);
            }
        }

        final void pollAndExecAll() {
            ForkJoinTask<?> t;
            while ((t = this.poll()) != null) {
                t.doExec();
            }
        }

        final void runTask(ForkJoinTask<?> task) {
            this.currentSteal = task;
            if (this.currentSteal != null) {
                task.doExec();
                ForkJoinTask<?>[] a = this.array;
                short md = this.mode;
                ++this.nsteals;
                this.currentSteal = null;
                if (md != 0) {
                    this.pollAndExecAll();
                } else if (a != null) {
                    long i;
                    ForkJoinTask t;
                    int s;
                    int m = a.length - 1;
                    while ((s = this.top - 1) - this.base >= 0 && (t = (ForkJoinTask)U.getObject(a, i = (long)(((m & s) << ASHIFT) + ABASE))) != null) {
                        if (!U.compareAndSwapObject(a, i, t, null)) continue;
                        this.top = s;
                        t.doExec();
                    }
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        final boolean tryRemoveAndExec(ForkJoinTask<?> task) {
            long j;
            ForkJoinTask t;
            if (task == null) return false;
            ForkJoinTask<?>[] a = this.array;
            if (this.array == null) return false;
            int m = a.length - 1;
            if (m < 0) return false;
            int s = this.top;
            int b = this.base;
            int n = s - b;
            if (n <= 0) return false;
            boolean removed = false;
            boolean empty = true;
            boolean stat = true;
            while ((t = (ForkJoinTask)U.getObject(a, j = (long)(((--s & m) << ASHIFT) + ABASE))) != null) {
                if (t == task) {
                    if (s + 1 == this.top) {
                        if (!U.compareAndSwapObject(a, j, task, null)) break;
                        this.top = s;
                        removed = true;
                        break;
                    }
                    if (this.base != b) break;
                    removed = U.compareAndSwapObject(a, j, task, new EmptyTask());
                    break;
                }
                if (t.status >= 0) {
                    empty = false;
                } else if (s + 1 == this.top) {
                    if (!U.compareAndSwapObject(a, j, t, null)) break;
                    this.top = s;
                    break;
                }
                if (--n != 0) continue;
                if (empty || this.base != b) break;
                stat = false;
                break;
            }
            if (!removed) return stat;
            task.doExec();
            return stat;
        }

        final boolean pollAndExecCC(CountedCompleter<?> root) {
            block5: {
                int b = this.base;
                if (b - this.top >= 0) break block5;
                ForkJoinTask<?>[] a = this.array;
                if (this.array != null) {
                    long j = ((a.length - 1 & b) << ASHIFT) + ABASE;
                    Object o = U.getObjectVolatile(a, j);
                    if (o == null) {
                        return true;
                    }
                    if (o instanceof CountedCompleter) {
                        CountedCompleter<?> t;
                        CountedCompleter<?> r = t = (CountedCompleter<?>)o;
                        do {
                            if (r != root) continue;
                            if (this.base == b && U.compareAndSwapObject(a, j, t, null)) {
                                U.putOrderedInt(this, QBASE, b + 1);
                                t.doExec();
                            }
                            return true;
                        } while ((r = r.completer) != null);
                    }
                }
            }
            return false;
        }

        final boolean externalPopAndExecCC(CountedCompleter<?> root) {
            block5: {
                long j;
                Object o;
                int s = this.top;
                if (this.base - s >= 0) break block5;
                ForkJoinTask<?>[] a = this.array;
                if (this.array != null && (o = U.getObject(a, j = (long)(((a.length - 1 & s - 1) << ASHIFT) + ABASE))) instanceof CountedCompleter) {
                    CountedCompleter<?> t;
                    CountedCompleter<?> r = t = (CountedCompleter<?>)o;
                    do {
                        if (r != root) continue;
                        if (U.compareAndSwapInt(this, QLOCK, 0, 1)) {
                            if (this.top == s && this.array == a && U.compareAndSwapObject(a, j, t, null)) {
                                this.top = s - 1;
                                this.qlock = 0;
                                t.doExec();
                            } else {
                                this.qlock = 0;
                            }
                        }
                        return true;
                    } while ((r = r.completer) != null);
                }
            }
            return false;
        }

        final boolean internalPopAndExecCC(CountedCompleter<?> root) {
            block3: {
                long j;
                Object o;
                int s = this.top;
                if (this.base - s >= 0) break block3;
                ForkJoinTask<?>[] a = this.array;
                if (this.array != null && (o = U.getObject(a, j = (long)(((a.length - 1 & s - 1) << ASHIFT) + ABASE))) instanceof CountedCompleter) {
                    CountedCompleter<?> t;
                    CountedCompleter<?> r = t = (CountedCompleter<?>)o;
                    do {
                        if (r != root) continue;
                        if (U.compareAndSwapObject(a, j, t, null)) {
                            this.top = s - 1;
                            t.doExec();
                        }
                        return true;
                    } while ((r = r.completer) != null);
                }
            }
            return false;
        }

        final boolean isApparentlyUnblocked() {
            Thread.State s;
            ForkJoinWorkerThread wt;
            return this.eventCount >= 0 && (wt = this.owner) != null && (s = wt.getState()) != Thread.State.BLOCKED && s != Thread.State.WAITING && s != Thread.State.TIMED_WAITING;
        }

        static {
            try {
                U = ForkJoinPool.getUnsafe();
                Class<WorkQueue> k = WorkQueue.class;
                Class<ForkJoinTask[]> ak = ForkJoinTask[].class;
                QBASE = U.objectFieldOffset(k.getDeclaredField("base"));
                QLOCK = U.objectFieldOffset(k.getDeclaredField("qlock"));
                ABASE = U.arrayBaseOffset(ak);
                int scale = U.arrayIndexScale(ak);
                if ((scale & scale - 1) != 0) {
                    throw new Error("data type scale not a power of two");
                }
                ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
            }
            catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    static final class EmptyTask
    extends ForkJoinTask<Void> {
        private static final long serialVersionUID = -7721805057305804111L;

        EmptyTask() {
            this.status = -268435456;
        }

        @Override
        public final Void getRawResult() {
            return null;
        }

        @Override
        public final void setRawResult(Void x) {
        }

        @Override
        public final boolean exec() {
            return true;
        }
    }

    static final class DefaultForkJoinWorkerThreadFactory
    implements ForkJoinWorkerThreadFactory {
        DefaultForkJoinWorkerThreadFactory() {
        }

        @Override
        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ForkJoinWorkerThread(pool);
        }
    }

    public static interface ForkJoinWorkerThreadFactory {
        public ForkJoinWorkerThread newThread(ForkJoinPool var1);
    }
}

