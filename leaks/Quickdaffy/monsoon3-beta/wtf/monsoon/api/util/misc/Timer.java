/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.misc;

import wtf.monsoon.api.util.Util;

public class Timer
extends Util {
    public long lastMS = System.currentTimeMillis();

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public void setTime(long time) {
        this.lastMS = time;
    }

    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }

    public boolean hasTimeElapsed(double time, boolean reset) {
        return this.hasTimeElapsed((long)time, reset);
    }
}

