package byron.Mono.utils;

public class TimeUtil {

    private long lastTime = getCurrentTime();
    public TimeUtil()
    {
        reset();
    }

    public long getCurrentTime()
    {
        return System.nanoTime() / 1000000;
    }

    public long getLastTime() {
        return lastTime;
    }

    public long getDifference() {
        return getCurrentTime() - lastTime;
    }

    public void reset() {
        lastTime = getCurrentTime();
    }

    public boolean hasTimePassed(long milliseconds) {
        return getDifference() >= milliseconds;
    }
}
