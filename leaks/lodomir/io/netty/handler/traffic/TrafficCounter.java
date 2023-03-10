/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.traffic;

import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TrafficCounter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(TrafficCounter.class);
    private final AtomicLong currentWrittenBytes = new AtomicLong();
    private final AtomicLong currentReadBytes = new AtomicLong();
    private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
    private final AtomicLong cumulativeReadBytes = new AtomicLong();
    private long lastCumulativeTime;
    private long lastWriteThroughput;
    private long lastReadThroughput;
    private final AtomicLong lastTime = new AtomicLong();
    private long lastWrittenBytes;
    private long lastReadBytes;
    private long lastNonNullWrittenBytes;
    private long lastNonNullWrittenTime;
    private long lastNonNullReadTime;
    private long lastNonNullReadBytes;
    final AtomicLong checkInterval = new AtomicLong(1000L);
    final String name;
    private final AbstractTrafficShapingHandler trafficShapingHandler;
    private final ScheduledExecutorService executor;
    private Runnable monitor;
    private volatile ScheduledFuture<?> scheduledFuture;
    final AtomicBoolean monitorActive = new AtomicBoolean();

    public synchronized void start() {
        if (this.monitorActive.get()) {
            return;
        }
        this.lastTime.set(System.currentTimeMillis());
        if (this.checkInterval.get() > 0L) {
            this.monitorActive.set(true);
            this.monitor = new TrafficMonitoringTask(this.trafficShapingHandler, this);
            this.scheduledFuture = this.executor.schedule(this.monitor, this.checkInterval.get(), TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void stop() {
        if (!this.monitorActive.get()) {
            return;
        }
        this.monitorActive.set(false);
        this.resetAccounting(System.currentTimeMillis());
        if (this.trafficShapingHandler != null) {
            this.trafficShapingHandler.doAccounting(this);
        }
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
        }
    }

    synchronized void resetAccounting(long newLastTime) {
        long interval = newLastTime - this.lastTime.getAndSet(newLastTime);
        if (interval == 0L) {
            return;
        }
        if (logger.isDebugEnabled() && interval > 2L * this.checkInterval()) {
            logger.debug("Acct schedule not ok: " + interval + " > 2*" + this.checkInterval() + " from " + this.name);
        }
        this.lastReadBytes = this.currentReadBytes.getAndSet(0L);
        this.lastWrittenBytes = this.currentWrittenBytes.getAndSet(0L);
        this.lastReadThroughput = this.lastReadBytes / interval * 1000L;
        this.lastWriteThroughput = this.lastWrittenBytes / interval * 1000L;
        if (this.lastWrittenBytes > 0L) {
            this.lastNonNullWrittenBytes = this.lastWrittenBytes;
            this.lastNonNullWrittenTime = newLastTime;
        }
        if (this.lastReadBytes > 0L) {
            this.lastNonNullReadBytes = this.lastReadBytes;
            this.lastNonNullReadTime = newLastTime;
        }
    }

    public TrafficCounter(AbstractTrafficShapingHandler trafficShapingHandler, ScheduledExecutorService executor, String name, long checkInterval) {
        this.trafficShapingHandler = trafficShapingHandler;
        this.executor = executor;
        this.name = name;
        this.lastCumulativeTime = System.currentTimeMillis();
        this.configure(checkInterval);
    }

    public void configure(long newcheckInterval) {
        long newInterval = newcheckInterval / 10L * 10L;
        if (this.checkInterval.get() != newInterval) {
            this.checkInterval.set(newInterval);
            if (newInterval <= 0L) {
                this.stop();
                this.lastTime.set(System.currentTimeMillis());
            } else {
                this.start();
            }
        }
    }

    void bytesRecvFlowControl(long recv) {
        this.currentReadBytes.addAndGet(recv);
        this.cumulativeReadBytes.addAndGet(recv);
    }

    void bytesWriteFlowControl(long write) {
        this.currentWrittenBytes.addAndGet(write);
        this.cumulativeWrittenBytes.addAndGet(write);
    }

    public long checkInterval() {
        return this.checkInterval.get();
    }

    public long lastReadThroughput() {
        return this.lastReadThroughput;
    }

    public long lastWriteThroughput() {
        return this.lastWriteThroughput;
    }

    public long lastReadBytes() {
        return this.lastReadBytes;
    }

    public long lastWrittenBytes() {
        return this.lastWrittenBytes;
    }

    public long currentReadBytes() {
        return this.currentReadBytes.get();
    }

    public long currentWrittenBytes() {
        return this.currentWrittenBytes.get();
    }

    public long lastTime() {
        return this.lastTime.get();
    }

    public long cumulativeWrittenBytes() {
        return this.cumulativeWrittenBytes.get();
    }

    public long cumulativeReadBytes() {
        return this.cumulativeReadBytes.get();
    }

    public long lastCumulativeTime() {
        return this.lastCumulativeTime;
    }

    public void resetCumulativeTime() {
        this.lastCumulativeTime = System.currentTimeMillis();
        this.cumulativeReadBytes.set(0L);
        this.cumulativeWrittenBytes.set(0L);
    }

    public String name() {
        return this.name;
    }

    public synchronized long readTimeToWait(long size, long limitTraffic, long maxTime) {
        long now = System.currentTimeMillis();
        this.bytesRecvFlowControl(size);
        if (limitTraffic == 0L) {
            return 0L;
        }
        long sum = this.currentReadBytes.get();
        long interval = now - this.lastTime.get();
        if (interval > 10L && sum > 0L) {
            long time = (sum * 1000L / limitTraffic - interval) / 10L * 10L;
            if (time > 10L) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Time: " + time + ":" + sum + ":" + interval);
                }
                return time > maxTime ? maxTime : time;
            }
            return 0L;
        }
        if (this.lastNonNullReadBytes > 0L && this.lastNonNullReadTime + 10L < now) {
            long lastsum = sum + this.lastNonNullReadBytes;
            long lastinterval = now - this.lastNonNullReadTime;
            long time = (lastsum * 1000L / limitTraffic - lastinterval) / 10L * 10L;
            if (time > 10L) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Time: " + time + ":" + lastsum + ":" + lastinterval);
                }
                return time > maxTime ? maxTime : time;
            }
        } else {
            long lastinterval = 10L;
            long time = ((sum += this.lastReadBytes) * 1000L / limitTraffic - lastinterval) / 10L * 10L;
            if (time > 10L) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Time: " + time + ":" + sum + ":" + lastinterval);
                }
                return time > maxTime ? maxTime : time;
            }
        }
        return 0L;
    }

    public synchronized long writeTimeToWait(long size, long limitTraffic, long maxTime) {
        this.bytesWriteFlowControl(size);
        if (limitTraffic == 0L) {
            return 0L;
        }
        long sum = this.currentWrittenBytes.get();
        long now = System.currentTimeMillis();
        long interval = now - this.lastTime.get();
        if (interval > 10L && sum > 0L) {
            long time = (sum * 1000L / limitTraffic - interval) / 10L * 10L;
            if (time > 10L) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Time: " + time + ":" + sum + ":" + interval);
                }
                return time > maxTime ? maxTime : time;
            }
            return 0L;
        }
        if (this.lastNonNullWrittenBytes > 0L && this.lastNonNullWrittenTime + 10L < now) {
            long lastsum = sum + this.lastNonNullWrittenBytes;
            long lastinterval = now - this.lastNonNullWrittenTime;
            long time = (lastsum * 1000L / limitTraffic - lastinterval) / 10L * 10L;
            if (time > 10L) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Time: " + time + ":" + lastsum + ":" + lastinterval);
                }
                return time > maxTime ? maxTime : time;
            }
        } else {
            long lastinterval = 10L + Math.abs(interval);
            long time = ((sum += this.lastWrittenBytes) * 1000L / limitTraffic - lastinterval) / 10L * 10L;
            if (time > 10L) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Time: " + time + ":" + sum + ":" + lastinterval);
                }
                return time > maxTime ? maxTime : time;
            }
        }
        return 0L;
    }

    public String toString() {
        return "Monitor " + this.name + " Current Speed Read: " + (this.lastReadThroughput >> 10) + " KB/s, Write: " + (this.lastWriteThroughput >> 10) + " KB/s Current Read: " + (this.currentReadBytes.get() >> 10) + " KB Current Write: " + (this.currentWrittenBytes.get() >> 10) + " KB";
    }

    private static class TrafficMonitoringTask
    implements Runnable {
        private final AbstractTrafficShapingHandler trafficShapingHandler1;
        private final TrafficCounter counter;

        protected TrafficMonitoringTask(AbstractTrafficShapingHandler trafficShapingHandler, TrafficCounter counter) {
            this.trafficShapingHandler1 = trafficShapingHandler;
            this.counter = counter;
        }

        @Override
        public void run() {
            if (!this.counter.monitorActive.get()) {
                return;
            }
            long endTime = System.currentTimeMillis();
            this.counter.resetAccounting(endTime);
            if (this.trafficShapingHandler1 != null) {
                this.trafficShapingHandler1.doAccounting(this.counter);
            }
            this.counter.scheduledFuture = this.counter.executor.schedule(this, this.counter.checkInterval.get(), TimeUnit.MILLISECONDS);
        }
    }
}

