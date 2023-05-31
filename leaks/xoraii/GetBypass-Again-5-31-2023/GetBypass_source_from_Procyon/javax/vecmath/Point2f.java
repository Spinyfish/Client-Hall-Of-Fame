// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Point2f extends Tuple2f implements Serializable
{
    public Point2f(final float x, final float y) {
        super(x, y);
    }
    
    public Point2f(final float[] p) {
        super(p);
    }
    
    public Point2f(final Point2f p1) {
        super(p1);
    }
    
    public Point2f(final Point2d p1) {
        super(p1);
    }
    
    public Point2f(final Tuple2f t1) {
        super(t1);
    }
    
    public Point2f(final Tuple2d t1) {
        super(t1);
    }
    
    public Point2f() {
    }
    
    public final float distanceSquared(final Point2f p1) {
        final double dx = super.x - p1.x;
        final double dy = super.y - p1.y;
        return (float)(dx * dx + dy * dy);
    }
    
    public final float distance(final Point2f p1) {
        return (float)Math.sqrt(this.distanceSquared(p1));
    }
    
    public final float distanceL1(final Point2f p1) {
        return Math.abs(super.x - p1.x) + Math.abs(super.y - p1.y);
    }
    
    public final float distanceLinf(final Point2f p1) {
        return Math.max(Math.abs(super.x - p1.x), Math.abs(super.y - p1.y));
    }
}
