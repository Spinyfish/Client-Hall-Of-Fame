package me.kansio.client.utils.math;

import me.kansio.client.utils.Util;

public final class Stopwatch extends Util {

    private long currentTime;

    public Stopwatch() {
        setCurrentTime(getCurrentTime());
    }

    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }

    public void resetTime() {
        setCurrentTime(getCurrentTime());
    }

    public long getStartTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public boolean timeElapsed(long milliseconds) {
        return getCurrentTime() - getStartTime() >= milliseconds;
    }

    public long getTimeRemaining(long milliseconds) {
        long currentTime = getCurrentTime(),
                startTime = getStartTime();
        return currentTime - startTime >= milliseconds ? 0 : currentTime - startTime;
    }
}