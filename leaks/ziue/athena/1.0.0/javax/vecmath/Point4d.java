package javax.vecmath;

import java.io.*;

public class Point4d extends Tuple4d implements Serializable
{
    static final long serialVersionUID = 1733471895962736949L;
    
    public Point4d(final double x, final double y, final double z, final double w) {
        super(x, y, z, w);
    }
    
    public Point4d(final double[] p) {
        super(p);
    }
    
    public Point4d(final Point4d p1) {
        super(p1);
    }
    
    public Point4d(final Point4f p1) {
        super(p1);
    }
    
    public Point4d(final Tuple4f t1) {
        super(t1);
    }
    
    public Point4d(final Tuple4d t1) {
        super(t1);
    }
    
    public Point4d(final Tuple3d t1) {
        super(t1.x, t1.y, t1.z, 1.0);
    }
    
    public Point4d() {
    }
    
    public final void set(final Tuple3d t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = 1.0;
    }
    
    public final double distanceSquared(final Point4d p1) {
        final double dx = this.x - p1.x;
        final double dy = this.y - p1.y;
        final double dz = this.z - p1.z;
        final double dw = this.w - p1.w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }
    
    public final double distance(final Point4d p1) {
        final double dx = this.x - p1.x;
        final double dy = this.y - p1.y;
        final double dz = this.z - p1.z;
        final double dw = this.w - p1.w;
        return Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }
    
    public final double distanceL1(final Point4d p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y) + Math.abs(this.z - p1.z) + Math.abs(this.w - p1.w);
    }
    
    public final double distanceLinf(final Point4d p1) {
        final double t1 = Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
        final double t2 = Math.max(Math.abs(this.z - p1.z), Math.abs(this.w - p1.w));
        return Math.max(t1, t2);
    }
    
    public final void project(final Point4d p1) {
        final double oneOw = 1.0 / p1.w;
        this.x = p1.x * oneOw;
        this.y = p1.y * oneOw;
        this.z = p1.z * oneOw;
        this.w = 1.0;
    }
}
