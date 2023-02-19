package alos.stella.utils.timer;

import net.minecraft.util.MathHelper;

public final class MSTimer {

    public long time = -1L;

    private long lastMS;

    public boolean hasTimePassed(final long MS) {
        return System.currentTimeMillis() >= time + MS;
    }

    public long hasTimeLeft(final long MS) {
        return (MS + time) - System.currentTimeMillis();
    }

    public void reset() {
        time = System.currentTimeMillis();
    }

    public boolean delay(double milliseconds) {
        return MathHelper.clamp_float(getCurrentMS() - lastMS, 0, (float) milliseconds) >= milliseconds;
    }
    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

}
