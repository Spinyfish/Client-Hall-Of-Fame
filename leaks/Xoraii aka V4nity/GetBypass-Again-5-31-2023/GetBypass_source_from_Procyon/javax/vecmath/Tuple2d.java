// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple2d implements Serializable
{
    public double x;
    public double y;
    
    public Tuple2d(final double x, final double y) {
        this.x = x;
        this.y = y;
    }
    
    public Tuple2d(final double[] t) {
        this.x = t[0];
        this.y = t[1];
    }
    
    public Tuple2d(final Tuple2d t1) {
        this.x = t1.x;
        this.y = t1.y;
    }
    
    public Tuple2d(final Tuple2f t1) {
        this.x = t1.x;
        this.y = t1.y;
    }
    
    public Tuple2d() {
        this.x = 0.0;
        this.y = 0.0;
    }
    
    public final void set(final double x, final double y) {
        this.x = x;
        this.y = y;
    }
    
    public final void set(final double[] t) {
        this.x = t[0];
        this.y = t[1];
    }
    
    public final void set(final Tuple2d t1) {
        this.x = t1.x;
        this.y = t1.y;
    }
    
    public final void set(final Tuple2f t1) {
        this.x = t1.x;
        this.y = t1.y;
    }
    
    public final void get(final double[] t) {
        t[0] = this.x;
        t[1] = this.y;
    }
    
    public final void add(final Tuple2d t1, final Tuple2d t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
    }
    
    public final void add(final Tuple2d t1) {
        this.x += t1.x;
        this.y += t1.y;
    }
    
    public final void sub(final Tuple2d t1, final Tuple2d t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
    }
    
    public final void sub(final Tuple2d t1) {
        this.x -= t1.x;
        this.y -= t1.y;
    }
    
    public final void negate(final Tuple2d t1) {
        this.x = -t1.x;
        this.y = -t1.y;
    }
    
    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }
    
    public final void scale(final double s, final Tuple2d t1) {
        this.x = s * t1.x;
        this.y = s * t1.y;
    }
    
    public final void scale(final double s) {
        this.x *= s;
        this.y *= s;
    }
    
    public final void scaleAdd(final double s, final Tuple2d t1, final Tuple2d t2) {
        this.x = s * t1.x + t2.x;
        this.y = s * t1.y + t2.y;
    }
    
    public final void scaleAdd(final double s, final Tuple2d t1) {
        this.x = s * this.x + t1.x;
        this.y = s * this.y + t1.y;
    }
    
    public int hashCode() {
        final long xbits = Double.doubleToLongBits(this.x);
        final long ybits = Double.doubleToLongBits(this.y);
        return (int)(xbits ^ xbits >> 32 ^ ybits ^ ybits >> 32);
    }
    
    public boolean equals(final Tuple2d t1) {
        return t1 != null && this.x == t1.x && this.y == t1.y;
    }
    
    public boolean equals(final Object o1) {
        return o1 != null && o1 instanceof Tuple2d && this.equals((Tuple2d)o1);
    }
    
    public boolean epsilonEquals(final Tuple2d t1, final double epsilon) {
        return Math.abs(t1.x - this.x) <= epsilon && Math.abs(t1.y - this.y) <= epsilon;
    }
    
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
    
    public final void clamp(final double min, final double max, final Tuple2d t) {
        this.set(t);
        this.clamp(min, max);
    }
    
    public final void clampMin(final double min, final Tuple2d t) {
        this.set(t);
        this.clampMin(min);
    }
    
    public final void clampMax(final double max, final Tuple2d t) {
        this.set(t);
        this.clampMax(max);
    }
    
    public final void absolute(final Tuple2d t) {
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
    }
    
    public final void clampMax(final double max) {
        if (this.x > max) {
            this.x = max;
        }
        if (this.y > max) {
            this.y = max;
        }
    }
    
    public final void absolute() {
        if (this.x < 0.0) {
            this.x = -this.x;
        }
        if (this.y < 0.0) {
            this.y = -this.y;
        }
    }
    
    public final void interpolate(final Tuple2d t1, final Tuple2d t2, final double alpha) {
        this.set(t1);
        this.interpolate(t2, alpha);
    }
    
    public final void interpolate(final Tuple2d t1, final double alpha) {
        final double beta = 1.0 - alpha;
        this.x = beta * this.x + alpha * t1.x;
        this.y = beta * this.y + alpha * t1.y;
    }
}
