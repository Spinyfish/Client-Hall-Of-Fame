package javax.vecmath;

import java.io.*;

public class Point3f extends Tuple3f implements Serializable
{
    static final long serialVersionUID = -8689337816398030143L;
    
    public Point3f(final float x, final float y, final float z) {
        super(x, y, z);
    }
    
    public Point3f(final float[] p) {
        super(p);
    }
    
    public Point3f(final Point3f p1) {
        super(p1);
    }
    
    public Point3f(final Point3d p1) {
        super(p1);
    }
    
    public Point3f(final Tuple3f t1) {
        super(t1);
    }
    
    public Point3f(final Tuple3d t1) {
        super(t1);
    }
    
    public Point3f() {
    }
    
    public final float distanceSquared(final Point3f p1) {
        final float dx = this.x - p1.x;
        final float dy = this.y - p1.y;
        final float dz = this.z - p1.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public final float distance(final Point3f p1) {
        final float dx = this.x - p1.x;
        final float dy = this.y - p1.y;
        final float dz = this.z - p1.z;
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public final float distanceL1(final Point3f p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y) + Math.abs(this.z - p1.z);
    }
    
    public final float distanceLinf(final Point3f p1) {
        final float tmp = Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
        return Math.max(tmp, Math.abs(this.z - p1.z));
    }
    
    public final void project(final Point4f p1) {
        final float oneOw = 1.0f / p1.w;
        this.x = p1.x * oneOw;
        this.y = p1.y * oneOw;
        this.z = p1.z * oneOw;
    }
}
