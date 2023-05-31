package de.lirium.base.helper;

public class TimeHelper {
    long currentMS;

    public boolean hasReached(long ms) {
        return hasReached(ms, true);
    }

    public boolean hasReached(long ms, boolean reset) {
        final boolean reached = System.currentTimeMillis() - currentMS >= ms;
        if(reached && reset)
            reset();
        return reached;
    }

    public void reset() {
        currentMS = System.currentTimeMillis();
    }
}
