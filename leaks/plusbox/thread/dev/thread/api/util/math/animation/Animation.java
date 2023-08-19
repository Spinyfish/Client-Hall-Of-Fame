package dev.thread.api.util.math.animation;

/**
 * @author aesthetical, plus
 * @since 03/26/23
 */
public class Animation {
    private final Easing easing;
    private boolean state;
    private final double animationTime;
    private long time;

    public Animation(Easing easing, double animationTime, boolean initialState) {
        this.easing = easing;
        this.animationTime = animationTime;
        setState(initialState);
    }

    /**
     * Gets the factor of this animation
     * @return the factor based on time and speed
     */
    public double getFactor() {
        double linear = (System.currentTimeMillis() - time) / animationTime;
        if (!state) {
            linear = 1.0 - linear;
        }

        return Math.min(Math.max(easing.ease(linear), 0.0), 1.0);
    }

    public Animation setState(boolean state) {
        this.state = state;
        time = System.currentTimeMillis();
        return this;
    }

    public boolean getState() {
        return state;
    }
}
