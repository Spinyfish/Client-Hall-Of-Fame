/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.storage.utils.other;

public class DelayTimer {
    private long prevTime;

    public DelayTimer() {
        this.reset();
    }

    public DelayTimer(long def) {
        this.prevTime = System.currentTimeMillis() - def;
    }

    public boolean hasPassed(double milli) {
        return (double)(System.currentTimeMillis() - this.prevTime) >= milli;
    }

    public void reset() {
        this.prevTime = System.currentTimeMillis();
    }

    public long getPassed() {
        return System.currentTimeMillis() - this.prevTime;
    }

    public void reset(long def) {
        this.prevTime = System.currentTimeMillis() - def;
    }

    public boolean isDelayComplete(double d) {
        return this.hasPassed(d);
    }
}
