package rip.athena.client.utils.time;

public class TimerUtil
{
    public long lastMS;
    
    public TimerUtil() {
        this.lastMS = System.currentTimeMillis();
    }
    
    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }
    
    public boolean hasTimeElapsed(final long time, final boolean reset) {
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }
    
    public boolean hasTimeElapsed(final long time) {
        return System.currentTimeMillis() - this.lastMS > time;
    }
    
    public boolean hasTimeElapsed(final double time) {
        return this.hasTimeElapsed((long)time);
    }
    
    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }
    
    public void setTime(final long time) {
        this.lastMS = time;
    }
}
