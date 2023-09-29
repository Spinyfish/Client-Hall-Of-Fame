package wtf.automn.utils.math;


public class TimeUtil {
    private long lastMS = 0L;

    public long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public boolean hasReached(final long milliseconds) {
        return this.getCurrentMS() - this.lastMS >= milliseconds;
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public int convertToMS(final int perSecond) {
        return 1000 / perSecond;
    }

    public void setCurrentDifference(final int difference) {
        this.lastMS = System.currentTimeMillis() - difference;
    }

    public boolean hasTimePassed(final long delay) {
        return (System.currentTimeMillis() >= this.lastMS + delay);
    }

    public void setLastMS() {
        this.lastMS = System.currentTimeMillis();
    }

}
