// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Point4f extends Tuple4f implements Serializable
{
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
    
    public Point4f(final Tuple4d t1) {
        super(t1);
    }
    
    public Point4f(final Tuple4f t1) {
        super(t1);
    }
    
    public Point4f() {
    }
    
    public Point4f(final Tuple3f t1) {
        super(t1.x, t1.y, t1.z, 1.0f);
    }
    
    public final void set(final Tuple3f t1) {
        this.set(t1.x, t1.y, t1.z, 1.0f);
    }
    
    public final float distanceSquared(final Point4f p1) {
        final double dx = super.x - p1.x;
        final double dy = super.y - p1.y;
        final double dz = super.z - p1.z;
        final double dw = super.w - p1.w;
        return (float)(dx * dx + dy * dy + dz * dz + dw * dw);
    }
    
    public final float distance(final Point4f p1) {
        return (float)Math.sqrt(this.distanceSquared(p1));
    }
    
    public final float distanceL1(final Point4f p1) {
        return Math.abs(super.x - p1.x) + Math.abs(super.y - p1.y) + Math.abs(super.z - p1.z) + Math.abs(super.w - p1.w);
    }
    
    public final float distanceLinf(final Point4f p1) {
        return Math.max(Math.max(Math.abs(super.x - p1.x), Math.abs(super.y - p1.y)), Math.max(Math.abs(super.z - p1.z), Math.abs(super.w - p1.w)));
    }
    
    public final void project(final Point4f p1) {
        super.x = p1.x / p1.w;
        super.y = p1.y / p1.w;
        super.z = p1.z / p1.w;
        super.w = 1.0f;
    }
}
