package me.jinthium.straight.impl.utils;

import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.SmoothStepAnimation;
import org.lwjglx.input.Mouse;

public class Scroll {


    private float maxScroll = Float.MAX_VALUE, minScroll = 0, rawScroll = 0;
    private float scroll;

    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public void onScroll(int ms) {
        scroll = rawScroll - scrollAnimation.getOutput().floatValue();
        rawScroll += Mouse.getEventDWheel() / 4f;
        rawScroll = Math.max(Math.min(minScroll, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    public boolean isScrollAnimationDone() {
        return scrollAnimation.isDone();
    }

    public float getScroll() {
        scroll = rawScroll - scrollAnimation.getOutput().floatValue();
        return scroll;
    }

    public float getMaxScroll() {
        return maxScroll;
    }

    public void setMaxScroll(float maxScroll) {
        this.maxScroll = maxScroll;
    }

    public float getMinScroll() {
        return minScroll;
    }

    public void setMinScroll(float minScroll) {
        this.minScroll = minScroll;
    }

    public float getRawScroll() {
        return rawScroll;
    }

    public void setRawScroll(float rawScroll) {
        this.rawScroll = rawScroll;
    }

    public Animation getScrollAnimation() {
        return scrollAnimation;
    }

    public void setScrollAnimation(Animation scrollAnimation) {
        this.scrollAnimation = scrollAnimation;
    }
}