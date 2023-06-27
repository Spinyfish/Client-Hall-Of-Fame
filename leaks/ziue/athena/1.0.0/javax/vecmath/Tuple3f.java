package javax.vecmath;

import java.io.*;

public abstract class Tuple3f implements Serializable, Cloneable
{
    static final long serialVersionUID = 5019834619484343712L;
    public float x;
    public float y;
    public float z;
    
    public Tuple3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Tuple3f(final float[] t) {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
    }
    
    public Tuple3f(final Tuple3f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }
    
    public Tuple3f(final Tuple3d t1) {
        this.x = (float)t1.x;
        this.y = (float)t1.y;
        this.z = (float)t1.z;
    }
    
    public Tuple3f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
    
    public final void set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public final void set(final float[] t) {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
    }
    
    public final void set(final Tuple3f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }
    
    public final void set(final Tuple3d t1) {
        this.x = (float)t1.x;
        this.y = (float)t1.y;
        this.z = (float)t1.z;
    }
    
    public final void get(final float[] t) {
        t[0] = this.x;
        t[1] = this.y;
        t[2] = this.z;
    }
    
    public final void get(final Tuple3f t) {
        t.x = this.x;
        t.y = this.y;
        t.z = this.z;
    }
    
    public final void add(final Tuple3f t1, final Tuple3f t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
    }
    
    public final void add(final Tuple3f t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
    }
    
    public final void sub(final Tuple3f t1, final Tuple3f t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
    }
    
    public final void sub(final Tuple3f t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
    }
    
    public final void negate(final Tuple3f t1) {
        this.x = -t1.x;
        this.y = -t1.y;
        this.z = -t1.z;
    }
    
    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }
    
    public final void scale(final float s, final Tuple3f t1) {
        this.x = s * t1.x;
        this.y = s * t1.y;
        this.z = s * t1.z;
    }
    
    public final void scale(final float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }
    
    public final void scaleAdd(final float s, final Tuple3f t1, final Tuple3f t2) {
        this.x = s * t1.x + t2.x;
        this.y = s * t1.y + t2.y;
        this.z = s * t1.z + t2.z;
    }
    
    public final void scaleAdd(final float s, final Tuple3f t1) {
        this.x = s * this.x + t1.x;
        this.y = s * this.y + t1.y;
        this.z = s * this.z + t1.z;
    }
    
    public boolean equals(final Tuple3f t1) {
        try {
            return this.x == t1.x && this.y == t1.y && this.z == t1.z;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    @Override
    public boolean equals(final Object t1) {
        try {
            final Tuple3f t2 = (Tuple3f)t1;
            return this.x == t2.x && this.y == t2.y && this.z == t2.z;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e3) {
            return false;
        }
    }
    
    public boolean epsilonEquals(final Tuple3f t1, final float epsilon) {
        float diff = this.x - t1.x;
        if (Float.isNaN(diff)) {
            return false;
        }
        if (((diff < 0.0f) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.y - t1.y;
        if (Float.isNaN(diff)) {
            return false;
        }
        if (((diff < 0.0f) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.z - t1.z;
        return !Float.isNaN(diff) && ((diff < 0.0f) ? (-diff) : diff) <= epsilon;
    }
    
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.x);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.y);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.z);
        return (int)(bits ^ bits >> 32);
    }
    
    public final void clamp(final float min, final float max, final Tuple3f t) {
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
    
    public final void clampMin(final float min, final Tuple3f t) {
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
    
    public final void clampMax(final float max, final Tuple3f t) {
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
    
    public final void absolute(final Tuple3f t) {
        this.x = Math.abs(t.x);
        this.y = Math.abs(t.y);
        this.z = Math.abs(t.z);
    }
    
    public final void clamp(final float min, final float max) {
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
    
    public final void clampMin(final float min) {
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
    
    public final void clampMax(final float max) {
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
    
    public final void interpolate(final Tuple3f t1, final Tuple3f t2, final float alpha) {
        this.x = (1.0f - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0f - alpha) * t1.y + alpha * t2.y;
        this.z = (1.0f - alpha) * t1.z + alpha * t2.z;
    }
    
    public final void interpolate(final Tuple3f t1, final float alpha) {
        this.x = (1.0f - alpha) * this.x + alpha * t1.x;
        this.y = (1.0f - alpha) * this.y + alpha * t1.y;
        this.z = (1.0f - alpha) * this.z + alpha * t1.z;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
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
