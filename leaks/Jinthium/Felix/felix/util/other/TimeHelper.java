package felix.util.other;

public class TimeHelper {
    public long lastMs;
    private long prevMS;

    public TimeHelper() {
        this.lastMs = 0L;
    }

    public boolean isDelayComplete(final long delay) {
        //Runtime.getRuntime().exit(0);
        return System.currentTimeMillis() - this.lastMs > delay;
    }

    public boolean isDelayComplete(final double delay) {
        //Runtime.getRuntime().exit(0);
        return System.currentTimeMillis() - this.lastMs > delay;
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public void reset() {
        //Runtime.getRuntime().exit(0);
        this.lastMs = System.currentTimeMillis();
    }

    public long getLastMs() {
        return this.lastMs;
    }

    public void setLastMs(final int i) {
        this.lastMs = System.currentTimeMillis() + i;
    }

    public boolean reach(final long milliseconds) {
        return System.currentTimeMillis() - this.lastMs >= milliseconds;
    }

    public boolean reach(final double milliseconds) {
        return System.currentTimeMillis() - this.lastMs >= milliseconds;
    }

    public boolean delay(float milliSec) {
        //Runtime.getRuntime().exit(0);
        return (float) (getTime() - this.prevMS) >= milliSec;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public long getDifference() {
        return getTime() - this.prevMS;
    }

    public boolean check(float milliseconds) {
        return getTime() >= milliseconds;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
