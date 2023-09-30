// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple4i implements Serializable
{
    public int x;
    public int y;
    public int z;
    public int w;
    
    public Tuple4i(final int x, final int y, final int z, final int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Tuple4i(final int[] t) {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
        this.w = t[3];
    }
    
    public Tuple4i(final Tuple4i t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }
    
    public Tuple4i() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }
    
    public final void set(final int x, final int y, final int z, final int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public final void set(final int[] t) {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
        this.w = t[3];
    }
    
    public final void set(final Tuple4i t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }
    
    public final void get(final int[] t) {
        t[0] = this.x;
        t[1] = this.y;
        t[2] = this.z;
        t[3] = this.w;
    }
    
    public final void get(final Tuple4i t) {
        t.x = this.x;
        t.y = this.y;
        t.z = this.z;
        t.w = this.w;
    }
    
    public final void add(final Tuple4i t1, final Tuple4i t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
        this.w = t1.w + t2.w;
    }
    
    public final void add(final Tuple4i t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
        this.w += t1.w;
    }
    
    public final void sub(final Tuple4i t1, final Tuple4i t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
        this.w = t1.w - t2.w;
    }
    
    public final void sub(final Tuple4i t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
        this.w -= t1.w;
    }
    
    public final void negate(final Tuple4i t1) {
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
    
    public final void scale(final int s, final Tuple4i t1) {
        this.x = s * t1.x;
        this.y = s * t1.y;
        this.z = s * t1.z;
        this.w = s * t1.w;
    }
    
    public final void scale(final int s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.w *= s;
    }
    
    public final void scaleAdd(final int s, final Tuple4i t1, final Tuple4i t2) {
        this.x = s * t1.x + t2.x;
        this.y = s * t1.y + t2.y;
        this.z = s * t1.z + t2.z;
        this.w = s * t1.w + t2.w;
    }
    
    public final void scaleAdd(final int s, final Tuple4i t1) {
        this.x = s * this.x + t1.x;
        this.y = s * this.y + t1.y;
        this.z = s * this.z + t1.z;
        this.w = s * this.w + t1.w;
    }
    
    public int hashCode() {
        return this.x ^ this.y ^ (this.z ^ this.w);
    }
    
    private boolean equals(final Tuple4i t1) {
        return t1 != null && this.x == t1.x && this.y == t1.y && this.z == t1.z;
    }
    
    public boolean equals(final Object o1) {
        return o1 != null && o1 instanceof Tuple4i && this.equals((Tuple4i)o1);
    }
    
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }
    
    public final void clamp(final int min, final int max, final Tuple4i t) {
        this.set(t);
        this.clamp(min, max);
    }
    
    public final void clampMin(final int min, final Tuple4i t) {
        this.set(t);
        this.clampMin(min);
    }
    
    public final void clampMax(final int max, final Tuple4i t) {
        this.set(t);
        this.clampMax(max);
    }
    
    public final void absolute(final Tuple4i t) {
        this.set(t);
        this.absolute();
    }
    
    public final void clamp(final int min, final int max) {
        this.clampMin(min);
        this.clampMax(max);
    }
    
    public final void clampMin(final int min) {
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
    
    public final void clampMax(final int max) {
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
}
