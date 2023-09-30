package dev.africa.pandaware.utils.client;

public class Timer {
    private long lastTime;

    public void Timer() {
        reset();
    }

    public void reset() {
        this.lastTime = System.currentTimeMillis();
    }

    public boolean hasReached(double milliseconds) {
        return ((System.currentTimeMillis() - lastTime) >= milliseconds);
    }

    public long getLastTime() {
        return lastTime;
    }
}
