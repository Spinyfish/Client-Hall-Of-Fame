/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.storage.utils.other;

public class TimeHelper {
    public long lastMs = 0L;

    public void reset() {
        this.lastMs = System.currentTimeMillis();
    }

    public boolean delay(long nextDelay) {
        return System.currentTimeMillis() - this.lastMs >= nextDelay;
    }

    public boolean delay(float nextDelay, boolean reset) {
        if (!((float)(System.currentTimeMillis() - this.lastMs) >= nextDelay)) return false;
        if (!reset) return true;
        this.reset();
        return true;
    }

    public boolean isDelayComplete(double valueState) {
        return (double)(System.currentTimeMillis() - this.lastMs) >= valueState;
    }

    public void setElapsedTime(long time) {
        this.lastMs = time;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.lastMs;
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public long getLastMs() {
        return this.lastMs;
    }
}
