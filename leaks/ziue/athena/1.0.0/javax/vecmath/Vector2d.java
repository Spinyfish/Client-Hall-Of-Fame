package javax.vecmath;

import java.io.*;

public class Vector2d extends Tuple2d implements Serializable
{
    static final long serialVersionUID = 8572646365302599857L;
    
    public Vector2d(final double x, final double y) {
        super(x, y);
    }
    
    public Vector2d(final double[] v) {
        super(v);
    }
    
    public Vector2d(final Vector2d v1) {
        super(v1);
    }
    
    public Vector2d(final Vector2f v1) {
        super(v1);
    }
    
    public Vector2d(final Tuple2d t1) {
        super(t1);
    }
    
    public Vector2d(final Tuple2f t1) {
        super(t1);
    }
    
    public Vector2d() {
    }
    
    public final double dot(final Vector2d v1) {
        return this.x * v1.x + this.y * v1.y;
    }
    
    public final double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    public final double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }
    
    public final void normalize(final Vector2d v1) {
        final double norm = 1.0 / Math.sqrt(v1.x * v1.x + v1.y * v1.y);
        this.x = v1.x * norm;
        this.y = v1.y * norm;
    }
    
    public final void normalize() {
        final double norm = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y);
        this.x *= norm;
        this.y *= norm;
    }
    
    public final double angle(final Vector2d v1) {
        double vDot = this.dot(v1) / (this.length() * v1.length());
        if (vDot < -1.0) {
            vDot = -1.0;
        }
        if (vDot > 1.0) {
            vDot = 1.0;
        }
        return Math.acos(vDot);
    }
}
