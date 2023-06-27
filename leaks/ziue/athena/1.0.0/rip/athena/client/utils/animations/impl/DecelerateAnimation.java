package rip.athena.client.utils.animations.impl;

import rip.athena.client.utils.animations.*;

public class DecelerateAnimation extends Animation
{
    public DecelerateAnimation(final int ms, final double endPoint) {
        super(ms, endPoint);
    }
    
    public DecelerateAnimation(final int ms, final double endPoint, final Direction direction) {
        super(ms, endPoint, direction);
    }
    
    @Override
    protected double getEquation(final double x) {
        return 1.0 - (x - 1.0) * (x - 1.0);
    }
}
