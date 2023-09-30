package dev.tenacity.utils.animations;

import dev.tenacity.utils.animations.impl.SmoothStepAnimation;
import lombok.Getter;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class ContinualAnimation {

    private float output, endpoint;

    @Getter
    private Animation animation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);


    @Exclude(Strategy.NAME_REMAPPING)
    public void animate(float destination, int ms) {
        output = endpoint - animation.getOutput().floatValue();
        endpoint = destination;
        if (output != (endpoint - destination)) {
            animation = new SmoothStepAnimation(ms, endpoint - output, Direction.BACKWARDS);
        }
    }


    public boolean isDone() {
        return output == endpoint || animation.isDone();
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public float getOutput() {
        output = endpoint - animation.getOutput().floatValue();
        return output;
    }


}
