package club.pulsive.impl.util.render.animations;

import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;

public class SmoothStep extends Animation {

    public SmoothStep(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothStep(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        double x1 = x / (double) duration;
        return -2 * ApacheMath.pow(x1, 3) + (3 * ApacheMath.pow(x1, 2));
    }

}
