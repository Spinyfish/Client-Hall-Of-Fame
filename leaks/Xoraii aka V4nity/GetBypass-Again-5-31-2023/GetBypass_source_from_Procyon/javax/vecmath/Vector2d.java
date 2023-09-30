// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Vector2d extends Tuple2d implements Serializable
{
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
        return super.x * v1.x + super.y * v1.y;
    }
    
    public final double length() {
        return Math.sqrt(super.x * super.x + super.y * super.y);
    }
    
    public final double lengthSquared() {
        return super.x * super.x + super.y * super.y;
    }
    
    public final void normalize() {
        final double d = this.length();
        super.x /= d;
        super.y /= d;
    }
    
    public final void normalize(final Vector2d v1) {
        this.set(v1);
        this.normalize();
    }
    
    public final double angle(final Vector2d v1) {
        return Math.abs(Math.atan2(super.x * v1.y - super.y * v1.x, this.dot(v1)));
    }
}
