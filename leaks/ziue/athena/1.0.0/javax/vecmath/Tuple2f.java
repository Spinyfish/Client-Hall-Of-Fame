package javax.vecmath;

import java.io.*;

public abstract class Tuple2f implements Serializable, Cloneable
{
    static final long serialVersionUID = 9011180388985266884L;
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
    
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.x);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.y);
        return (int)(bits ^ bits >> 32);
    }
    
    public boolean equals(final Tuple2f t1) {
        try {
            return this.x == t1.x && this.y == t1.y;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    @Override
    public boolean equals(final Object t1) {
        try {
            final Tuple2f t2 = (Tuple2f)t1;
            return this.x == t2.x && this.y == t2.y;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e3) {
            return false;
        }
    }
    
    public boolean epsilonEquals(final Tuple2f t1, final float epsilon) {
        float diff = this.x - t1.x;
        if (Float.isNaN(diff)) {
            return false;
        }
        if (((diff < 0.0f) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.y - t1.y;
        return !Float.isNaN(diff) && ((diff < 0.0f) ? (-diff) : diff) <= epsilon;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
    
    public final void clamp(final float min, final float max, final Tuple2f t) {
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
    }
    
    public final void clampMin(final float min, final Tuple2f t) {
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
    }
    
    public final void clampMax(final float max, final Tuple2f t) {
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
    }
    
    public final void absolute(final Tuple2f t) {
        this.x = Math.abs(t.x);
        this.y = Math.abs(t.y);
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
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
    }
    
    public final void interpolate(final Tuple2f t1, final Tuple2f t2, final float alpha) {
        this.x = (1.0f - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0f - alpha) * t1.y + alpha * t2.y;
    }
    
    public final void interpolate(final Tuple2f t1, final float alpha) {
        this.x = (1.0f - alpha) * this.x + alpha * t1.x;
        this.y = (1.0f - alpha) * this.y + alpha * t1.y;
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
}
