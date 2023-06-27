package javax.vecmath;

import java.io.*;

public abstract class Tuple3i implements Serializable, Cloneable
{
    static final long serialVersionUID = -732740491767276200L;
    public int x;
    public int y;
    public int z;
    
    public Tuple3i(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Tuple3i(final int[] t) {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
    }
    
    public Tuple3i(final Tuple3i t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }
    
    public Tuple3i() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
    public final void set(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public final void set(final int[] t) {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
    }
    
    public final void set(final Tuple3i t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }
    
    public final void get(final int[] t) {
        t[0] = this.x;
        t[1] = this.y;
        t[2] = this.z;
    }
    
    public final void get(final Tuple3i t) {
        t.x = this.x;
        t.y = this.y;
        t.z = this.z;
    }
    
    public final void add(final Tuple3i t1, final Tuple3i t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
    }
    
    public final void add(final Tuple3i t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
    }
    
    public final void sub(final Tuple3i t1, final Tuple3i t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
    }
    
    public final void sub(final Tuple3i t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
    }
    
    public final void negate(final Tuple3i t1) {
        this.x = -t1.x;
        this.y = -t1.y;
        this.z = -t1.z;
    }
    
    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }
    
    public final void scale(final int s, final Tuple3i t1) {
        this.x = s * t1.x;
        this.y = s * t1.y;
        this.z = s * t1.z;
    }
    
    public final void scale(final int s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }
    
    public final void scaleAdd(final int s, final Tuple3i t1, final Tuple3i t2) {
        this.x = s * t1.x + t2.x;
        this.y = s * t1.y + t2.y;
        this.z = s * t1.z + t2.z;
    }
    
    public final void scaleAdd(final int s, final Tuple3i t1) {
        this.x = s * this.x + t1.x;
        this.y = s * this.y + t1.y;
        this.z = s * this.z + t1.z;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
    
    @Override
    public boolean equals(final Object t1) {
        try {
            final Tuple3i t2 = (Tuple3i)t1;
            return this.x == t2.x && this.y == t2.y && this.z == t2.z;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e3) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + this.x;
        bits = 31L * bits + this.y;
        bits = 31L * bits + this.z;
        return (int)(bits ^ bits >> 32);
    }
    
    public final void clamp(final int min, final int max, final Tuple3i t) {
        if (t.x > max) {
            this.x = max;
        }
        else if (t.x < min) {
            this.x = min;
        }
        else {
            this.x = t.x;
        }
        if (t.y > max) {
            this.y = max;
        }
        else if (t.y < min) {
            this.y = min;
        }
        else {
            this.y = t.y;
        }
        if (t.z > max) {
            this.z = max;
        }
        else if (t.z < min) {
            this.z = min;
        }
        else {
            this.z = t.z;
        }
    }
    
    public final void clampMin(final int min, final Tuple3i t) {
        if (t.x < min) {
            this.x = min;
        }
        else {
            this.x = t.x;
        }
        if (t.y < min) {
            this.y = min;
        }
        else {
            this.y = t.y;
        }
        if (t.z < min) {
            this.z = min;
        }
        else {
            this.z = t.z;
        }
    }
    
    public final void clampMax(final int max, final Tuple3i t) {
        if (t.x > max) {
            this.x = max;
        }
        else {
            this.x = t.x;
        }
        if (t.y > max) {
            this.y = max;
        }
        else {
            this.y = t.y;
        }
        if (t.z > max) {
            this.z = max;
        }
        else {
            this.z = t.z;
        }
    }
    
    public final void absolute(final Tuple3i t) {
        this.x = Math.abs(t.x);
        this.y = Math.abs(t.y);
        this.z = Math.abs(t.z);
    }
    
    public final void clamp(final int min, final int max) {
        if (this.x > max) {
            this.x = max;
        }
        else if (this.x < min) {
            this.x = min;
        }
        if (this.y > max) {
            this.y = max;
        }
        else if (this.y < min) {
            this.y = min;
        }
        if (this.z > max) {
            this.z = max;
        }
        else if (this.z < min) {
            this.z = min;
        }
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
    }
    
    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    public final int getX() {
        return this.x;
    }
    
    public final void setX(final int x) {
        this.x = x;
    }
    
    public final int getY() {
        return this.y;
    }
    
    public final void setY(final int y) {
        this.y = y;
    }
    
    public final int getZ() {
        return this.z;
    }
    
    public final void setZ(final int z) {
        this.z = z;
    }
}
