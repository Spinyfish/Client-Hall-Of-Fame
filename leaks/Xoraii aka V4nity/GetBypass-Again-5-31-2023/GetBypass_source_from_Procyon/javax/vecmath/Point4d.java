// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Point4d extends Tuple4d implements Serializable
{
    public Point4d(final double x, final double y, final double z, final double w) {
        super(x, y, z, w);
    }
    
    public Point4d(final double[] p) {
        super(p);
    }
    
    public Point4d(final Point4f p1) {
        super(p1);
    }
    
    public Point4d(final Point4d p1) {
        super(p1);
    }
    
    public Point4d(final Tuple4d t1) {
        super(t1);
    }
    
    public Point4d(final Tuple4f t1) {
        super(t1);
    }
    
    public Point4d() {
    }
    
    public Point4d(final Tuple3d t1) {
        super(t1.x, t1.y, t1.z, 1.0);
    }
    
    public final void set(final Tuple3d t1) {
        this.set(t1.x, t1.y, t1.z, 1.0);
    }
    
    public final double distanceSquared(final Point4d p1) {
        final double dx = super.x - p1.x;
        final double dy = super.y - p1.y;
        final double dz = super.z - p1.z;
        final double dw = super.w - p1.w;
        return (float)(dx * dx + dy * dy + dz * dz + dw * dw);
    }
    
    public final double distance(final Point4d p1) {
        return Math.sqrt(this.distanceSquared(p1));
    }
    
    public final double distanceL1(final Point4d p1) {
        return Math.abs(super.x - p1.x) + Math.abs(super.y - p1.y) + Math.abs(super.z - p1.z) + Math.abs(super.w - p1.w);
    }
    
    public final double distanceLinf(final Point4d p1) {
        return Math.max(Math.max(Math.abs(super.x - p1.x), Math.abs(super.y - p1.y)), Math.max(Math.abs(super.z - p1.z), Math.abs(super.w - p1.w)));
    }
    
    public final void project(final Point4d p1) {
        super.x = p1.x / p1.w;
        super.y = p1.y / p1.w;
        super.z = p1.z / p1.w;
        super.w = 1.0;
    }
}
