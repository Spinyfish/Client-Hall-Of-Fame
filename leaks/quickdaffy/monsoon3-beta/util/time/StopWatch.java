/*
 * Decompiled with CFR 0.152.
 */
package util.time;

public class StopWatch {
    private long millis;

    public StopWatch() {
        this.reset();
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= this.millis;
    }

    public void reset() {
        this.millis = System.currentTimeMillis();
    }

    public long getMillis() {
        return this.millis;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }
}

