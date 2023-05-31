// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple4d implements Serializable
{
    public double x;
    public double y;
    public double z;
    public double w;
    
    public Tuple4d(final double x, final double y, final double z, final double w) {
        this.set(x, y, z, w);
    }
    
    public Tuple4d(final double[] t) {
        this.set(t);
    }
    
    public Tuple4d(final Tuple4d t1) {
        this.set(t1);
    }
    
    public Tuple4d(final Tuple4f t1) {
        this.set(t1);
    }
    
    public Tuple4d() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 0.0;
    }
    
    public final void set(final double x, final double y, final double z, final double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public final void set(final double[] t) {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
        this.w = t[3];
    }
    
    public final void set(final Tuple4d t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }
    
    public final void set(final Tuple4f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }
    
    public final void get(final double[] t) {
        t[0] = this.x;
        t[1] = this.y;
        t[2] = this.z;
        t[3] = this.w;
    }
    
    public final void get(final Tuple4d t) {
        t.x = this.x;
        t.y = this.y;
        t.z = this.z;
        t.w = this.w;
    }
    
    public final void add(final Tuple4d t1, final Tuple4d t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
        this.w = t1.w + t2.w;
    }
    
    public final void add(final Tuple4d t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
        this.w += t1.w;
    }
    
    public final void sub(final Tuple4d t1, final Tuple4d t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
        this.w = t1.w - t2.w;
    }
    
    public final void sub(final Tuple4d t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
        this.w -= t1.w;
    }
    
    public final void negate(final Tuple4d t1) {
        this.x = -t1.x;
        this.y = -t1.y;
        this.z = -t1.z;
        this.w = -t1.w;
    }
    
    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }
    
    public final void scale(final double s, final Tuple4d t1) {
        this.x = s * t1.x;
        this.y = s * t1.y;
        this.z = s * t1.z;
        this.w = s * t1.w;
    }
    
    public final void scale(final double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.w *= s;
    }
    
    public final void scaleAdd(final double s, final Tuple4d t1, final Tuple4d t2) {
        this.x = s * t1.x + t2.x;
        this.y = s * t1.y + t2.y;
        this.z = s * t1.z + t2.z;
        this.w = s * t1.w + t2.w;
    }
    
    public final void scaleAdd(final double s, final Tuple4d t1) {
        this.x = s * this.x + t1.x;
        this.y = s * this.y + t1.y;
        this.z = s * this.z + t1.z;
        this.w = s * this.z + t1.w;
    }
    
    public int hashCode() {
        final long xbits = Double.doubleToLongBits(this.x);
        final long ybits = Double.doubleToLongBits(this.y);
        final long zbits = Double.doubleToLongBits(this.z);
        final long wbits = Double.doubleToLongBits(this.w);
        return (int)(xbits ^ xbits >> 32 ^ ybits ^ ybits >> 32 ^ zbits ^ zbits >> 32 ^ wbits ^ wbits >> 32);
    }
    
    public boolean equals(final Tuple4d t1) {
        return t1 != null && this.x == t1.x && this.y == t1.y && this.z == t1.z && this.w == t1.w;
    }
    
    public boolean epsilonEquals(final Tuple4d t1, final double epsilon) {
        return Math.abs(t1.x - this.x) <= epsilon && Math.abs(t1.y - this.y) <= epsilon && Math.abs(t1.z - this.z) <= epsilon && Math.abs(t1.w - this.w) <= epsilon;
    }
    
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }
    
    public final void clamp(final double min, final double max, final Tuple4d t) {
        this.set(t);
        this.clamp(min, max);
    }
    
    public final void clampMin(final double min, final Tuple4d t) {
        this.set(t);
        this.clampMin(min);
    }
    
    public final void clampMax(final double max, final Tuple4d t) {
        this.set(t);
        this.clampMax(max);
    }
    
    public final void absolute(final Tuple4d t) {
        this.set(t);
        this.absolute();
    }
    
    public final void clamp(final double min, final double max) {
        this.clampMin(min);
        this.clampMax(max);
    }
    
    public final void clampMin(final double min) {
        if (this.x < min) {
            this.x = min;
        }
        if (this.y < min) {
            this.y = min;
        }
        if (this.z < min) {
            this.z = min;
        }
        if (this.w < min) {
            this.w = min;
        }
    }
    
    public final void clampMax(final double max) {
        if (this.x > max) {
            this.x = max;
        }
        if (this.y > max) {
            this.y = max;
        }
        if (this.z > max) {
            this.z = max;
        }
        if (this.w > max) {
            this.w = max;
        }
    }
    
    public final void absolute() {
        if (this.x < 0.0) {
            this.x = -this.x;
        }
        if (this.y < 0.0) {
            this.y = -this.y;
        }
        if (this.z < 0.0) {
            this.z = -this.z;
        }
        if (this.w < 0.0) {
            this.w = -this.w;
        }
    }
    
    public final void interpolate(final Tuple4d t1, final Tuple4d t2, final double alpha) {
        this.set(t1);
        this.interpolate(t2, alpha);
    }
    
    public final void interpolate(final Tuple4d t1, final double alpha) {
        final double beta = 1.0 - alpha;
        this.x = beta * this.x + alpha * t1.x;
        this.y = beta * this.y + alpha * t1.y;
        this.z = beta * this.z + alpha * t1.z;
        this.w = beta * this.w + alpha * t1.w;
    }
}
