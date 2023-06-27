package javax.vecmath;

import java.io.*;

public class Point2f extends Tuple2f implements Serializable
{
    static final long serialVersionUID = -4801347926528714435L;
    
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
    
    public Point2f(final Tuple2d t1) {
        super(t1);
    }
    
    public Point2f(final Tuple2f t1) {
        super(t1);
    }
    
    public Point2f() {
    }
    
    public final float distanceSquared(final Point2f p1) {
        final float dx = this.x - p1.x;
        final float dy = this.y - p1.y;
        return dx * dx + dy * dy;
    }
    
    public final float distance(final Point2f p1) {
        final float dx = this.x - p1.x;
        final float dy = this.y - p1.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }
    
    public final float distanceL1(final Point2f p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y);
    }
    
    public final float distanceLinf(final Point2f p1) {
        return Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
    }
}
