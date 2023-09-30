// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Quat4d extends Tuple4d implements Serializable
{
    public Quat4d(final double x, final double y, final double z, final double w) {
        super(x, y, z, w);
    }
    
    public Quat4d(final double[] q) {
        super(q);
    }
    
    public Quat4d(final Quat4d q1) {
        super(q1);
    }
    
    public Quat4d(final Quat4f q1) {
        super(q1);
    }
    
    public Quat4d(final Tuple4d t1) {
        super(t1);
    }
    
    public Quat4d(final Tuple4f t1) {
        super(t1);
    }
    
    public Quat4d() {
    }
    
    public final void conjugate(final Quat4d q1) {
        super.x = -q1.x;
        super.y = -q1.y;
        super.z = -q1.z;
        super.w = q1.w;
    }
    
    public final void conjugate() {
        super.x = -super.x;
        super.y = -super.y;
        super.z = -super.z;
    }
    
    public final void mul(final Quat4d q1, final Quat4d q2) {
        this.set(q1.x * q2.w + q1.w * q2.x + q1.y * q2.z - q1.z * q2.y, q1.y * q2.w + q1.w * q2.y + q1.z * q2.x - q1.x * q2.z, q1.z * q2.w + q1.w * q2.z + q1.x * q2.y - q1.y * q2.x, q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z);
    }
    
    public final void mul(final Quat4d q1) {
        this.set(super.x * q1.w + super.w * q1.x + super.y * q1.z - super.z * q1.y, super.y * q1.w + super.w * q1.y + super.z * q1.x - super.x * q1.z, super.z * q1.w + super.w * q1.z + super.x * q1.y - super.y * q1.x, super.w * q1.w - super.x * q1.x - super.y * q1.y - super.z * q1.z);
    }
    
    public final void mulInverse(final Quat4d q1, final Quat4d q2) {
        double n = this.norm();
        n = ((n == 0.0) ? n : (1.0 / n));
        this.set((q1.x * q2.w - q1.w * q2.x - q1.y * q2.z + q1.z * q2.y) * n, (q1.y * q2.w - q1.w * q2.y - q1.z * q2.x + q1.x * q2.z) * n, (q1.z * q2.w - q1.w * q2.z - q1.x * q2.y + q1.y * q2.x) * n, (q1.w * q2.w + q1.x * q2.x + q1.y * q2.y + q1.z * q2.z) * n);
    }
    
    public final void mulInverse(final Quat4d q1) {
        double n = this.norm();
        n = ((n == 0.0) ? n : (1.0 / n));
        this.set((super.x * q1.w - super.w * q1.x - super.y * q1.z + super.z * q1.y) * n, (super.y * q1.w - super.w * q1.y - super.z * q1.x + super.x * q1.z) * n, (super.z * q1.w - super.w * q1.z - super.x * q1.y + super.y * q1.x) * n, (super.w * q1.w + super.x * q1.x + super.y * q1.y + super.z * q1.z) * n);
    }
    
    private final double norm() {
        return super.x * super.x + super.y * super.y + super.z * super.z + super.w * super.w;
    }
    
    public final void inverse(final Quat4d q1) {
        final double n = q1.norm();
        super.x = -q1.x / n;
        super.y = -q1.y / n;
        super.z = -q1.z / n;
        super.w = q1.w / n;
    }
    
    public final void inverse() {
        final double n = this.norm();
        super.x = -super.x / n;
        super.y = -super.y / n;
        super.z = -super.z / n;
        super.w /= n;
    }
    
    public final void normalize(final Quat4d q1) {
        final double n = Math.sqrt(q1.norm());
        super.x = q1.x / n;
        super.y = q1.y / n;
        super.z = q1.z / n;
        super.w = q1.w / n;
    }
    
    public final void normalize() {
        final double n = Math.sqrt(this.norm());
        super.x /= n;
        super.y /= n;
        super.z /= n;
        super.w /= n;
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
    
    public final void set(final AxisAngle4f a1) {
        super.x = a1.x;
        super.y = a1.y;
        super.z = a1.z;
        final double n = Math.sqrt(super.x * super.x + super.y * super.y + super.z * super.z);
        final double s = Math.sin(0.5 * a1.angle) / n;
        super.x *= s;
        super.y *= s;
        super.z *= s;
        super.w = Math.cos(0.5 * a1.angle);
    }
    
    public final void set(final AxisAngle4d a1) {
        super.x = a1.x;
        super.y = a1.y;
        super.z = a1.z;
        final double n = Math.sqrt(super.x * super.x + super.y * super.y + super.z * super.z);
        final double s = Math.sin(0.5 * a1.angle) / n;
        super.x *= s;
        super.y *= s;
        super.z *= s;
        super.w = Math.cos(0.5 * a1.angle);
    }
    
    public final void interpolate(final Quat4d q1, final double alpha) {
        this.normalize();
        final double n1 = Math.sqrt(q1.norm());
        final double x1 = q1.x / n1;
        final double y1 = q1.y / n1;
        final double z1 = q1.z / n1;
        final double w1 = q1.w / n1;
        double t = super.x * x1 + super.y * y1 + super.z * z1 + super.w * w1;
        if (1.0 <= Math.abs(t)) {
            return;
        }
        t = Math.acos(t);
        final double sin_t = Math.sin(t);
        if (sin_t == 0.0) {
            return;
        }
        final double s = Math.sin((1.0 - alpha) * t) / sin_t;
        t = Math.sin(alpha * t) / sin_t;
        super.x = s * super.x + t * x1;
        super.y = s * super.y + t * y1;
        super.z = s * super.z + t * z1;
        super.w = s * super.w + t * w1;
    }
    
    public final void interpolate(final Quat4d q1, final Quat4d q2, final double alpha) {
        this.set(q1);
        this.interpolate(q2, alpha);
    }
    
    private void setFromMat(final double m00, final double m01, final double m02, final double m10, final double m11, final double m12, final double m20, final double m21, final double m22) {
        final double tr = m00 + m11 + m22;
        if (tr >= 0.0) {
            double s = Math.sqrt(tr + 1.0);
            super.w = s * 0.5;
            s = 0.5 / s;
            super.x = (m21 - m12) * s;
            super.y = (m02 - m20) * s;
            super.z = (m10 - m01) * s;
        }
        else {
            final double max = Math.max(Math.max(m00, m11), m22);
            if (max == m00) {
                double s = Math.sqrt(m00 - (m11 + m22) + 1.0);
                super.x = s * 0.5;
                s = 0.5 / s;
                super.y = (m01 + m10) * s;
                super.z = (m20 + m02) * s;
                super.w = (m21 - m12) * s;
            }
            else if (max == m11) {
                double s = Math.sqrt(m11 - (m22 + m00) + 1.0);
                super.y = s * 0.5;
                s = 0.5 / s;
                super.z = (m12 + m21) * s;
                super.x = (m01 + m10) * s;
                super.w = (m02 - m20) * s;
            }
            else {
                double s = Math.sqrt(m22 - (m00 + m11) + 1.0);
                super.z = s * 0.5;
                s = 0.5 / s;
                super.x = (m20 + m02) * s;
                super.y = (m12 + m21) * s;
                super.w = (m10 - m01) * s;
            }
        }
    }
}
