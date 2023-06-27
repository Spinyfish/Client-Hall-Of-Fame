package javax.vecmath;

import java.io.*;

public abstract class Tuple2i implements Serializable, Cloneable
{
    static final long serialVersionUID = -3555701650170169638L;
    public int x;
    public int y;
    
    public Tuple2i(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public Tuple2i(final int[] t) {
        this.x = t[0];
        this.y = t[1];
    }
    
    public Tuple2i(final Tuple2i t1) {
        this.x = t1.x;
        this.y = t1.y;
    }
    
    public Tuple2i() {
        this.x = 0;
        this.y = 0;
    }
    
    public final void set(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public final void set(final int[] t) {
        this.x = t[0];
        this.y = t[1];
    }
    
    public final void set(final Tuple2i t1) {
        this.x = t1.x;
        this.y = t1.y;
    }
    
    public final void get(final int[] t) {
        t[0] = this.x;
        t[1] = this.y;
    }
    
    public final void get(final Tuple2i t) {
        t.x = this.x;
        t.y = this.y;
    }
    
    public final void add(final Tuple2i t1, final Tuple2i t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
    }
    
    public final void add(final Tuple2i t1) {
        this.x += t1.x;
        this.y += t1.y;
    }
    
    public final void sub(final Tuple2i t1, final Tuple2i t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
    }
    
    public final void sub(final Tuple2i t1) {
        this.x -= t1.x;
        this.y -= t1.y;
    }
    
    public final void negate(final Tuple2i t1) {
        this.x = -t1.x;
        this.y = -t1.y;
    }
    
    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }
    
    public final void scale(final int s, final Tuple2i t1) {
        this.x = s * t1.x;
        this.y = s * t1.y;
    }
    
    public final void scale(final int s) {
        this.x *= s;
        this.y *= s;
    }
    
    public final void scaleAdd(final int s, final Tuple2i t1, final Tuple2i t2) {
        this.x = s * t1.x + t2.x;
        this.y = s * t1.y + t2.y;
    }
    
    public final void scaleAdd(final int s, final Tuple2i t1) {
        this.x = s * this.x + t1.x;
        this.y = s * this.y + t1.y;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
    
    @Override
    public boolean equals(final Object t1) {
        try {
            final Tuple2i t2 = (Tuple2i)t1;
            return this.x == t2.x && this.y == t2.y;
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
        return (int)(bits ^ bits >> 32);
    }
    
    public final void clamp(final int min, final int max, final Tuple2i t) {
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
    
    public final void clampMin(final int min, final Tuple2i t) {
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
    
    public final void clampMax(final int max, final Tuple2i t) {
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
    
    public final void absolute(final Tuple2i t) {
        this.x = Math.abs(t.x);
        this.y = Math.abs(t.y);
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
    }
    
    public final void clampMin(final int min) {
        if (this.x < min) {
            this.x = min;
        }
        if (this.y < min) {
            this.y = min;
        }
    }
    
    public final void clampMax(final int max) {
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
}
