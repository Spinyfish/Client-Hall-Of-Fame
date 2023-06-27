package javax.vecmath;

import java.io.*;

public class Point4f extends Tuple4f implements Serializable
{
    static final long serialVersionUID = 4643134103185764459L;
    
    public Point4f(final float x, final float y, final float z, final float w) {
        super(x, y, z, w);
    }
    
    public Point4f(final float[] p) {
        super(p);
    }
    
    public Point4f(final Point4f p1) {
        super(p1);
    }
    
    public Point4f(final Point4d p1) {
        super(p1);
    }
    
    public Point4f(final Tuple4f t1) {
        super(t1);
    }
    
    public Point4f(final Tuple4d t1) {
        super(t1);
    }
    
    public Point4f(final Tuple3f t1) {
        super(t1.x, t1.y, t1.z, 1.0f);
    }
    
    public Point4f() {
    }
    
    public final void set(final Tuple3f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = 1.0f;
    }
    
    public final float distanceSquared(final Point4f p1) {
        final float dx = this.x - p1.x;
        final float dy = this.y - p1.y;
        final float dz = this.z - p1.z;
        final float dw = this.w - p1.w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }
    
    public final float distance(final Point4f p1) {
        final float dx = this.x - p1.x;
        final float dy = this.y - p1.y;
        final float dz = this.z - p1.z;
        final float dw = this.w - p1.w;
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }
    
    public final float distanceL1(final Point4f p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y) + Math.abs(this.z - p1.z) + Math.abs(this.w - p1.w);
    }
    
    public final float distanceLinf(final Point4f p1) {
        final float t1 = Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
        final float t2 = Math.max(Math.abs(this.z - p1.z), Math.abs(this.w - p1.w));
        return Math.max(t1, t2);
    }
    
    public final void project(final Point4f p1) {
        final float oneOw = 1.0f / p1.w;
        this.x = p1.x * oneOw;
        this.y = p1.y * oneOw;
        this.z = p1.z * oneOw;
        this.w = 1.0f;
    }
}
