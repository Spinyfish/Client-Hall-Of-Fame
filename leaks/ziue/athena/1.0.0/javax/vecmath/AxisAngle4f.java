package javax.vecmath;

import java.io.*;

public class AxisAngle4f implements Serializable, Cloneable
{
    static final long serialVersionUID = -163246355858070601L;
    public float x;
    public float y;
    public float z;
    public float angle;
    static final double EPS = 1.0E-6;
    
    public AxisAngle4f(final float x, final float y, final float z, final float angle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
    }
    
    public AxisAngle4f(final float[] a) {
        this.x = a[0];
        this.y = a[1];
        this.z = a[2];
        this.angle = a[3];
    }
    
    public AxisAngle4f(final AxisAngle4f a1) {
        this.x = a1.x;
        this.y = a1.y;
        this.z = a1.z;
        this.angle = a1.angle;
    }
    
    public AxisAngle4f(final AxisAngle4d a1) {
        this.x = (float)a1.x;
        this.y = (float)a1.y;
        this.z = (float)a1.z;
        this.angle = (float)a1.angle;
    }
    
    public AxisAngle4f(final Vector3f axis, final float angle) {
        this.x = axis.x;
        this.y = axis.y;
        this.z = axis.z;
        this.angle = angle;
    }
    
    public AxisAngle4f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 1.0f;
        this.angle = 0.0f;
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
    
    public final void set(final Vector3f axis, final float angle) {
        this.x = axis.x;
        this.y = axis.y;
        this.z = axis.z;
        this.angle = angle;
    }
    
    public final void get(final float[] a) {
        a[0] = this.x;
        a[1] = this.y;
        a[2] = this.z;
        a[3] = this.angle;
    }
    
    public final void set(final Quat4f q1) {
        double mag = q1.x * q1.x + q1.y * q1.y + q1.z * q1.z;
        if (mag > 1.0E-6) {
            mag = Math.sqrt(mag);
            final double invMag = 1.0 / mag;
            this.x = (float)(q1.x * invMag);
            this.y = (float)(q1.y * invMag);
            this.z = (float)(q1.z * invMag);
            this.angle = (float)(2.0 * Math.atan2(mag, q1.w));
        }
        else {
            this.x = 0.0f;
            this.y = 1.0f;
            this.z = 0.0f;
            this.angle = 0.0f;
        }
    }
    
    public final void set(final Quat4d q1) {
        double mag = q1.x * q1.x + q1.y * q1.y + q1.z * q1.z;
        if (mag > 1.0E-6) {
            mag = Math.sqrt(mag);
            final double invMag = 1.0 / mag;
            this.x = (float)(q1.x * invMag);
            this.y = (float)(q1.y * invMag);
            this.z = (float)(q1.z * invMag);
            this.angle = (float)(2.0 * Math.atan2(mag, q1.w));
        }
        else {
            this.x = 0.0f;
            this.y = 1.0f;
            this.z = 0.0f;
            this.angle = 0.0f;
        }
    }
    
    public final void set(final Matrix4f m1) {
        final Matrix3f m3f = new Matrix3f();
        m1.get(m3f);
        this.x = m3f.m21 - m3f.m12;
        this.y = m3f.m02 - m3f.m20;
        this.z = m3f.m10 - m3f.m01;
        double mag = this.x * this.x + this.y * this.y + this.z * this.z;
        if (mag > 1.0E-6) {
            mag = Math.sqrt(mag);
            final double sin = 0.5 * mag;
            final double cos = 0.5 * (m3f.m00 + m3f.m11 + m3f.m22 - 1.0);
            this.angle = (float)Math.atan2(sin, cos);
            final double invMag = 1.0 / mag;
            this.x *= (float)invMag;
            this.y *= (float)invMag;
            this.z *= (float)invMag;
        }
        else {
            this.x = 0.0f;
            this.y = 1.0f;
            this.z = 0.0f;
            this.angle = 0.0f;
        }
    }
    
