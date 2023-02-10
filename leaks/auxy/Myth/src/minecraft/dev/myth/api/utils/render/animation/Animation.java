package dev.myth.api.utils.render.animation;

public class Animation {

    private long animationStart;
    private double duration;
    private double animationFromValue;
    private double animationToValue;
    private Easing easing = Easings.NONE;
    private double lastValue;

    public double getValue() {
        return this.lastValue;
    }

    public Animation setValue(double value) {
        this.animationFromValue = value;
        this.animationToValue = value;
        this.lastValue = value;
        return this;
    }

    public void animate(double value, double duration, Easing easing) {
        this.animationFromValue = this.lastValue;
        this.animationToValue = value;
        this.animationStart = System.currentTimeMillis();
        this.duration = duration;
        this.easing = easing;
    }

    public boolean updateAnimation() {
        double part = (double) (System.currentTimeMillis() - animationStart) / duration;
        double value;
        if(isAlive()) {
            part = this.easing.ease(part);
            value = animationFromValue + (animationToValue - animationFromValue) * part;
        } else {
            this.animationStart = 0;
            value = this.animationToValue;
        }
        this.lastValue = value;
        return isAlive();
    }

    public boolean isDone() {
        double part = (double) (System.currentTimeMillis() - animationStart) / duration;
        return part >= 1.0;
    }

    public boolean isAlive() {
        double part = (double) (System.currentTimeMillis() - animationStart) / duration;
        return part < 1.0;
    }

}
