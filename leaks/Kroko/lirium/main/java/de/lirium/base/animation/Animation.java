package de.lirium.base.animation;

public class Animation {
    private long animationStart;
    private float duration;
    private float animationFromValue;
    private float animationToValue;
    public float lastValue;
    private Easing easing;

    public Animation() {
        this.easing = Easings.NONE;
    }

    public void animate(double value, double duration, boolean safe) {
        this.animate(value, duration, Easings.NONE, safe);
    }

    public void animate(double value, double duration, Easing easing) {
        this.animate(value, duration, easing, false);
    }

    public void animate(double value, double duration, Easing easing, boolean safe) {
        if (!safe || !this.isAlive()) {
            this.setValue(this.getValue());
            this.setAnimationToValue((float) value);
            this.setAnimationStart(System.currentTimeMillis());
            this.setDuration((float) (duration * 1000.0D));
            this.setEasing(easing);
        }
    }

    public boolean update() {
        float part = (float) ((double)(System.currentTimeMillis() - this.animationStart) / this.duration);
        float value;
        if (this.isAlive()) {
            part = (float) this.easing.ease(part);
            value = this.animationFromValue + (this.animationToValue - this.animationFromValue) * part;
        } else {
            this.animationStart = 0L;
            value = this.animationToValue;
        }

        this.lastValue = value;
        return this.isAlive();
    }

    public boolean isDone() {
        return !this.isAlive();
    }

    public boolean isAlive() {
        double part = (double)(System.currentTimeMillis() - this.animationStart) / this.duration;
        return part < 1.0D;
    }

    public double getAnimationFromValue() {
        return this.animationFromValue;
    }

    public double getAnimationToValue() {
        return this.animationToValue;
    }

    public double getDuration() {
        return this.duration;
    }

    public Easing getEasing() {
        return this.easing;
    }

    public long getAnimationStart() {
        return this.animationStart;
    }

    public float getValue() {
        return this.lastValue;
    }

    public void setAnimationFromValue(float animationFromValue) {
        this.animationFromValue = animationFromValue;
    }

    public void setAnimationToValue(float animationToValue) {
        this.animationToValue = animationToValue;
    }

    public void setValue(float value) {
        this.setAnimationFromValue(value);
        this.setAnimationToValue(value);
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setAnimationStart(long animationStart) {
        this.animationStart = animationStart;
    }

    public void setEasing(Easing easing) {
        this.easing = easing;
    }
}