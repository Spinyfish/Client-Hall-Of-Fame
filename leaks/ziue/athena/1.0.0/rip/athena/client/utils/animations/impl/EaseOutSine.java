package rip.athena.client.utils.animations.impl;

import rip.athena.client.utils.animations.*;

public class EaseOutSine extends Animation
{
    public EaseOutSine(final int ms, final double endPoint) {
        super(ms, endPoint);
    }
    
    public EaseOutSine(final int ms, final double endPoint, final Direction direction) {
        super(ms, endPoint, direction);
    }
    
    @Override
    protected boolean correctOutput() {
        return true;
    }
    
    @Override
    protected double getEquation(final double x) {
        return Math.sin(x * 1.5707963267948966);
    }
}