    public final void set(final Matrix4d m1) {
        final Matrix3d m3d = new Matrix3d();
        m1.get(m3d);
        this.x = (float)(m3d.m21 - m3d.m12);
        this.y = (float)(m3d.m02 - m3d.m20);
        this.z = (float)(m3d.m10 - m3d.m01);
        double mag = this.x * this.x + this.y * this.y + this.z * this.z;
        if (mag > 1.0E-6) {
            mag = Math.sqrt(mag);
            final double sin = 0.5 * mag;
            final double cos = 0.5 * (m3d.m00 + m3d.m11 + m3d.m22 - 1.0);
            this.angle = (float)Math.atan2(sin, cos);
            final double invMag = 1.0 / mag;
            this.x *= (float)invMag;
            this.y *= (float)invMag;
            this.z *= (float)invMag;
        }
        else {
            this.x = 0.0f;
            this.y = 1.0f;
            this.z = 0.0f;
            this.angle = 0.0f;
        }
    }
    
    public final void set(final Matrix3f m1) {
        this.x = m1.m21 - m1.m12;
        this.y = m1.m02 - m1.m20;
        this.z = m1.m10 - m1.m01;
        double mag = this.x * this.x + this.y * this.y + this.z * this.z;
        if (mag > 1.0E-6) {
            mag = Math.sqrt(mag);
            final double sin = 0.5 * mag;
            final double cos = 0.5 * (m1.m00 + m1.m11 + m1.m22 - 1.0);
            this.angle = (float)Math.atan2(sin, cos);
            final double invMag = 1.0 / mag;
            this.x *= (float)invMag;
            this.y *= (float)invMag;
            this.z *= (float)invMag;
        }
        else {
            this.x = 0.0f;
            this.y = 1.0f;
            this.z = 0.0f;
            this.angle = 0.0f;
        }
    }
    
    public final void set(final Matrix3d m1) {
        this.x = (float)(m1.m21 - m1.m12);
        this.y = (float)(m1.m02 - m1.m20);
        this.z = (float)(m1.m10 - m1.m01);
        double mag = this.x * this.x + this.y * this.y + this.z * this.z;
        if (mag > 1.0E-6) {
            mag = Math.sqrt(mag);
            final double sin = 0.5 * mag;
            final double cos = 0.5 * (m1.m00 + m1.m11 + m1.m22 - 1.0);
            this.angle = (float)Math.atan2(sin, cos);
            final double invMag = 1.0 / mag;
            this.x *= (float)invMag;
            this.y *= (float)invMag;
            this.z *= (float)invMag;
        }
        else {
            this.x = 0.0f;
            this.y = 1.0f;
            this.z = 0.0f;
            this.angle = 0.0f;
        }
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
    }
    
    public boolean equals(final AxisAngle4f a1) {
        try {
            return this.x == a1.x && this.y == a1.y && this.z == a1.z && this.angle == a1.angle;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    @Override
    public boolean equals(final Object o1) {
        try {
            final AxisAngle4f a2 = (AxisAngle4f)o1;
            return this.x == a2.x && this.y == a2.y && this.z == a2.z && this.angle == a2.angle;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e3) {
            return false;
        }
    }
    
    public boolean epsilonEquals(final AxisAngle4f a1, final float epsilon) {
        float diff = this.x - a1.x;
        if (((diff < 0.0f) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.y - a1.y;
        if (((diff < 0.0f) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.z - a1.z;
        if (((diff < 0.0f) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.angle - a1.angle;
        return ((diff < 0.0f) ? (-diff) : diff) <= epsilon;
    }
    
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.x);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.y);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.z);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.angle);
        return (int)(bits ^ bits >> 32);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    public final float getAngle() {
        return this.angle;
    }
    
    public final void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public final float getX() {
        return this.x;
    }
    
    public final void setX(final float x) {
        this.x = x;
    }
    
    public final float getY() {
        return this.y;
    }
    
    public final void setY(final float y) {
        this.y = y;
    }
    
    public final float getZ() {
        return this.z;
    }
    
    public final void setZ(final float z) {
        this.z = z;
    }
}
