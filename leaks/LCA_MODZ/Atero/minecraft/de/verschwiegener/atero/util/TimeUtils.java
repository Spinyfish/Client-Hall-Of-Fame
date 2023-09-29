package de.verschwiegener.atero.util;

public class TimeUtils {

    private long lastMS = 0L;
    private long prevMS;

    public TimeUtils() {
	this.prevMS = 0L;
    }

    public boolean isDelayComplete(long delay) {
	if (getCurrentMS() - lastMS >= delay) {
	    return true;
	}

	return false;
    }

    public long getCurrentMS() {
	return System.nanoTime() / 1000000L;
    }

    public void setLastMS(long lastMS) {
	this.lastMS = lastMS;
    }

    public void setLastMS() {
	lastMS = System.currentTimeMillis();
    }

    public int convertToMS(int d) {
	return 1000 / d;
    }

    public  boolean hasReached(float f) {
	return (float) (getCurrentMS() - lastMS) >= f;
    }

    public void reset() {
	lastMS = getCurrentMS();
    }

    public boolean delay(float milliSec) {
	return (float) (getTime() - this.prevMS) >= milliSec;
    }

    private long getTime() {
	return System.nanoTime() / 1000000L;
    }
}
