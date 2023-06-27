package javax.vecmath;

import java.io.*;

public class Point3d extends Tuple3d implements Serializable
{
    static final long serialVersionUID = 5718062286069042927L;
    
    public Point3d(final double x, final double y, final double z) {
        super(x, y, z);
    }
    
    public Point3d(final double[] p) {
        super(p);
    }
    
    public Point3d(final Point3d p1) {
        super(p1);
    }
    
    public Point3d(final Point3f p1) {
        super(p1);
    }
    
    public Point3d(final Tuple3f t1) {
        super(t1);
    }
    
    public Point3d(final Tuple3d t1) {
        super(t1);
    }
    
    public Point3d() {
    }
    
    public final double distanceSquared(final Point3d p1) {
        final double dx = this.x - p1.x;
        final double dy = this.y - p1.y;
        final double dz = this.z - p1.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public final double distance(final Point3d p1) {
        final double dx = this.x - p1.x;
        final double dy = this.y - p1.y;
        final double dz = this.z - p1.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public final double distanceL1(final Point3d p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y) + Math.abs(this.z - p1.z);
    }
    
    public final double distanceLinf(final Point3d p1) {
        final double tmp = Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
        return Math.max(tmp, Math.abs(this.z - p1.z));
    }
    
    public final void project(final Point4d p1) {
        final double oneOw = 1.0 / p1.w;
        this.x = p1.x * oneOw;
        this.y = p1.y * oneOw;
        this.z = p1.z * oneOw;
    }
}
