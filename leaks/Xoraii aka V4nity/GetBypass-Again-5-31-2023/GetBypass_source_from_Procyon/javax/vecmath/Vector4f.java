// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Vector4f extends Tuple4f implements Serializable
{
    public Vector4f(final float x, final float y, final float z, final float w) {
        super(x, y, z, w);
    }
    
    public Vector4f(final float[] v) {
        super(v);
    }
    
    public Vector4f(final Vector4f v1) {
        super(v1);
    }
    
    public Vector4f(final Vector4d v1) {
        super(v1);
    }
    
    public Vector4f(final Tuple4d t1) {
        super(t1);
    }
    
    public Vector4f(final Tuple4f t1) {
        super(t1);
    }
    
    public Vector4f() {
    }
    
    public Vector4f(final Tuple3f t1) {
        super(t1.x, t1.y, t1.z, 0.0f);
    }
    
    public final void set(final Tuple3f t1) {
        this.set(t1.x, t1.y, t1.z, 0.0f);
    }
    
    public final float lengthSquared() {
        return super.x * super.x + super.y * super.y + super.z * super.z + super.w * super.w;
    }
    
    public final float length() {
        return (float)Math.sqrt(this.lengthSquared());
    }
    
    public final float dot(final Vector4f v1) {
        return super.x * v1.x + super.y * v1.y + super.z * v1.z + super.w * v1.w;
    }
    
    public final void normalize(final Vector4d v1) {
        this.set(v1);
        this.normalize();
    }
    
    public final void normalize() {
        final double d = this.length();
        super.x /= (float)d;
        super.y /= (float)d;
        super.z /= (float)d;
        super.w /= (float)d;
    }
    
    public final float angle(final Vector4f v1) {
        final double d = this.dot(v1);
        final double v1_length = v1.length();
        final double v_length = this.length();
        return (float)Math.acos(d / v1_length / v_length);
    }
}
