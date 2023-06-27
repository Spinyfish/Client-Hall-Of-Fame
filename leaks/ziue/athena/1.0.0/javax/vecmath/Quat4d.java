package javax.vecmath;

import java.io.*;

public class Quat4d extends Tuple4d implements Serializable
{
    static final long serialVersionUID = 7577479888820201099L;
    static final double EPS = 1.0E-12;
    static final double EPS2 = 1.0E-30;
    static final double PIO2 = 1.57079632679;
    
    public Quat4d(final double x, final double y, final double z, final double w) {
        final double mag = 1.0 / Math.sqrt(x * x + y * y + z * z + w * w);
        this.x = x * mag;
        this.y = y * mag;
        this.z = z * mag;
        this.w = w * mag;
    }
    
    public Quat4d(final double[] q) {
        final double mag = 1.0 / Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        this.x = q[0] * mag;
        this.y = q[1] * mag;
        this.z = q[2] * mag;
        this.w = q[3] * mag;
    }
    
    public Quat4d(final Quat4d q1) {
        super(q1);
    }
    
    public Quat4d(final Quat4f q1) {
        super(q1);
    }
    
    public Quat4d(final Tuple4f t1) {
        final double mag = 1.0 / Math.sqrt(t1.x * t1.x + t1.y * t1.y + t1.z * t1.z + t1.w * t1.w);
        this.x = t1.x * mag;
        this.y = t1.y * mag;
        this.z = t1.z * mag;
        this.w = t1.w * mag;
    }
    
    public Quat4d(final Tuple4d t1) {
        final double mag = 1.0 / Math.sqrt(t1.x * t1.x + t1.y * t1.y + t1.z * t1.z + t1.w * t1.w);
        this.x = t1.x * mag;
        this.y = t1.y * mag;
        this.z = t1.z * mag;
        this.w = t1.w * mag;
    }
    
    public Quat4d() {
    }
    
    public final void conjugate(final Quat4d q1) {
        this.x = -q1.x;
        this.y = -q1.y;
        this.z = -q1.z;
        this.w = q1.w;
    }
    
