// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Point2d extends Tuple2d implements Serializable
{
    public Point2d(final double x, final double y) {
        super(x, y);
    }
    
    public Point2d(final double[] p) {
        super(p);
    }
    
    public Point2d(final Point2d p1) {
        super(p1);
    }
    
    public Point2d(final Point2f p1) {
        super(p1);
    }
    
    public Point2d(final Tuple2d t1) {
        super(t1);
    }
    
    public Point2d(final Tuple2f t1) {
        super(t1);
    }
    
    public Point2d() {
    }
    
    public final double distanceSquared(final Point2d p1) {
        final double dx = super.x - p1.x;
        final double dy = super.y - p1.y;
        return dx * dx + dy * dy;
    }
    
    public final double distance(final Point2d p1) {
        return Math.sqrt(this.distanceSquared(p1));
    }
    
    public final double distanceL1(final Point2d p1) {
        return Math.abs(super.x - p1.x) + Math.abs(super.y - p1.y);
    }
    
    public final double distanceLinf(final Point2d p1) {
        return Math.max(Math.abs(super.x - p1.x), Math.abs(super.y - p1.y));
    }
}
