package rip.athena.client.utils.animations.impl;

import rip.athena.client.utils.animations.*;

public class ElasticAnimation extends Animation
{
    float easeAmount;
    float smooth;
    boolean reallyElastic;
    
    public ElasticAnimation(final int ms, final double endPoint, final float elasticity, final float smooth, final boolean moreElasticity) {
        super(ms, endPoint);
        this.easeAmount = elasticity;
        this.smooth = smooth;
        this.reallyElastic = moreElasticity;
    }
    
    public ElasticAnimation(final int ms, final double endPoint, final float elasticity, final float smooth, final boolean moreElasticity, final Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = elasticity;
        this.smooth = smooth;
        this.reallyElastic = moreElasticity;
    }
    
    @Override
    protected double getEquation(double x) {
        x = Math.pow(x, this.smooth);
        final double elasticity = this.easeAmount * 0.1f;
        return Math.pow(2.0, -10.0 * (this.reallyElastic ? Math.sqrt(x) : x)) * Math.sin((x - elasticity / 4.0) * (6.283185307179586 / elasticity)) + 1.0;
    }
}
