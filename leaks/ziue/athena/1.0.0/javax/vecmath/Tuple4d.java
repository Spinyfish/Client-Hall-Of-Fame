package javax.vecmath;

import java.io.*;

public abstract class Tuple4d implements Serializable, Cloneable
{
    static final long serialVersionUID = -4748953690425311052L;
    public double x;
    public double y;
    public double z;
    public double w;
    
    public Tuple4d(final double x, final double y, final double z, final double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Tuple4d(final double[] t) {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
        this.w = t[3];
    }
    
    public Tuple4d(final Tuple4d t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }
    
    public Tuple4d(final Tuple4f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
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
    
    @Deprecated
    public final void scaleAdd(final float s, final Tuple4d t1) {
        this.scaleAdd((double)s, t1);
    }
    
    public final void scaleAdd(final double s, final Tuple4d t1) {
        this.x = s * this.x + t1.x;
        this.y = s * this.y + t1.y;
        this.z = s * this.z + t1.z;
        this.w = s * this.w + t1.w;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }
    
    public boolean equals(final Tuple4d t1) {
        try {
            return this.x == t1.x && this.y == t1.y && this.z == t1.z && this.w == t1.w;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    @Override
    public boolean equals(final Object t1) {
        try {
            final Tuple4d t2 = (Tuple4d)t1;
            return this.x == t2.x && this.y == t2.y && this.z == t2.z && this.w == t2.w;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e3) {
            return false;
        }
    }
    
    public boolean epsilonEquals(final Tuple4d t1, final double epsilon) {
        double diff = this.x - t1.x;
        if (Double.isNaN(diff)) {
            return false;
        }
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.y - t1.y;
        if (Double.isNaN(diff)) {
            return false;
        }
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.z - t1.z;
        if (Double.isNaN(diff)) {
            return false;
        }
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.w - t1.w;
        return !Double.isNaN(diff) && ((diff < 0.0) ? (-diff) : diff) <= epsilon;
    }
    
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.x);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.y);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.z);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.w);
        return (int)(bits ^ bits >> 32);
    }
    
    @Deprecated
    public final void clamp(final float min, final float max, final Tuple4d t) {
        this.clamp(min, (double)max, t);
    }
    
    public final void clamp(final double min, final double max, final Tuple4d t) {
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
        if (t.w > max) {
            this.w = max;
        }
        else if (t.w < min) {
            this.w = min;
        }
        else {
            this.w = t.w;
        }
    }
    
    @Deprecated
    public final void clampMin(final float min, final Tuple4d t) {
        this.clampMin((double)min, t);
    }
    
    public final void clampMin(final double min, final Tuple4d t) {
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
        if (t.w < min) {
            this.w = min;
        }
        else {
            this.w = t.w;
        }
    }
    
    @Deprecated
    public final void clampMax(final float max, final Tuple4d t) {
        this.clampMax((double)max, t);
    }
    
    public final void clampMax(final double max, final Tuple4d t) {
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
        if (t.w > max) {
            this.w = max;
        }
        else {
            this.w = t.z;
        }
    }
    
    public final void absolute(final Tuple4d t) {
        this.x = Math.abs(t.x);
        this.y = Math.abs(t.y);
        this.z = Math.abs(t.z);
        this.w = Math.abs(t.w);
    }
    
    @Deprecated
    public final void clamp(final float min, final float max) {
        this.clamp(min, (double)max);
    }
    
    public final void clamp(final double min, final double max) {
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
        if (this.w > max) {
            this.w = max;
        }
        else if (this.w < min) {
            this.w = min;
        }
    }
    
    @Deprecated
    public final void clampMin(final float min) {
        this.clampMin((double)min);
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
    
    @Deprecated
    public final void clampMax(final float max) {
        this.clampMax((double)max);
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
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        this.w = Math.abs(this.w);
    }
    
    @Deprecated
    public void interpolate(final Tuple4d t1, final Tuple4d t2, final float alpha) {
        this.interpolate(t1, t2, (double)alpha);
    }
    
    public void interpolate(final Tuple4d t1, final Tuple4d t2, final double alpha) {
        this.x = (1.0 - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0 - alpha) * t1.y + alpha * t2.y;
        this.z = (1.0 - alpha) * t1.z + alpha * t2.z;
        this.w = (1.0 - alpha) * t1.w + alpha * t2.w;
    }
    
    @Deprecated
    public void interpolate(final Tuple4d t1, final float alpha) {
        this.interpolate(t1, (double)alpha);
    }
    
    public void interpolate(final Tuple4d t1, final double alpha) {
        this.x = (1.0 - alpha) * this.x + alpha * t1.x;
        this.y = (1.0 - alpha) * this.y + alpha * t1.y;
        this.z = (1.0 - alpha) * this.z + alpha * t1.z;
        this.w = (1.0 - alpha) * this.w + alpha * t1.w;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    public final double getX() {
        return this.x;
    }
    
    public final void setX(final double x) {
        this.x = x;
    }
    
    public final double getY() {
        return this.y;
    }
    
    public final void setY(final double y) {
        this.y = y;
    }
    
    public final double getZ() {
        return this.z;
    }
    
    public final void setZ(final double z) {
        this.z = z;
    }
    
    public final double getW() {
        return this.w;
    }
    
    public final void setW(final double w) {
        this.w = w;
    }
}
