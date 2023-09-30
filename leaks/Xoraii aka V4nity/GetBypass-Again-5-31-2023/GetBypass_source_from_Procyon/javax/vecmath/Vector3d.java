// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Vector3d extends Tuple3d implements Serializable
{
    public Vector3d(final double x, final double y, final double z) {
        super(x, y, z);
    }
    
    public Vector3d(final double[] v) {
        super(v);
    }
    
    public Vector3d(final Vector3f v1) {
        super(v1);
    }
    
    public Vector3d(final Vector3d v1) {
        super(v1);
    }
    
    public Vector3d(final Tuple3d t1) {
        super(t1);
    }
    
    public Vector3d(final Tuple3f t1) {
        super(t1);
    }
    
    public Vector3d() {
    }
    
    public final void cross(final Vector3d v1, final Vector3d v2) {
        this.set(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }
    
    public final void normalize(final Vector3d v1) {
        this.set(v1);
        this.normalize();
    }
    
    public final void normalize() {
        final double d = this.length();
        super.x /= d;
        super.y /= d;
        super.z /= d;
    }
    
    public final double dot(final Vector3d v1) {
        return super.x * v1.x + super.y * v1.y + super.z * v1.z;
    }
    
    public final double lengthSquared() {
        return super.x * super.x + super.y * super.y + super.z * super.z;
    }
    
    public final double length() {
        return Math.sqrt(this.lengthSquared());
    }
    
    public final double angle(final Vector3d v1) {
        final double xx = super.y * v1.z - super.z * v1.y;
        final double yy = super.z * v1.x - super.x * v1.z;
        final double zz = super.x * v1.y - super.y * v1.x;
        final double cross = Math.sqrt(xx * xx + yy * yy + zz * zz);
        return Math.abs(Math.atan2(cross, this.dot(v1)));
    }
}
