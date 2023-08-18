package me.jinthium.straight.impl.utils.animation;
import me.jinthium.straight.impl.utils.animation.impl.SmoothStepAnimation;

public class ContinualAnimation {

    private float output, endpoint;


    private Animation animation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public Animation getAnimation() {
        return animation;
    }

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

    public float getOutput() {
        output = endpoint - animation.getOutput().floatValue();
        return output;
    }


}