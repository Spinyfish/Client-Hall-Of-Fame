// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Vector3f extends Tuple3f implements Serializable
{
    public Vector3f(final float x, final float y, final float z) {
        super(x, y, z);
    }
    
    public Vector3f(final float[] v) {
        super(v);
    }
    
    public Vector3f(final Vector3f v1) {
        super(v1);
    }
    
    public Vector3f(final Vector3d v1) {
        super(v1);
    }
    
    public Vector3f(final Tuple3d t1) {
        super(t1);
    }
    
    public Vector3f(final Tuple3f t1) {
        super(t1);
    }
    
    public Vector3f() {
    }
    
    public final float lengthSquared() {
        return super.x * super.x + super.y * super.y + super.z * super.z;
    }
    
    public final float length() {
        return (float)Math.sqrt(this.lengthSquared());
    }
    
    public final void cross(final Vector3f v1, final Vector3f v2) {
        this.set(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }
    
    public final float dot(final Vector3f v1) {
        return super.x * v1.x + super.y * v1.y + super.z * v1.z;
    }
    
    public final void normalize(final Vector3f v1) {
        this.set(v1);
        this.normalize();
    }
    
    public final void normalize() {
        final double d = this.length();
        super.x /= (float)d;
        super.y /= (float)d;
        super.z /= (float)d;
    }
    
    public final float angle(final Vector3f v1) {
        final double xx = super.y * v1.z - super.z * v1.y;
        final double yy = super.z * v1.x - super.x * v1.z;
        final double zz = super.x * v1.y - super.y * v1.x;
        final double cross = Math.sqrt(xx * xx + yy * yy + zz * zz);
        return (float)Math.abs(Math.atan2(cross, this.dot(v1)));
    }
}
