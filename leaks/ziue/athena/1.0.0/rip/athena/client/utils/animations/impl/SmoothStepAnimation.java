package rip.athena.client.utils.animations.impl;

import rip.athena.client.utils.animations.*;

public class SmoothStepAnimation extends Animation
{
    public SmoothStepAnimation(final int ms, final double endPoint) {
        super(ms, endPoint);
    }
    
    public SmoothStepAnimation(final int ms, final double endPoint, final Direction direction) {
        super(ms, endPoint, direction);
    }
    
    @Override
    protected double getEquation(final double x) {
        return -2.0 * Math.pow(x, 3.0) + 3.0 * Math.pow(x, 2.0);
    }
}
