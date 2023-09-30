package club.pulsive.impl.util.client;

public final class TimerUtil {

    private long currentMS = System.currentTimeMillis();

    public long lastReset() {
        return currentMS;
    }

    public boolean hasElapsed(long milliseconds) {
        return elapsed() > milliseconds;
    }

    public long elapsed() {
        return System.currentTimeMillis() - currentMS;
    }

    public void reset() {
        currentMS = System.currentTimeMillis();
    }

    public void setCurrentMS(long currentMS) {
        this.currentMS = currentMS;
    }
}
