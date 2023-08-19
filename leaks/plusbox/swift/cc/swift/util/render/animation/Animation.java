/**
 * @project Fish-Client
 * @author CodeMan
 * @at 10.02.23, 20:49
 */
package cc.swift.util.render.animation;

public class Animation {
    private long animationStart;
    private double duration;
    private double animationFromValue;
    private double animationToValue;
    private Easings.Easing easing = Easings.NONE;
    private double lastValue;

    public double getValue() {
        return this.lastValue;
    }

    public void setValue(double value) {
        this.animationFromValue = value;
        this.animationToValue = value;
    }

    public void animate(double value, double duration, Easings.Easing easing) {
        this.animationFromValue = this.lastValue;
        this.animationToValue = value;
        this.animationStart = System.currentTimeMillis();
        this.duration = duration;
        this.easing = easing;
    }

    public boolean updateAnimation() {
        double part = (double) (System.currentTimeMillis() - animationStart) / duration;
        double value;
        if (isAlive()) {
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
        return (System.currentTimeMillis() - animationStart) / duration >= 1.0;
    }

    public boolean isAlive() {
        return (System.currentTimeMillis() - animationStart) / duration < 1.0;
    }

}
