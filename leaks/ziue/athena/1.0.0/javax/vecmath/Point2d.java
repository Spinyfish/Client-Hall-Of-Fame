package javax.vecmath;

import java.io.*;

public class Point2d extends Tuple2d implements Serializable
{
    static final long serialVersionUID = 1133748791492571954L;
    
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
        final double dx = this.x - p1.x;
        final double dy = this.y - p1.y;
        return dx * dx + dy * dy;
    }
    
    public final double distance(final Point2d p1) {
        final double dx = this.x - p1.x;
        final double dy = this.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public final double distanceL1(final Point2d p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y);
    }
    
    public final double distanceLinf(final Point2d p1) {
        return Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
    }
}
