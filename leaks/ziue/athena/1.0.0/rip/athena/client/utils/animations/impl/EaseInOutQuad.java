package rip.athena.client.utils.animations.impl;

import rip.athena.client.utils.animations.*;

public class EaseInOutQuad extends Animation
{
    public EaseInOutQuad(final int ms, final double endPoint) {
        super(ms, endPoint);
    }
    
    public EaseInOutQuad(final int ms, final double endPoint, final Direction direction) {
        super(ms, endPoint, direction);
    }
    
    @Override
    protected double getEquation(final double x) {
        return (x < 0.5) ? (2.0 * Math.pow(x, 2.0)) : (1.0 - Math.pow(-2.0 * x + 2.0, 2.0) / 2.0);
    }
}
