package store.femboy.spring.impl.util;

public final class TimeUtil {

    private long lastMS = System.currentTimeMillis();

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(double delay) {
        return System.currentTimeMillis() - this.lastMS >= delay;
    }

    public boolean hasTimeElapsed(long delay) {
        return System.currentTimeMillis() - this.lastMS >= delay;
    }

    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

}
