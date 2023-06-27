package rip.athena.client.utils.animations;

class AnimationTimer
{
    public long lastMS;
    
    AnimationTimer() {
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
    
    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }
    
    public void setTime(final long time) {
        this.lastMS = time;
    }
}
