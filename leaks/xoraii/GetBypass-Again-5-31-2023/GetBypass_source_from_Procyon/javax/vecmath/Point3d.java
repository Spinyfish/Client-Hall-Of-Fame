// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Point3d extends Tuple3d implements Serializable
{
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
    
    public Point3d(final Tuple3d t1) {
        super(t1);
    }
    
    public Point3d(final Tuple3f t1) {
        super(t1);
    }
    
    public Point3d() {
    }
    
    public final double distanceSquared(final Point3d p1) {
        final double dx = super.x - p1.x;
        final double dy = super.y - p1.y;
        final double dz = super.z - p1.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public final double distance(final Point3d p1) {
        return Math.sqrt(this.distanceSquared(p1));
    }
    
    public final double distanceL1(final Point3d p1) {
        return Math.abs(super.x - p1.x) + Math.abs(super.y - p1.y) + Math.abs(super.z - p1.z);
    }
    
    public final double distanceLinf(final Point3d p1) {
        return Math.max(Math.max(Math.abs(super.x - p1.x), Math.abs(super.y - p1.y)), Math.abs(super.z - p1.z));
    }
    
    public final void project(final Point4d p1) {
        super.x = p1.x / p1.w;
        super.y = p1.y / p1.w;
        super.z = p1.z / p1.w;
    }
}
