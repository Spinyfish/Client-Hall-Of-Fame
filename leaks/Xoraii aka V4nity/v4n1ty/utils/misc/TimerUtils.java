package v4n1ty.utils.misc;

public class TimerUtils {

    public long lastMS = System.currentTimeMillis();
    private long lastTime;
    private long prevMS = 0L;

    public void reset() {
        lastMS = System.currentTimeMillis();
    }


    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }


    public boolean delay(final float milliSec) {
        return this.getTime() - this.prevMS >= milliSec;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    public long getCurrentTime() {
        return System.nanoTime() / 1000000L;
    }

    public long getLastTime() {
        return this.lastTime;
    }

    public long getDifference() {
        return this.getCurrentTime() - this.lastTime;
    }

    public boolean hasReached(final long milliseconds) {
        return this.getDifference() >= milliseconds;
    }

    public boolean hasTimeElapsed(double time) {
        return hasTimeElapsed((long) time);
    }


    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }

    public int convertToMS(int d) {
        return 1000 / d;
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public long getDelay() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public void setLastMS() {
        this.lastMS = System.currentTimeMillis();
    }

    public void setLastMS(long lastMS) {
        this.lastMS = lastMS;
    }

}