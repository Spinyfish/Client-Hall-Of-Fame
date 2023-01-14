package cafe.corrosion.menu.animation;

public abstract class Animation {
    protected final long time;
    protected long startTime = -1L;
    protected boolean inverted;

    public Animation(long time) {
        this.time = time;
    }

    public double getProgression() {
        long timeElapsed = System.currentTimeMillis() - this.startTime;
        return (double)timeElapsed / (double)this.time;
    }

    public void start(boolean inverted) {
        this.inverted = inverted;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isComplete() {
        return System.currentTimeMillis() - this.startTime > this.time;
    }

    public boolean isAnimating() {
        return this.startTime != -1L && !this.isComplete();
    }

    public boolean isInverted() {
        return this.inverted;
    }

    public abstract double calculate();

    public long getTime() {
        return this.time;
    }

    public long getStartTime() {
        return this.startTime;
    }
}
