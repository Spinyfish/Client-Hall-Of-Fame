// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Vector4d extends Tuple4d implements Serializable
{
    public Vector4d(final double x, final double y, final double z, final double w) {
        super(x, y, z, w);
    }
    
    public Vector4d(final double[] v) {
        super(v);
    }
    
    public Vector4d(final Vector4f v1) {
        super(v1);
    }
    
    public Vector4d(final Vector4d v1) {
        super(v1);
    }
    
    public Vector4d(final Tuple4d t1) {
        super(t1);
    }
    
    public Vector4d(final Tuple4f t1) {
        super(t1);
    }
    
    public Vector4d() {
    }
    
    public Vector4d(final Tuple3d t1) {
        super(t1.x, t1.y, t1.z, 0.0);
    }
    
    public final void set(final Tuple3d t1) {
        this.set(t1.x, t1.y, t1.z, 0.0);
    }
    
    public final double lengthSquared() {
        return super.x * super.x + super.y * super.y + super.z * super.z + super.w * super.w;
    }
    
    public final double length() {
        return Math.sqrt(this.lengthSquared());
    }
    
    public final double dot(final Vector4d v1) {
        return super.x * v1.x + super.y * v1.y + super.z * v1.z + super.w * v1.w;
    }
    
    public final void normalize(final Vector4d v1) {
        this.set(v1);
        this.normalize();
    }
    
    public final void normalize() {
        final double d = this.length();
        super.x /= d;
        super.y /= d;
        super.z /= d;
        super.w /= d;
    }
    
    public final double angle(final Vector4d v1) {
        final double d = this.dot(v1);
        final double v1_length = v1.length();
        final double v_length = this.length();
        return Math.acos(d / v1_length / v_length);
    }
}
