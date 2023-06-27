package javax.vecmath;

import java.io.*;

public abstract class Tuple2d implements Serializable, Cloneable
{
    static final long serialVersionUID = 6205762482756093838L;
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
    
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.x);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.y);
        return (int)(bits ^ bits >> 32);
    }
    
    public boolean equals(final Tuple2d t1) {
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
            final Tuple2d t2 = (Tuple2d)t1;
            return this.x == t2.x && this.y == t2.y;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e3) {
            return false;
        }
    }
    
    public boolean epsilonEquals(final Tuple2d t1, final double epsilon) {
        double diff = this.x - t1.x;
        if (Double.isNaN(diff)) {
            return false;
        }
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.y - t1.y;
        return !Double.isNaN(diff) && ((diff < 0.0) ? (-diff) : diff) <= epsilon;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
    
    public final void clamp(final double min, final double max, final Tuple2d t) {
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
    
    public final void clampMin(final double min, final Tuple2d t) {
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
    
    public final void clampMax(final double max, final Tuple2d t) {
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
    
    public final void absolute(final Tuple2d t) {
        this.x = Math.abs(t.x);
        this.y = Math.abs(t.y);
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
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
    }
    
    public final void interpolate(final Tuple2d t1, final Tuple2d t2, final double alpha) {
        this.x = (1.0 - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0 - alpha) * t1.y + alpha * t2.y;
    }
    
    public final void interpolate(final Tuple2d t1, final double alpha) {
        this.x = (1.0 - alpha) * this.x + alpha * t1.x;
        this.y = (1.0 - alpha) * this.y + alpha * t1.y;
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
}
