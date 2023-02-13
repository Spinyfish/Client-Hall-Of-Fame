package juul.util.animations.impl;

import juul.util.animations.Animation;
import juul.util.animations.Direction;

public class EaseOutCirc extends Animation {

    public EaseOutCirc(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseOutCirc(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected boolean correctOutput() {
        return true;
    }

    protected double getEquation(double x) {
        double x1 = x / duration;
        return Math.sqrt(1 - Math.pow(x1 - 1, 2));
    }
}
