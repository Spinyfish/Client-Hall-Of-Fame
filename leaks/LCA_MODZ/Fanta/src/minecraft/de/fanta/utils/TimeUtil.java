package de.fanta.utils;

public class TimeUtil {
    private long lastMS = 0L;

    public long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public boolean hasReached(long milliseconds) {
        return getCurrentMS() - lastMS >= milliseconds;
    }

    public void reset() {
        lastMS = getCurrentMS();
    }

    public int convertToMS(int perSecond) {
        return 1000 / perSecond;
    }

    public void setCurrentDifference(int difference) {
    	this.lastMS = System.currentTimeMillis()-difference;
    }
    
    public boolean hasTimePassed(long delay) {
        return (System.currentTimeMillis() >= lastMS + delay);
    }

    public void setLastMS() {
        lastMS = System.currentTimeMillis();
    }

}
