package javax.vecmath;

import java.io.*;

public class Vector2f extends Tuple2f implements Serializable
{
    static final long serialVersionUID = -2168194326883512320L;
    
    public Vector2f(final float x, final float y) {
        super(x, y);
    }
    
    public Vector2f(final float[] v) {
        super(v);
    }
    
    public Vector2f(final Vector2f v1) {
        super(v1);
    }
    
    public Vector2f(final Vector2d v1) {
        super(v1);
    }
    
    public Vector2f(final Tuple2f t1) {
        super(t1);
    }
    
    public Vector2f(final Tuple2d t1) {
        super(t1);
    }
    
    public Vector2f() {
    }
    
    public final float dot(final Vector2f v1) {
        return this.x * v1.x + this.y * v1.y;
    }
    
    public final float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    public final float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }
    
    public final void normalize(final Vector2f v1) {
        final float norm = (float)(1.0 / Math.sqrt(v1.x * v1.x + v1.y * v1.y));
        this.x = v1.x * norm;
        this.y = v1.y * norm;
    }
    
    public final void normalize() {
        final float norm = (float)(1.0 / Math.sqrt(this.x * this.x + this.y * this.y));
        this.x *= norm;
        this.y *= norm;
    }
    
    public final float angle(final Vector2f v1) {
        double vDot = this.dot(v1) / (this.length() * v1.length());
        if (vDot < -1.0) {
            vDot = -1.0;
        }
        if (vDot > 1.0) {
            vDot = 1.0;
        }
        return (float)Math.acos(vDot);
    }
}
