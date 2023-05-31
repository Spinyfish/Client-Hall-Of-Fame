// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple2f implements Serializable
{
    public float x;
    public float y;
    
    public Tuple2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public Tuple2f(final float[] t) {
        this.x = t[0];
        this.y = t[1];
    }
    
    public Tuple2f(final Tuple2f t1) {
        this.x = t1.x;
        this.y = t1.y;
    }
    
    public Tuple2f(final Tuple2d t1) {
        this.x = (float)t1.x;
        this.y = (float)t1.y;
    }
    
    public Tuple2f() {
        this.x = 0.0f;
        this.y = 0.0f;
    }
    
    public final void set(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public final void set(final float[] t) {
        this.x = t[0];
        this.y = t[1];
    }
    
    public final void set(final Tuple2f t1) {
        this.x = t1.x;
        this.y = t1.y;
    }
    
    public final void set(final Tuple2d t1) {
        this.x = (float)t1.x;
        this.y = (float)t1.y;
    }
    
    public final void get(final float[] t) {
        t[0] = this.x;
        t[1] = this.y;
    }
    
    public final void add(final Tuple2f t1, final Tuple2f t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
    }
    
    public final void add(final Tuple2f t1) {
        this.x += t1.x;
        this.y += t1.y;
    }
    
    public final void sub(final Tuple2f t1, final Tuple2f t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
    }
    
    public final void sub(final Tuple2f t1) {
        this.x -= t1.x;
        this.y -= t1.y;
    }
    
    public final void negate(final Tuple2f t1) {
        this.x = -t1.x;
        this.y = -t1.y;
    }
    
    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }
    
    public final void scale(final float s, final Tuple2f t1) {
        this.x = s * t1.x;
        this.y = s * t1.y;
    }
    
    public final void scale(final float s) {
        this.x *= s;
        this.y *= s;
    }
    
    public final void scaleAdd(final float s, final Tuple2f t1, final Tuple2f t2) {
        this.x = s * t1.x + t2.x;
        this.y = s * t1.y + t2.y;
    }
    
    public final void scaleAdd(final float s, final Tuple2f t1) {
        this.x = s * this.x + t1.x;
        this.y = s * this.y + t1.y;
    }
    
    public int hashCode() {
        final int xbits = Float.floatToIntBits(this.x);
        final int ybits = Float.floatToIntBits(this.y);
        return xbits ^ ybits;
    }
    
    public boolean equals(final Tuple2f t1) {
        return t1 != null && this.x == t1.x && this.y == t1.y;
    }
    
    public boolean equals(final Object o1) {
        return o1 != null && o1 instanceof Tuple2f && this.equals((Tuple2f)o1);
    }
    
    public boolean epsilonEquals(final Tuple2f t1, final float epsilon) {
        return Math.abs(t1.x - this.x) <= epsilon && Math.abs(t1.y - this.y) <= epsilon;
    }
    
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
    
    public final void clamp(final float min, final float max, final Tuple2f t) {
        this.set(t);
        this.clamp(min, max);
    }
    
    public final void clampMin(final float min, final Tuple2f t) {
        this.set(t);
        this.clampMin(min);
    }
    
    public final void clampMax(final float max, final Tuple2f t) {
        this.set(t);
        this.clampMax(max);
    }
    
    public final void absolute(final Tuple2f t) {
        this.set(t);
        this.absolute();
    }
    
    public final void clamp(final float min, final float max) {
        this.clampMin(min);
        this.clampMax(max);
    }
    
    public final void clampMin(final float min) {
        if (this.x < min) {
            this.x = min;
        }
        if (this.y < min) {
            this.y = min;
        }
    }
    
    public final void clampMax(final float max) {
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
    
    public final void interpolate(final Tuple2f t1, final Tuple2f t2, final float alpha) {
        this.set(t1);
        this.interpolate(t2, alpha);
    }
    
    public final void interpolate(final Tuple2f t1, final float alpha) {
        final float beta = 1.0f - alpha;
        this.x = beta * this.x + alpha * t1.x;
        this.y = beta * this.y + alpha * t1.y;
    }
}