    public final void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }
    
    public final void mul(final Quat4d q1, final Quat4d q2) {
        if (this != q1 && this != q2) {
            this.w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
            this.x = q1.w * q2.x + q2.w * q1.x + q1.y * q2.z - q1.z * q2.y;
            this.y = q1.w * q2.y + q2.w * q1.y - q1.x * q2.z + q1.z * q2.x;
            this.z = q1.w * q2.z + q2.w * q1.z + q1.x * q2.y - q1.y * q2.x;
        }
        else {
            final double w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
            final double x = q1.w * q2.x + q2.w * q1.x + q1.y * q2.z - q1.z * q2.y;
            final double y = q1.w * q2.y + q2.w * q1.y - q1.x * q2.z + q1.z * q2.x;
            this.z = q1.w * q2.z + q2.w * q1.z + q1.x * q2.y - q1.y * q2.x;
            this.w = w;
            this.x = x;
            this.y = y;
        }
    }
    
    public final void mul(final Quat4d q1) {
        final double w = this.w * q1.w - this.x * q1.x - this.y * q1.y - this.z * q1.z;
        final double x = this.w * q1.x + q1.w * this.x + this.y * q1.z - this.z * q1.y;
        final double y = this.w * q1.y + q1.w * this.y - this.x * q1.z + this.z * q1.x;
        this.z = this.w * q1.z + q1.w * this.z + this.x * q1.y - this.y * q1.x;
        this.w = w;
        this.x = x;
        this.y = y;
    }
    
    public final void mulInverse(final Quat4d q1, final Quat4d q2) {
        final Quat4d tempQuat = new Quat4d(q2);
        tempQuat.inverse();
        this.mul(q1, tempQuat);
    }
    
    public final void mulInverse(final Quat4d q1) {
        final Quat4d tempQuat = new Quat4d(q1);
        tempQuat.inverse();
        this.mul(tempQuat);
    }
    
    public final void inverse(final Quat4d q1) {
        final double norm = 1.0 / (q1.w * q1.w + q1.x * q1.x + q1.y * q1.y + q1.z * q1.z);
        this.w = norm * q1.w;
        this.x = -norm * q1.x;
        this.y = -norm * q1.y;
        this.z = -norm * q1.z;
    }
    
    public final void inverse() {
        final double norm = 1.0 / (this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
        this.w *= norm;
        this.x *= -norm;
        this.y *= -norm;
        this.z *= -norm;
    }
    
    public final void normalize(final Quat4d q1) {
        double norm = q1.x * q1.x + q1.y * q1.y + q1.z * q1.z + q1.w * q1.w;
        if (norm > 0.0) {
            norm = 1.0 / Math.sqrt(norm);
            this.x = norm * q1.x;
            this.y = norm * q1.y;
            this.z = norm * q1.z;
            this.w = norm * q1.w;
        }
        else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
            this.w = 0.0;
        }
    }
    
    public final void normalize() {
        double norm = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if (norm > 0.0) {
            norm = 1.0 / Math.sqrt(norm);
            this.x *= norm;
            this.y *= norm;
            this.z *= norm;
            this.w *= norm;
        }
        else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
            this.w = 0.0;
        }
    }
    
    public final void set(final Matrix4f m1) {
        double ww = 0.25 * (m1.m00 + m1.m11 + m1.m22 + m1.m33);
        if (ww < 0.0) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        if (ww >= 1.0E-30) {
            this.w = Math.sqrt(ww);
            ww = 0.25 / this.w;
            this.x = (m1.m21 - m1.m12) * ww;
            this.y = (m1.m02 - m1.m20) * ww;
            this.z = (m1.m10 - m1.m01) * ww;
            return;
        }
        this.w = 0.0;
        ww = -0.5 * (m1.m11 + m1.m22);
        if (ww < 0.0) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        if (ww >= 1.0E-30) {
            this.x = Math.sqrt(ww);
            ww = 1.0 / (2.0 * this.x);
            this.y = m1.m10 * ww;
            this.z = m1.m20 * ww;
            return;
        }
        this.x = 0.0;
        ww = 0.5 * (1.0 - m1.m22);
        if (ww >= 1.0E-30) {
            this.y = Math.sqrt(ww);
            this.z = m1.m21 / (2.0 * this.y);
            return;
        }
        this.y = 0.0;
        this.z = 1.0;
    }
    
    public final void set(final Matrix4d m1) {
        double ww = 0.25 * (m1.m00 + m1.m11 + m1.m22 + m1.m33);
        if (ww < 0.0) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        if (ww >= 1.0E-30) {
            this.w = Math.sqrt(ww);
            ww = 0.25 / this.w;
            this.x = (m1.m21 - m1.m12) * ww;
            this.y = (m1.m02 - m1.m20) * ww;
            this.z = (m1.m10 - m1.m01) * ww;
            return;
        }
        this.w = 0.0;
        ww = -0.5 * (m1.m11 + m1.m22);
        if (ww < 0.0) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        if (ww >= 1.0E-30) {
            this.x = Math.sqrt(ww);
            ww = 0.5 / this.x;
            this.y = m1.m10 * ww;
            this.z = m1.m20 * ww;
            return;
        }
        this.x = 0.0;
        ww = 0.5 * (1.0 - m1.m22);
        if (ww >= 1.0E-30) {
            this.y = Math.sqrt(ww);
            this.z = m1.m21 / (2.0 * this.y);
            return;
        }
        this.y = 0.0;
        this.z = 1.0;
    }
    
    public final void set(final Matrix3f m1) {
        double ww = 0.25 * (m1.m00 + m1.m11 + m1.m22 + 1.0);
        if (ww < 0.0) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        if (ww >= 1.0E-30) {
            this.w = Math.sqrt(ww);
            ww = 0.25 / this.w;
            this.x = (m1.m21 - m1.m12) * ww;
            this.y = (m1.m02 - m1.m20) * ww;
            this.z = (m1.m10 - m1.m01) * ww;
            return;
        }
        this.w = 0.0;
        ww = -0.5 * (m1.m11 + m1.m22);
        if (ww < 0.0) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        if (ww >= 1.0E-30) {
            this.x = Math.sqrt(ww);
            ww = 0.5 / this.x;
            this.y = m1.m10 * ww;
            this.z = m1.m20 * ww;
            return;
        }
        this.x = 0.0;
        ww = 0.5 * (1.0 - m1.m22);
        if (ww >= 1.0E-30) {
            this.y = Math.sqrt(ww);
            this.z = m1.m21 / (2.0 * this.y);
        }
        this.y = 0.0;
        this.z = 1.0;
    }
    
    public final void set(final Matrix3d m1) {
        double ww = 0.25 * (m1.m00 + m1.m11 + m1.m22 + 1.0);
        if (ww < 0.0) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        if (ww >= 1.0E-30) {
            this.w = Math.sqrt(ww);
            ww = 0.25 / this.w;
            this.x = (m1.m21 - m1.m12) * ww;
            this.y = (m1.m02 - m1.m20) * ww;
            this.z = (m1.m10 - m1.m01) * ww;
            return;
        }
        this.w = 0.0;
        ww = -0.5 * (m1.m11 + m1.m22);
        if (ww < 0.0) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        if (ww >= 1.0E-30) {
            this.x = Math.sqrt(ww);
            ww = 0.5 / this.x;
            this.y = m1.m10 * ww;
            this.z = m1.m20 * ww;
            return;
        }
        this.x = 0.0;
        ww = 0.5 * (1.0 - m1.m22);
        if (ww >= 1.0E-30) {
            this.y = Math.sqrt(ww);
            this.z = m1.m21 / (2.0 * this.y);
            return;
        }
        this.y = 0.0;
        this.z = 1.0;
    }
    
    public final void set(final AxisAngle4f a) {
        double amag = Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
        if (amag < 1.0E-12) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        }
        else {
            final double mag = Math.sin(a.angle / 2.0);
            amag = 1.0 / amag;
            this.w = Math.cos(a.angle / 2.0);
            this.x = a.x * amag * mag;
            this.y = a.y * amag * mag;
            this.z = a.z * amag * mag;
        }
    }
    
    public final void set(final AxisAngle4d a) {
        double amag = Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
        if (amag < 1.0E-12) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        }
        else {
            amag = 1.0 / amag;
            final double mag = Math.sin(a.angle / 2.0);
            this.w = Math.cos(a.angle / 2.0);
            this.x = a.x * amag * mag;
            this.y = a.y * amag * mag;
            this.z = a.z * amag * mag;
        }
    }
    
    public final void interpolate(final Quat4d q1, final double alpha) {
        double dot = this.x * q1.x + this.y * q1.y + this.z * q1.z + this.w * q1.w;
        if (dot < 0.0) {
            q1.x = -q1.x;
            q1.y = -q1.y;
            q1.z = -q1.z;
            q1.w = -q1.w;
            dot = -dot;
        }
        double s1;
        double s2;
        if (1.0 - dot > 1.0E-12) {
            final double om = Math.acos(dot);
            final double sinom = Math.sin(om);
            s1 = Math.sin((1.0 - alpha) * om) / sinom;
            s2 = Math.sin(alpha * om) / sinom;
        }
        else {
            s1 = 1.0 - alpha;
            s2 = alpha;
        }
        this.w = s1 * this.w + s2 * q1.w;
        this.x = s1 * this.x + s2 * q1.x;
        this.y = s1 * this.y + s2 * q1.y;
        this.z = s1 * this.z + s2 * q1.z;
    }
    
    public final void interpolate(final Quat4d q1, final Quat4d q2, final double alpha) {
        double dot = q2.x * q1.x + q2.y * q1.y + q2.z * q1.z + q2.w * q1.w;
        if (dot < 0.0) {
            q1.x = -q1.x;
            q1.y = -q1.y;
            q1.z = -q1.z;
            q1.w = -q1.w;
            dot = -dot;
        }
        double s1;
        double s2;
        if (1.0 - dot > 1.0E-12) {
            final double om = Math.acos(dot);
            final double sinom = Math.sin(om);
            s1 = Math.sin((1.0 - alpha) * om) / sinom;
            s2 = Math.sin(alpha * om) / sinom;
        }
        else {
            s1 = 1.0 - alpha;
            s2 = alpha;
        }
        this.w = s1 * q1.w + s2 * q2.w;
        this.x = s1 * q1.x + s2 * q2.x;
        this.y = s1 * q1.y + s2 * q2.y;
        this.z = s1 * q1.z + s2 * q2.z;
    }
}
