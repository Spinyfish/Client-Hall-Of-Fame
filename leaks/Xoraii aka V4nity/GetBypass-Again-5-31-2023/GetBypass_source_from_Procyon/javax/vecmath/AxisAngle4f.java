// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class AxisAngle4f implements Serializable
{
    public float x;
    public float y;
    public float z;
    public float angle;
    
    public AxisAngle4f(final float x, final float y, final float z, final float angle) {
        this.set(x, y, z, angle);
    }
    
    public AxisAngle4f(final float[] a) {
        this.set(a);
    }
    
    public AxisAngle4f(final AxisAngle4f a1) {
        this.set(a1);
    }
    
    public AxisAngle4f(final AxisAngle4d a1) {
        this.set(a1);
    }
    
    public AxisAngle4f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 1.0f;
        this.angle = 0.0f;
    }
    
    public AxisAngle4f(final Vector3f axis, final float angle) {
        this.x = axis.x;
        this.y = axis.y;
        this.z = axis.z;
        this.angle = angle;
    }
    
    public final void set(final Vector3f axis, final float angle) {
        this.x = axis.x;
        this.y = axis.y;
        this.z = axis.z;
        this.angle = angle;
    }
    
    public final void set(final float x, final float y, final float z, final float angle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
    }
    
    public final void set(final float[] a) {
        this.x = a[0];
        this.y = a[1];
        this.z = a[2];
        this.angle = a[3];
    }
    
    public final void set(final AxisAngle4f a1) {
        this.x = a1.x;
        this.y = a1.y;
        this.z = a1.z;
        this.angle = a1.angle;
    }
    
    public final void set(final AxisAngle4d a1) {
        this.x = (float)a1.x;
        this.y = (float)a1.y;
        this.z = (float)a1.z;
        this.angle = (float)a1.angle;
    }
    
    public final void get(final float[] a) {
        a[0] = this.x;
        a[1] = this.y;
        a[2] = this.z;
        a[3] = this.angle;
    }
    
    public final void set(final Matrix4f m1) {
        this.setFromMat(m1.m00, m1.m01, m1.m02, m1.m10, m1.m11, m1.m12, m1.m20, m1.m21, m1.m22);
    }
    
    public final void set(final Matrix4d m1) {
        this.setFromMat(m1.m00, m1.m01, m1.m02, m1.m10, m1.m11, m1.m12, m1.m20, m1.m21, m1.m22);
    }
    
    public final void set(final Matrix3f m1) {
        this.setFromMat(m1.m00, m1.m01, m1.m02, m1.m10, m1.m11, m1.m12, m1.m20, m1.m21, m1.m22);
    }
    
    public final void set(final Matrix3d m1) {
        this.setFromMat(m1.m00, m1.m01, m1.m02, m1.m10, m1.m11, m1.m12, m1.m20, m1.m21, m1.m22);
    }
    
    public final void set(final Quat4f q1) {
        this.setFromQuat(q1.x, q1.y, q1.z, q1.w);
    }
    
    public final void set(final Quat4d q1) {
        this.setFromQuat(q1.x, q1.y, q1.z, q1.w);
    }
    
    private void setFromMat(final double m00, final double m01, final double m02, final double m10, final double m11, final double m12, final double m20, final double m21, final double m22) {
        final double cos = (m00 + m11 + m22 - 1.0) * 0.5;
        this.x = (float)(m21 - m12);
        this.y = (float)(m02 - m20);
        this.z = (float)(m10 - m01);
        final double sin = 0.5 * Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.angle = (float)Math.atan2(sin, cos);
    }
    
    private void setFromQuat(final double x, final double y, final double z, final double w) {
        final double sin_a2 = Math.sqrt(x * x + y * y + z * z);
        this.angle = (float)(2.0 * Math.atan2(sin_a2, w));
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }
    
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
    }
    
    public boolean equals(final AxisAngle4f a1) {
        return a1 != null && this.x == a1.x && this.y == a1.y && this.z == a1.z && this.angle == a1.angle;
    }
    
    public boolean equals(final Object o1) {
        return o1 != null && o1 instanceof AxisAngle4f && this.equals((AxisAngle4f)o1);
    }
    
    public boolean epsilonEquals(final AxisAngle4f a1, final float epsilon) {
        return Math.abs(a1.x - this.x) <= epsilon && Math.abs(a1.y - this.y) <= epsilon && Math.abs(a1.z - this.z) <= epsilon && Math.abs(a1.angle - this.angle) <= epsilon;
    }
    
    public int hashCode() {
        return Float.floatToIntBits(this.x) ^ Float.floatToIntBits(this.y) ^ Float.floatToIntBits(this.z) ^ Float.floatToIntBits(this.angle);
    }
}
