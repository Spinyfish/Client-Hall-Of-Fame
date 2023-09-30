package dev.tenacity.utils.time;

import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class TimerUtil {

    public long lastMS = System.currentTimeMillis();

    @Exclude(Strategy.NAME_REMAPPING)
    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean hasTimeElapsed(double time) {
        return hasTimeElapsed((long) time);
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }

}
